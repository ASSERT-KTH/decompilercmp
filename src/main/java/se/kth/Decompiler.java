package se.kth;

import java.io.File;

public interface Decompiler {
	boolean decompile(File in, File outDir, String cl, String[] classpath);
	String getName();
}
