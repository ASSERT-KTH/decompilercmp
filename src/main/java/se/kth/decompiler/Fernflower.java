package se.kth.decompiler;

import se.kth.Decompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Fernflower implements Decompiler {
	@Override
	public boolean decompile(File in, File outDir, String cl, String[] classpath) {
		String dir = cl.substring(0,cl.lastIndexOf("/"));
		File o = new File(outDir,dir);
		try {
			String[] cmd = new String[]{
					"java", "-jar", "lib/fernflower.jar",
					in.getAbsolutePath(),
					o.getAbsolutePath()
			};

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
		return "Fernflower";
	}

	/*@Override
	public boolean decompile(File in, File outDir, String cl) {
		String dir = cl.substring(0,cl.lastIndexOf("/"));
		File o = new File(outDir,dir);
		ConsoleDecompiler.main(new String[]{in.getAbsolutePath(), o.getPath()});
		return true;
	}

	@Override
	public String getName() {
		return "Fernflower-2.5.0.Final";
	}*/

}
