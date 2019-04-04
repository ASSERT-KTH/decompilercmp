package org.program.transformation;

import org.junit.Test;

import static org.junit.Assert.*;

public class LambdaTest {

	@Test
	public void testLambda() {
		Lambda l = new Lambda();
		assertEquals(l.printDouble(), "2,4,6");
	}

}