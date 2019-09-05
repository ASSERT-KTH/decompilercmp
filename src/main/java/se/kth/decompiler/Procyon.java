package se.kth.decompiler;

import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import org.apache.commons.io.FileUtils;
import se.kth.Decompiler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Procyon implements Decompiler {
	@Override
	public boolean decompile(File in, File outDir, String cl, String[] classpath) {
		try {
			FileUtils.write(new File(outDir,cl + ".java"), decompileClassNode(in));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		//com.strobel.decompiler.Decompiler.decompile();
		return false;
	}

	@Override
	public String getName() {
		return "Procyon-0.5.34";
	}

	//Shamelessly stealing wrapper from https://github.com/Konloch/bytecode-viewer
	public String decompileClassNode(File in) {
		String exception = "";
		try {

			final File tempClass = in;

			/*try {
				final FileOutputStream fos = new FileOutputStream(tempClass);

				fos.write(b);

				fos.close();
			} catch (final IOException e) {
			}*/

			DecompilerSettings settings = DecompilerSettings.javaDefaults();

			LuytenTypeLoader typeLoader = new LuytenTypeLoader();
			MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
			TypeReference type = metadataSystem.lookupType(tempClass
					.getCanonicalPath());

			DecompilationOptions decompilationOptions = new DecompilationOptions();
			decompilationOptions.setSettings(DecompilerSettings.javaDefaults());
			decompilationOptions.setFullDecompilation(true);

			TypeDefinition resolvedType = null;
			if (type == null || ((resolvedType = type.resolve()) == null)) {
				throw new Exception("Unable to resolve type.");
			}
			StringWriter stringwriter = new StringWriter();
			settings.getLanguage().decompileType(resolvedType,
					new PlainTextOutput(stringwriter), decompilationOptions);
			String decompiledSource = stringwriter.toString();

			return decompiledSource;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public final class LuytenTypeLoader implements ITypeLoader {
		private final List<ITypeLoader> _typeLoaders;

		public LuytenTypeLoader() {
			_typeLoaders = new ArrayList<ITypeLoader>();
			_typeLoaders.add(new InputTypeLoader());
		}

		public final List<ITypeLoader> getTypeLoaders() {
			return _typeLoaders;
		}

		@Override
		public boolean tryLoadType(final String internalName,
		                           final Buffer buffer) {
			for (final ITypeLoader typeLoader : _typeLoaders) {
				if (typeLoader.tryLoadType(internalName, buffer)) {
					return true;
				}

				buffer.reset();
			}

			return false;
		}
	}
}
