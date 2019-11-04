package se.kth.asm.testclasses;

public abstract class AbsB {
	public abstract int absM(String s);
	public int inhM(String s) {
		return s.length();
	}
}
