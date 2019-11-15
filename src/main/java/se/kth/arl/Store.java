package se.kth.arl;

import spoon.reflect.declaration.CtTypeMember;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Store {
	Map<String, Map<String, CtTypeMember>> store= new HashMap<>();

	public void addFragment(String signature, String decompiler, CtTypeMember typeMember) {
		if(!store.containsKey(signature)) {
			store.put(signature, new LinkedHashMap<>());
		}
		Map<String, CtTypeMember> fragmentsForSignature = store.get(signature);
		fragmentsForSignature.put(decompiler,typeMember);
	}

	public Map<String, CtTypeMember> getFragment(String signature) {
		return store.get(signature);
	}

	public boolean containsFragment(String signature) {
		if(!store.containsKey(signature)) return false;
		return !store.get(signature).isEmpty();
	}
}
