package se.kth.arl;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtTypeMember;

import java.util.List;

public interface Position {
	boolean reachable(CtClass base);
	boolean putAt(CtClass base, CtTypeMember tm);// throws NoSuchInnerClassException;
}
