package se.kth.asm;

import org.junit.Ignore;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class ClassAPIVisitorTest {

	@Test
	public void testReadAAPI() throws IOException {

		Map<String, List<CtTypeMember>> typeMembers;

		final Launcher launcherOutput = new Launcher();
		ClassAPIVisitor cav = ClassAPIVisitor.readClass(
				new File("./target/test-classes/se/kth/asm/testclasses/A.class"),
				launcherOutput.getFactory());
		CtClass outputClass = cav.toBuild;
		typeMembers = cav.classApi;

		assertEquals(6, typeMembers.size());
	}

	@Test
	public void testReadBAPI() throws IOException {
		/*Launcher launcher = new Launcher();

		File dir = new File("./target/test-classes/");
		String className = "se.kth.asm.testclasses.B";
		ClassAPIVisitor api = ClassAPIVisitor.readClass(dir,className, launcher.getFactory());
		//assertEquals(5, api.classApi.size());
		//assertTrue(ap);

		launcher.addInputResource("./src/test/java/se/kth/asm/testclasses/B.java");
		launcher.buildModel();
		System.out.println("Done");*/
	}

	@Test
	public void testReadAPIs() throws IOException {
		compareSpoonAndBytecode(new File("src/test/java/se/kth/asm/testclasses/A.java"),
				new File("target/test-classes/se/kth/asm/testclasses/A.class"),
				"se.kth.asm.testclasses.A");
		compareSpoonAndBytecode(new File("src/test/java/se/kth/asm/testclasses/B.java"),
				new File("target/test-classes/se/kth/asm/testclasses/B.class"),
				"se.kth.asm.testclasses.B");
		compareSpoonAndBytecode(new File("src/test/java/se/kth/asm/testclasses/C.java"),
				new File("target/test-classes/se/kth/asm/testclasses/C.class"),
				"se.kth.asm.testclasses.C");
		compareSpoonAndBytecode(new File("src/test/java/se/kth/asm/testclasses/D.java"),
				new File("target/test-classes/se/kth/asm/testclasses/D.class"),
				"se.kth.asm.testclasses.D");
		compareSpoonAndBytecode(new File("src/test/java/se/kth/asm/testclasses/E.java"),
				new File("target/test-classes/se/kth/asm/testclasses/E.class"),
				"se.kth.asm.testclasses.E");
		compareSpoonAndBytecode(new File("src/test/java/se/kth/asm/testclasses/F.java"),
				new File("target/test-classes/se/kth/asm/testclasses/F.class"),
				"se.kth.asm.testclasses.F");
		compareSpoonAndBytecode(new File("src/test/java/se/kth/asm/testclasses/G.java"),
				new File("target/test-classes/se/kth/asm/testclasses/G.class"),
				"se.kth.asm.testclasses.G");
	}

	@Ignore
	@Test
	public void testProblem() throws IOException {
		File srcs = new File("src/test/resources/problems/Sources");
		File bytecode = new File("src/test/resources/problems/Bytecode");
		File classNames = new File("src/test/resources/problems/ClassNames");

		Scanner scannerSrcs = new Scanner(srcs);
		Scanner scannerBc = new Scanner(bytecode);
		Scanner scannerCn = new Scanner(classNames);
		while (scannerSrcs.hasNextLine() && scannerBc.hasNextLine() && scannerCn.hasNextLine()) {
			String src = scannerSrcs.nextLine();
			String bc = scannerBc.nextLine();
			String cn = scannerCn.nextLine();
			compareSpoonAndBytecode(new File(src),
					new File(bc),
					cn);
		}
	}



	public void compareSpoonAndBytecode(File src, File bytecode, String className) throws IOException {
		//Read with Class
		Launcher launcherA = new Launcher();
		ClassAPIVisitor api = ClassAPIVisitor.readClass(bytecode, launcherA.getFactory());

		Launcher launcherB = new Launcher();
		launcherB.addInputResource(src.getAbsolutePath());
		launcherB.buildModel();
		CtType t = launcherB.getFactory().Type().get(className);
		Set<String> spoonKeys = readWithSpoon(t);

		if(api.classApi.containsKey("<clinit>()") && !spoonKeys.contains("<clinit>()")) {
			spoonKeys.add("<clinit>()");
		}

		for(String key :api.classApi.keySet()) {
			assertTrue("Key: \"" + key + " is missing from spoon.", spoonKeys.contains(key));
		}
		assertEquals(spoonKeys.size(), api.classApi.size());

		assertEquals(t.getFormalCtTypeParameters().size(), api.toBuild.getFormalCtTypeParameters().size());
		for(int i = 0; i < t.getFormalCtTypeParameters().size(); i++) {
			CtTypeParameter tpSpoon =  t.getFormalCtTypeParameters().get(i);
			CtTypeParameter tpByteCode = api.toBuild.getFormalCtTypeParameters().get(i);
			assertEquals(tpSpoon.getSimpleName(), tpByteCode.getSimpleName());
			assertEquals(tpSpoon.getReference().getBoundingType().getQualifiedName(),
					tpByteCode.getReference().getBoundingType().getQualifiedName());
		}
	}

	public Set<String> readWithSpoon(CtType t) {
		Set<String> results = new HashSet<>();
		for(Object obj : t.getTypeMembers()) {
			CtTypeMember tm = (CtTypeMember) obj;
			String key;
			if (tm instanceof CtField) {
				key = tm.getParent(CtType.class).getQualifiedName() + "#" + tm.getSimpleName();
			} else if (tm instanceof CtType) {
				key = ((CtType) tm).getQualifiedName();
			} else {
				key = ((CtExecutable) tm).getSignature();
			}
			if(key != null)
				results.add(key);
		}
		return results;
	}
}
