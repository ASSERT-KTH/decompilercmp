package se.kth.decompiler;

import se.kth.Decompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Krakatau implements Decompiler {
	@Override
	public boolean decompile(File in, File outDir, String cl) {
		String path = in.getAbsolutePath().replace(cl + ".class", "");

		String mvnHome = null;
		try {
			String[] cmd = new String[]{"python2.7", "lib//Krakatau/decompile.py", "-out", outDir.getAbsolutePath(), "-path", path, cl};

			Process p = Runtime.getRuntime().exec(cmd);

			try (BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				String line;

				while ((line = output.readLine()) != null) {
					System.out.println(line);
				}
			}

			try (BufferedReader output = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
				String line;

				while ((line = output.readLine()) != null) {
					System.out.println(line);
				}
			}

			p.waitFor();

			return p.exitValue() == 0;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	@Override
	public String getName() {
		return "Krakatau";
	}
}
