package org.program.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lambda {
	List<Integer> l = new ArrayList<>();
	public Lambda() {
		l.add(1);
		l.add(2);
		l.add(3);
	}

	public String printDouble() {
		return l.stream().map(i -> 2 * i).map(i -> i.toString()).collect(Collectors.joining(","));
	}
}
