package org.program.transformation;

import org.junit.Test;

import static org.junit.Assert.*;

public class UsaTest {

	@Test
	public void testUsa() {
		assertTrue(TestUtil.testMain("org.program.transformation.Usa", new String[]{},"Detroit\n" +
				"London\n" +
				"Dublin\n" +
				"Dublin\n"));
	}

}