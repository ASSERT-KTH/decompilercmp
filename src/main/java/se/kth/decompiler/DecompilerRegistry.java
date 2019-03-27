package se.kth.decompiler;

import se.kth.Decompiler;

import java.util.HashMap;
import java.util.Map;

public class DecompilerRegistry {
	public static Map<String, Decompiler> decompilers;
	static {
		decompilers = new HashMap<>();
		decompilers.put("CFR-0.141", new CFR());
		decompilers.put("Fernflower-2.5.0.Final", new Fernflower());
		decompilers.put("Krakatau", new Krakatau());
	}
}
