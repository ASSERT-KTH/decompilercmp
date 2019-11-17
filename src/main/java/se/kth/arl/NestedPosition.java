package se.kth.arl;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtTypeMember;

import java.util.ArrayList;
import java.util.List;

public class NestedPosition implements Position {
	String[] parents = new String[0];
	String fullyQualifiedName;
	int position;

	public NestedPosition(String host, int i) {
		if(host.contains("$")) {
			parents = host.split("\\$");
		}
		fullyQualifiedName = host;
		position = i;
	}


	@Override
	public boolean reachable(CtClass base) {
		CtClass host;
		if(base.getQualifiedName().equals(fullyQualifiedName)) {
			host = base;
		} else {
			host = (CtClass) base.getNestedType(fullyQualifiedName.replace(base.getQualifiedName() + "$", ""));
		}
		if(host == null) return false;
		return true;
	}

	@Override
	public boolean putAt(CtClass base, CtTypeMember tm) {//throws NoSuchInnerClassException{
		CtClass host;
		if(base.getQualifiedName().equals(fullyQualifiedName)) {
			host = base;
		} else {
			host = (CtClass) base.getNestedType(fullyQualifiedName.replace(base.getQualifiedName() + "$", ""));
		}
		if(host == null) return false;
		List<CtTypeMember> typeMembers = new ArrayList<>(host.getTypeMembers());
		String signature = Decompilation.signature(tm);
		for(int i = 0; i < typeMembers.size(); i++) {
			if(Decompilation.signature(typeMembers.get(i)).equals(signature)) {
				typeMembers.set(i, tm);
			}
		}
		//typeMembers.set(position, tm);
		host.setTypeMembers(typeMembers);
		return true;
	}
}
