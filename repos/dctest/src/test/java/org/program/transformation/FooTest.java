package org.program.transformation;

import org.junit.Test;

import static org.junit.Assert.*;

public class FooTest {

	@Test
	public void foo() {
		Foo f = new Foo();
		assertEquals(f.foo(10,6),6);
	}
}