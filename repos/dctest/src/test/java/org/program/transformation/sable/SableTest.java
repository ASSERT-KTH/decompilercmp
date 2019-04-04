package org.program.transformation.sable;

import org.junit.Test;
import org.program.transformation.TestUtil;

import static org.junit.Assert.*;

public class SableTest {

	@Test
	public void test() {
		assertTrue(TestUtil.testMain("org.program.transformation.sable.Main",new String[]{},""));
	}

}