package se.kth.arl;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import se.kth.decompiler.MetaDecompiler;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtTypeMember;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

	public Map<String, CtTypeMember> getFragments(String signature) {
		return store.get(signature);
	}

	public CtTypeMember getFragment(String signature) {

		return store.get(signature).values().iterator().next();
	}

	public boolean containsFragment(String signature) {
		if(!store.containsKey(signature)) return false;
		return !store.get(signature).isEmpty();
	}

	public void update(CtClass root, List<CategorizedProblem> problems, String dcName) {
		List<CtTypeMember> tms = root.getTypeMembers();
		for(CtTypeMember tm: tms) {
			if (MetaDecompiler.innerClassGranularity && tm instanceof CtClass) {
				update((CtClass) tm, problems, dcName);
				updateUnit(tm, problems, dcName);
			} else {
				updateUnit(tm, problems, dcName);
			}
		}
	}

	private void updateUnit(CtTypeMember tm, List<CategorizedProblem> problems, String dcName) {
		if (!Decompilation.hasProblem(problems, tm)) {
			String signature = Decompilation.signature(tm);
			//if(!store.containsKey(signature)) {
			if (!containsFragment(signature)) {
				//store.put(signature, tm);
				addFragment(signature, dcName, tm);
			}
		}
	}

	public void rotate() {
		for(Map<String, CtTypeMember> fragments : store.values()) {
			Map.Entry<String, CtTypeMember> entry = fragments.entrySet().iterator().next();
			fragments.remove(entry.getKey());
			fragments.put(entry.getKey(), entry.getValue());
		}
	}
}
