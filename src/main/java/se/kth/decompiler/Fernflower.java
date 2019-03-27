package se.kth.decompiler;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import se.kth.Decompiler;

import java.io.File;

public class Fernflower implements Decompiler {
	@Override
	public boolean decompile(File in, File outDir, String cl) {
		String dir = cl.substring(0,cl.lastIndexOf("/"));
		File o = new File(outDir,dir);
		ConsoleDecompiler.main(new String[]{in.getAbsolutePath(), o.getPath()});
		return true;
	}

	@Override
	public String getName() {
		return "Fernflower-2.5.0.Final";
	}
}
