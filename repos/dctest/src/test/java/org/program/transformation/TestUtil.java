package org.program.transformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestUtil {
	public static boolean testMain(String cl, String[] args, String expected) {
		return testMain(cl, args, expected, 0);
	}

	public static boolean testMain(String cl, String[] args, String expected, int expectedStatus) {
		String[] cmd = new String[args.length+4];
		cmd[0] = "java";
		cmd[1] = "-cp";
		cmd[2] = "target/classes";
		cmd[3] = cl;

		for(int i = 0; i < args.length; i++) {
			cmd[i+4] = args[i];
		}

		ProcessBuilder pb = new ProcessBuilder(cmd);
		try {

			Process p = pb.start();


			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();

			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			StringBuilder errorBuilder = new StringBuilder();
			String errorLine = null;
			while ( (errorLine = errorReader.readLine()) != null) {
				errorBuilder.append(errorLine);
				errorBuilder.append(System.getProperty("line.separator"));
			}
			String error = errorBuilder.toString();

			int r = p.waitFor();
			if(r != expectedStatus) return false;
			return result.compareTo(expected) == 0 && error.length() == 0;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}
}
