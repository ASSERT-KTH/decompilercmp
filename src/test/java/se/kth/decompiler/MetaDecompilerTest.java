package se.kth.decompiler;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class MetaDecompilerTest {
	public static File testDir = new File("tmp-test-dir");

	@Before
	public void prepare() {
		if(testDir.exists()) {
			testDir.delete();
		}
		testDir.mkdirs();

	}

	@After
	public void clean() {
		if(testDir.exists()) {
			testDir.delete();
		}
	}

	@Test
	public void testDecomileFieldError() {
		File srcA = new File("src/test/resources/decompiled/a/FieldA.java");
		File srcB = new File("src/test/resources/decompiled/b/FieldA.java");
		testDecompile(srcA, srcB, "FieldA");
	}

	@Test
	public void testDecomileMethodError() {
		File srcA = new File("src/test/resources/decompiled/a/MethodA.java");
		File srcB = new File("src/test/resources/decompiled/b/MethodA.java");
		testDecompile(srcA, srcB, "MethodA");
	}

	@Test
	public void testDecomileInnerError() {
		File srcA = new File("src/test/resources/decompiled/a/Outer.java");
		File srcB = new File("src/test/resources/decompiled/b/Outer.java");
		testDecompile(srcA, srcB, "Outer");
	}


	public void testDecompile(File srcA, File srcB, String className) {

		//File srcA = new File("src/test/resources/decompiled/a/FieldA.java");
		//File srcB = new File("src/test/resources/decompiled/b/FieldA.java");
		MetaDecompiler md = new MetaDecompiler(
				Arrays.asList(
						new DecompilerFacade(srcA, srcA, false, "A"),
						new DecompilerFacade(srcA, srcB, false, "B")
				), true
		);

		boolean success = md.decompile(srcA, testDir, className, new String[0]);
		assertTrue(success);
	}
}
