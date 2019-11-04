package se.kth.asm.testclasses;

import java.util.List;

public class A<T extends List<String>> extends Abs implements I {
	T obj;
	public T getObj() {
		return obj;
	}

	@Override
	public int absM(String s) {
		return 0;
	}

	@Override
	public List<Integer> intM(Integer i) {
		return null;
	}

	protected static class B {
		int f;
	}
}
