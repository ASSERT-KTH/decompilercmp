package org.program.transformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TryFinally {

	public static String readFile1(File file) {
		String res = "";
		try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
			String line = null;
			while((line = bf.readLine()) != null) {
				res += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			res += "\n";
		}

		return res;
	}

	public static String readFile2(File file) {
		String res = "";
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = bf.readLine()) != null) {
				res += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			res += "\n";
		}

		return res;
	}

	public static String readFile3(File file) {
		String res = "";
		try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
			String line = null;
			while((line = bf.readLine()) != null) {
				res += line;
			}
			res += "\n";
		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;
	}
}
