package org.program.transformation;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class TryFinallyTest {
	@Test
	public void test() {
		assertEquals("TOTO\n",TryFinally.readFile1(new File("src/test/resources/Test")));
		assertEquals("TOTO\n",TryFinally.readFile2(new File("src/test/resources/Test")));
		assertEquals("TOTO\n",TryFinally.readFile3(new File("src/test/resources/Test")));
	}
}
