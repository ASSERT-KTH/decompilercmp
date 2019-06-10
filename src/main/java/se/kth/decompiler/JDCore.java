package se.kth.decompiler;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;
import se.kth.Decompiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JDCore implements Decompiler {

	@Override
	public String getName() {
		return "JD-Core-1.0.0";
	}

	public boolean decompile(File in, File out, String cl) {
		Boolean result = null;
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Boolean> task = () -> decompileAttempt(in, out, cl);
		Future<Boolean> future = executor.submit(task);
		try {
			result = future.get(3*60, TimeUnit.SECONDS);
		} catch (TimeoutException | InterruptedException | ExecutionException e) {
			System.err.println(getName() + " failed with e: " + e.getMessage());
		} finally {
			future.cancel(true); // may or may not desire this
		}
		if(result != null) return result;
		else return false;
	}


	public boolean decompileAttempt(File in, File out, String cl) {
		try {
			final File tempJava = new File(out, cl + ".java");
			File inBase = new File(in.getAbsolutePath().replace(cl + ".class", ""));

			//System.out.println("[DEBUG] inBase: " + inBase.getAbsolutePath());

			Loader loader = new Loader() {
				@Override
				public byte[] load(String s) throws LoaderException {
					//System.err.println("[RE] load ? " + s);
					File file;
					if(s.startsWith("/"))
						file = new File(s);
					else
						file = new File(inBase, s + ".class");

					try {
						return FileUtils.readFileToByteArray(file);
					} catch (IOException e) {
						throw new LoaderException(
								"'" + file.getAbsolutePath() + "'  not found.");
					}
				}

				@Override
				public boolean canLoad(String s) {
					//System.err.println("[RE] canLoad ? " + s);
					File file = new File(inBase, s + ".class");
					return file.exists() && file.isFile();
				}
			};

			PrintStream ps = new PrintStream(tempJava.getAbsolutePath());
			Printer printer = new TextPrinter(ps);

			//System.err.println("JDGUI out: " + tempJava.getAbsolutePath());

			org.jd.core.v1.ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
			decompiler.decompile(new HashMap<>(), loader, printer, cl);

			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println(getName() + " failed with e: " + e.getMessage());
		}
		return false;
	}

	class TextPrinter implements Printer {
		PrintStream ps;
		protected int indentationCount;
		protected StringBuilder sb;
		protected int textAreaLineNumber;

		public TextPrinter(PrintStream ps) {
			this.sb = new StringBuilder();
			this.textAreaLineNumber = 1;
			this.ps = ps;
		}

		@Override
		public void start(int maxLineNumber, int majorVersion, int minorVersion)
		{
			indentationCount = 0;
		}

		@Override
		public void end() {
			ps.println(sb.toString());
		}

		@Override
		public void printText(String s) {
			sb.append(s);
		}

		@Override
		public void printNumericConstant(String s) {
			sb.append(s);
		}

		@Override
		public void printStringConstant(String constant, String ownerInternalName) {
			sb.append(constant);
		}

		@Override
		public void printKeyword(String s) {
			sb.append(s);
		}

		@Override
		public void printDeclaration(int flags, String internalTypeName, String name, String descriptor) {
			if (DefaultTypeTransformation.booleanUnbox(name))
			{
				this.sb.append(name);
			}
		}

		@Override
		public void printReference(int flags, String internalTypeName, String name, String descriptor, String ownerInternalName) {
			if (DefaultTypeTransformation.booleanUnbox(name)) {
				if (((flags & Printer.TYPE_FLAG) == 0 ? 1 : 0) != 0) {
					getRef(internalTypeName, name, descriptor, ownerInternalName);
				} else {
					getRef(internalTypeName, null, null, ownerInternalName);
				}
				sb.append(name);
			}
		}

		@Override
		public void indent() {
			indentationCount++;
		}

		@Override
		public void unindent() {
			indentationCount--;
			if(indentationCount < 0) indentationCount = 0;
		}

		@Override
		public void startLine(int lineNumber) {
			for (int i = 0; i < indentationCount; i++) {
				sb.append("  ");
			}
		}

		@Override
		public void endLine() {
			textAreaLineNumber++;
			sb.append("\n");
		}

		@Override
		public void extraLine(int count)
		{
			textAreaLineNumber += count;
			for(int i = 0; i < count; i++) {
				sb.append("\n");
			}
		}
		@Override
		public void startMarker(int i) {

		}

		@Override
		public void endMarker(int i) {
		}


		protected HashMap<String, ReferenceData> referencesCache = new HashMap<>();
		public ReferenceData getRef(String internalName, String name, String descriptor, String scopeInternalName) {
			String key = internalName + "-" + name + "-" + descriptor + "-" + scopeInternalName;
			ReferenceData reference = referencesCache.get(key);
			if(reference == null) {
				reference = new ReferenceData(internalName,name,descriptor,scopeInternalName);
				referencesCache.put(key, reference);
			}
			return reference;
		}

		class ReferenceData {
			String internalName;
			String name;
			String descriptor;
			String scopeInternalName;

			public ReferenceData(String internalName, String name, String descriptor, String scopeInternalName) {
				this.internalName = internalName;
				this.name = name;
				this.descriptor = descriptor;
				this.scopeInternalName = scopeInternalName;
			}
		}
	}


}
