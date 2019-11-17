package se.kth.arl;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtTypeMember;

import java.util.ArrayList;
import java.util.List;

public class SimplePosition implements Position {
	int position;
	public SimplePosition(int i) {
		position = i;
	}

	@Override
	public boolean reachable(CtClass base) {
		return true;
	}

	@Override
	public boolean putAt(CtClass base, CtTypeMember tm) {
		List<CtTypeMember> typeMembers = new ArrayList<>(base.getTypeMembers());
		String signature = Decompilation.signature(tm);
		for(int i = 0; i < typeMembers.size(); i++) {
			if(Decompilation.signature(typeMembers.get(i)).equals(signature)) {
				typeMembers.set(i, tm);
			}
		}
		//typeMembers.set(position, tm);
		base.setTypeMembers(typeMembers);
		return true;
	}
}
