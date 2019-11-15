package se.kth.decompiler;

import org.apache.commons.io.FileUtils;
import se.kth.Decompiler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DecompilerFacade implements Decompiler {
	Map<File, Map.Entry<File,Boolean>> inouts;
	String name;

	public DecompilerFacade(Map<File, Map.Entry<File,Boolean>> inouts, String name) {
		this.inouts = inouts;
		this.name = name;
	}

	public DecompilerFacade(File in, File out, boolean result, String name) {
		this.inouts = new HashMap<>();
		inouts.put(in, new HashMap.SimpleEntry<>(out, result));
		this.name = name;
	}

	@Override
	public boolean decompile(File in, File outDir, String cl, String[] classpath) {
		if(!inouts.containsKey(in)) return false;
		Map.Entry<File,Boolean> res = inouts.get(in);
		try {
			FileUtils.copyFile(res.getKey(), new File(outDir, cl.replace('.','/') + ".java"));
			return res.getValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getName() {
		return name;
	}
}
