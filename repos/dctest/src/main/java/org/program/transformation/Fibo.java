/* Fibo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.program.transformation;

public class Fibo {
	private static int fib (int x) {
		if (x > 1)
			return (fib(x - 1) + fib(x - 2));
		else return (x);
	}

	public static void main(String args[]) throws Exception {
		int number = 0, value;

		try {
			number = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println ("Input error");
			System.exit (1);
		}
		value = fib(number);
		System.out.println ("fibonacci(" + number + ") = " + value);
	}

}
