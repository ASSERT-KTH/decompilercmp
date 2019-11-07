package se.kth.arl;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import se.kth.asm.ClassAPIVisitor;
import spoon.Launcher;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.cu.position.PartialSourcePositionImpl;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Decompilation {
	CtClass src;
	Map<Integer, String> rProblems;
	boolean doomed = false;
	public String decompilerName;

	public Decompilation(CtClass src, List<CategorizedProblem> problems, String decompilerName) {
		this.src = src;
		this.decompilerName = decompilerName;

		rProblems = new HashMap<>();
		List<CtTypeMember> typeMembers = src.getTypeMembers();
		for(int i = 0; i < typeMembers.size(); i++) {
			CtTypeMember tm = typeMembers.get(i);
			if(hasProblem(problems, tm)) {
				rProblems.put(i, signature(tm));
			}
		}
		if(!problems.isEmpty() && rProblems.isEmpty()) {
			//There are problem with the Class structure that won't be overcome
			doomed = true;
			System.out.println("[" + decompilerName + "] Solution is Doomed! ------------------");
		}
	}

	public Decompilation(ClassAPIVisitor v) {
		this.src = v.toBuild;
		List<CtTypeMember> typeMembers = new ArrayList<>();
		this.decompilerName = "Empty";

		rProblems = new HashMap<>();
		int i = 0;
		for(String signature: v.classApi.keySet()) {
			CtField placeholder = src.getFactory().Field().create(
					src,
					new HashSet<>(),
					src.getFactory().Type().BOOLEAN_PRIMITIVE,
					"placeHolderFieldN" + i);
			typeMembers.add(i, placeholder);
			rProblems.put(i, signature);
			i++;
		}
		src.setTypeMembers(typeMembers);
	}

	public int initialProblems() {
		return rProblems.size();
	}

	public int remainingProblems(Map<String, CtTypeMember> store) {
		if(doomed) return 666;
		int remainingProblems = rProblems.size();
		for (Map.Entry<Integer, String> problem: rProblems.entrySet()) {
			if(store.containsKey(problem.getValue())) {
				remainingProblems--;
			}
		}
		return remainingProblems;
	}

	public void print(File outputDir, Map<String, CtTypeMember> store) {

		final Launcher outputLauncher = new Launcher();
		outputLauncher.setSourceOutputDirectory(outputDir);

		CtClass base = outputLauncher.getFactory().createClass(src.getQualifiedName());

		base.setModifiers(src.getModifiers());
		base.setSuperclass(src.getSuperclass());
		base.setSuperInterfaces(src.getSuperInterfaces());
		base.setFormalCtTypeParameters(src.getFormalCtTypeParameters());
		List<CtTypeMember> typeMembers = new ArrayList<>(src.getTypeMembers());
		for (Map.Entry<Integer, String> problem: rProblems.entrySet()) {
			typeMembers.set(problem.getKey(),store.get(problem.getValue()));
		}
		base.setTypeMembers(typeMembers);
		Factory factory = base.getFactory();
		CompilationUnit cu = factory.createCompilationUnit();

		base.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				super.enter(e);
				e.setFactory(factory);
				e.setPosition(new PartialSourcePositionImpl(cu));
			}
		});

		outputLauncher.prettyprint();
	}

	public boolean isSolutionRecompilable(File outputDir, Map<String, CtTypeMember> store, String[] classpath) {
		print(outputDir, store);
		boolean success = false;

		String filePath = outputDir.getAbsolutePath() + "/" + src.getQualifiedName().replace(".", "/") + ".java";
		try {
			Launcher testLauncher = new Launcher();
			testLauncher.addInputResource(outputDir.getAbsolutePath());
			testLauncher.getEnvironment().setSourceClasspath(classpath);
			testLauncher.buildModel();
			JDTBasedSpoonCompiler compiler = (JDTBasedSpoonCompiler) testLauncher.getModelBuilder();
			//Check for compilation error in decompiled code and categorized them by type member
			List<CategorizedProblem> problems = compiler.getProblems()
					.stream()
					.filter(p -> p.isError())
					.filter(p -> new String(p.getOriginatingFileName()).equals(filePath))
					.collect(Collectors.toList());
			success = problems.isEmpty();
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(!success) {
			File toDelete = new File(filePath);
			if(toDelete.exists()) toDelete.delete();
			System.out.println("[" + decompilerName + "] Solution will not work ------------------");
			doomed = true;
		}
		return success;
	}

	public static boolean hasProblem(List<CategorizedProblem> problems, CtElement element) {
		try {
			SourcePosition position = element.getPosition();
			int begin = position.getLine();
			int end = position.getEndLine();
			boolean has = false;
			for (CategorizedProblem problem : problems) {
				int line = problem.getSourceLineNumber();
				has |= line >= begin && line <= end;
			}
			if(!has && element instanceof CtExecutable) {
				CtExecutable executable = (CtExecutable) element;
				if(executable.getBody().getStatements().size() == 1) {
					CtStatement stmt = executable.getBody().getStatements().get(0);
					if(stmt instanceof CtThrow) {
						CtThrow ctThrow = (CtThrow) stmt;
						String ex = ctThrow.getThrownExpression().getType().getQualifiedName();
						if(ex.contains("IllegalStateException")) {
							has = true;
						}
					}
				}
			}
			return has;
		} catch (Exception e) {
			System.err.println("Problem");
			return true;
		}
	}

	public static String signature(CtTypeMember tm) {
		if(tm instanceof CtField) {
			return tm.getParent(CtType.class).getQualifiedName() + "#" + tm.getSimpleName();
		} else if (tm instanceof CtType) {
			return ((CtType) tm).getQualifiedName();
		//} else if (tm instanceof CtAnonymousExecutable) {
		//	return "<clinit>()";
		} else {
			return ((CtExecutable) tm).getSignature();
		}
	}
}
