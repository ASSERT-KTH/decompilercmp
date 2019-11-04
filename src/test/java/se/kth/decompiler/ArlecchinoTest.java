package se.kth.decompiler;

import org.junit.Test;
import se.kth.Decompiler;

import java.io.File;
import java.util.Arrays;

public class ArlecchinoTest {

	@Test
	public void decompile() {
		Decompiler dc = new Arlecchino(
				Arrays.asList(
						new CFR(),
						new Fernflower(),
						new Procyon(),
						new JDCore(),
						new JADX(),
						new Jode(),
						new Dava(),
						new Krakatau()
				)
		);

		File in = new File("target/test-classes/se/kth/asm/testclasses/A.class");
		File out = new File("./target/");

		dc.decompile(in, out, "se/kth/asm/testclasses/A", new String[] {"target/test-classes/se/kth/asm/testclasses"});

		System.out.println("Done");
	}

}
