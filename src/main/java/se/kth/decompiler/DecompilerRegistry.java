package se.kth.decompiler;

import se.kth.Decompiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DecompilerRegistry {
	public static Map<String, Decompiler> decompilers;
	static {
		decompilers = new HashMap<>();
		decompilers.put("CFR-0.141", new CFR());
		//decompilers.put("Fernflower-2.5.0.Final", new Fernflower());
		decompilers.put("Fernflower", new Fernflower());
		decompilers.put("Krakatau", new Krakatau());
		decompilers.put("Procyon-0.5.34", new Procyon());
		decompilers.put("Dava-3.3.0", new Dava());
		decompilers.put("JD-GUI-1.4.1", new JDGui());
		decompilers.put("Jode-1.1.2-pre1", new Jode());
		decompilers.put("JADX-0.9.0", new JADX());

		decompilers.put("JD-Core-1.0.0", new JDCore());

		decompilers.put("Arlecchino",
			new Arlecchino(
				Arrays.asList(
					new Procyon(),
					new CFR(),
					new Fernflower(),
					new JDCore(),
					new JADX(),
					new Jode(),
					new Dava(),
					new Krakatau()
				)
			)
		);

	}
}
