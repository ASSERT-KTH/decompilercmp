package se.kth.decompiler;

import org.benf.cfr.reader.Main;
import se.kth.Decompiler;

import java.io.File;

public class CFR implements Decompiler {
	@Override
	public boolean decompile(File in, File outputDir, String cl, String[] classpath) {
		Main.main(new String[]{in.getAbsolutePath(), "--outputdir", outputDir.getAbsolutePath()});
		return true;
	}

	@Override
	public String getName() {
		return "CFR-0.141";
	}
}
