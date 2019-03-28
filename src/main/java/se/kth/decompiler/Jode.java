package se.kth.decompiler;

import jode.decompiler.Main;
import se.kth.Decompiler;

import java.io.File;

public class Jode implements Decompiler {
	@Override
	public boolean decompile(File in, File outDir, String cl) {
		File inBase = new File(in.getAbsolutePath().replace(cl + ".class",""));
		try {
			Main.decompile(new String[]{"-d", outDir.getAbsolutePath(), "-c", inBase.getAbsolutePath(), cl.replace("/", ".")});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "Jode-1.1.2-pre1";
	}
}
