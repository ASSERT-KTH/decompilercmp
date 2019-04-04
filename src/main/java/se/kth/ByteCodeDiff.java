package se.kth;


import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ByteCodeDiff {
	public static int compare(File originalByteCode, File nByteCode) {
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"java",
					"-jar", "lib/jardiff.jar",
					originalByteCode.getAbsolutePath(),
					nByteCode.getAbsolutePath()
			);
			//pb.directory(new File("lib/jadx-0.9.0/bin"));

			Process p = pb.start();

			int distance = 0;

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ( (line = reader.readLine()) != null) {
				if(line.startsWith("+ ") || line.startsWith("- ")) distance++;
			}



			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			StringBuilder errorBuilder = new StringBuilder();
			String errorLine = null;
			while ( (errorLine = errorReader.readLine()) != null) {
				errorBuilder.append(errorLine);
				errorBuilder.append(System.getProperty("line.separator"));
			}
			String error = errorBuilder.toString();
			System.err.println(error);

			int r = p.waitFor();
			if(r==0 || r==1) return distance;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return Integer.MIN_VALUE;
	}
}
