package org.program.transformation;

import org.junit.Test;

import static org.junit.Assert.*;

public class FiboTest {

	@Test
	public void testFibo() throws Exception {
		assertTrue(TestUtil.testMain("org.program.transformation.Fibo", new String[]{"10"},"fibonacci(10) = 55\n"));
		assertTrue(TestUtil.testMain("org.program.transformation.Fibo", new String[]{"abc"},"Input error\n", 1));
	}

}