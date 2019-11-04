package se.kth.decompiler;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import se.kth.Decompiler;
import se.kth.asm.ClassAPIVisitor;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.reflect.cu.position.PartialSourcePositionImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Arlecchino implements Decompiler {

	static File arlecchinoLogs = new File("arlecchino-logs");

	static boolean bestNotAssembleWhenUnnecessary = false;

	static boolean readMemberListFromDc = true;

	static boolean trustFirstTypeMemberList = false;

	boolean firstPassIsOver = false;

	File tmpOutputDir = new File("Arlecchino");

	public Arlecchino(List<Decompiler> decompilers) {
		this.decompilers = decompilers;
		cleanTmpDir();
	}

	public void cleanTmpDir() {

		if(tmpOutputDir.exists()) {
			try {
				FileUtils.deleteDirectory(tmpOutputDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tmpOutputDir.mkdirs();
	}

	List<Decompiler> decompilers;

	@Override
	public boolean decompile(File in, File outDir, String cl, String[] classpath) {

		boolean success = false,
				isSuperInterfacesFilled = false,
				isFormalTypeParameterFilled = false,
				isSuperModifiersFilled = false;
		Map<String, List<CtTypeMember>> typeMembers;

		final Launcher launcherOutput = new Launcher();
		//launcherOutput.getEnvironment().setNoClasspath(true);
		launcherOutput.setSourceOutputDirectory(outDir);
		try {
			CtClass outputClass;
			if (!readMemberListFromDc) {
				ClassAPIVisitor cav = ClassAPIVisitor.readClass(in, launcherOutput.getFactory());
				outputClass = cav.toBuild;
				typeMembers = cav.classApi;
			} else {
				typeMembers = new HashMap<>();
				outputClass = launcherOutput.getFactory().createClass(cl.replace("/", "."));
			}

			boolean isTrivial = false;

			for(Decompiler dc: decompilers) {
				try {
					cleanTmpDir();
					new File(tmpOutputDir + "/" + (cl.contains("/") ? cl.substring(0, cl.lastIndexOf("/")) : cl)).mkdirs();
					//Decompilation attempt
					System.out.println("[" + getName() + "] Decompilation attempt with: " + dc.getName());
					dc.decompile(in, tmpOutputDir, cl, classpath);


					//Build model i
					final String filePath = new File(tmpOutputDir.getAbsolutePath() + "/" + cl + ".java").getCanonicalPath();
					final Launcher launcher = new Launcher();
					launcher.getEnvironment().setSourceClasspath(classpath);
					//launcherA.getEnvironment().setNoClasspath(true);
					launcher.addInputResource(tmpOutputDir.getAbsolutePath());
					CtModel model = launcher.buildModel();

					Optional<CtType<?>> op = model.getAllTypes()
							.stream()
							.filter(t -> t.getQualifiedName().equals(cl.replace("/", ".")))
							.findFirst();

					//If no decompilation output, move on to the next decompiler
					if(!op.isPresent()) continue;

					CtType aa = op.get();

					System.out.println("[" + getName() + "] Decompiled type found: " + aa.getClass().getName() + ":" + aa.getQualifiedName());

					//Assume that Interfaces, Annotation and Enum will be handled correctly by the first decompiler.
					if(!(aa instanceof CtClass)) {
						File toRemove = new File(tmpOutputDir,cl + ".java");
						FileUtils.moveFile(toRemove, new File(outDir.getAbsolutePath() + "/" + cl + ".java"));
						return true;
					}

					JDTBasedSpoonCompiler compiler = (JDTBasedSpoonCompiler) launcher.getModelBuilder();

					if (readMemberListFromDc) {
						//Fill class information
						if(!isSuperInterfacesFilled) {
							outputClass.setSuperInterfaces(aa.getSuperInterfaces());
							isSuperInterfacesFilled = true;
						}
						if(outputClass.getSuperclass() == null) {
							outputClass.setSuperclass(aa.getSuperclass());
						}
						if(!isFormalTypeParameterFilled) {
							outputClass.setFormalCtTypeParameters(aa.getFormalCtTypeParameters());
							isFormalTypeParameterFilled = true;
						}
						if(!isSuperModifiersFilled) {
							outputClass.setModifiers(aa.getModifiers());
							isSuperModifiersFilled = true;
						}
					}

					//Check for compilation error in decompiled code and categorized them by type member
					List<CategorizedProblem> problems = compiler.getProblems()
							.stream()
							.filter(p -> p.isError())
							.filter(p -> new String(p.getOriginatingFileName()).equals(filePath))
							.collect(Collectors.toList());
					System.out.println("[" + getName() + "] Type contains " + problems.size() + " problems.");

					//When a single decompiler handle correctly the class, take directly its solution
					if(bestNotAssembleWhenUnnecessary && (problems.size() == 0)) {
						System.out.println("[" + getName() + "] Use " + dc.getName() + "'s solution.");
						File toRemove = new File(tmpOutputDir,cl + ".java");
						FileUtils.moveFile(toRemove, new File(outDir.getAbsolutePath() + "/" + cl + ".java"));
						return true;
					}

					//If not, let's store the new type members correctly decompiled (i.e. without decompilation error)
					//aa.getTypeMembers()
					//		.stream()
					//		.forEach(tm -> addToMap(((CtTypeMember) tm), typeMembers, problems));
					for(Object tm: aa.getTypeMembers()) {
						addToMap((CtTypeMember) tm, typeMembers, problems);
					}

					System.out.println("[" + getName() + "] Type contains " + typeMembers.values().stream().filter(l -> l.isEmpty()).count() + " remaining problems.");
					System.out.println("[" + getName() + "] Type contains " + typeMembers.keySet().size() + " type members.");

					//If no incorrectly decompiled type member remain, call it a day.
					success = typeMembers.values().stream().map(l -> !l.isEmpty()).reduce(Boolean::logicalAnd).get();
					System.out.println("[" + getName() + "] Type is correct ? " + success);


					firstPassIsOver = true;

				} catch (Exception e) {
					//Spoon or the decompiler may crash, just go on with the next one.
					e.printStackTrace();
				} finally {
					//Clean up
					File toRemove = new File(tmpOutputDir,cl + ".java");
					toRemove.delete();
				}

				if(success) {
					break;
				}

			}
			if(success)
				//Assemble the solution from correct type members
				//mergeResults(cl.replace("/","."), outDir.getAbsolutePath(), superClass, interfaces,formalParameters, modifiers, typeMembers);
				mergeResults(cl.replace("/","."), launcherOutput, outputClass, typeMembers);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public String getName() {
		return "Arlecchino";
	}

	public void mergeResults(String className, Launcher launcherOutput,
				CtClass outputClass,
				Map<String, List<CtTypeMember>> typeMembers) {

		for(String key: typeMembers.keySet()) {
			if(key.equals("<clinit>()") && typeMembers.get(key).size() > 1) {
				//Small hack, as first solution for <clinit>() is an empty block
				outputClass.addTypeMember(typeMembers.get(key).get(1));
			} else {
				outputClass.addTypeMember(typeMembers.get(key).get(0));
			}
		}
		Factory factory = outputClass.getFactory();
		CompilationUnit cu = factory.createCompilationUnit();

		outputClass.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				super.enter(e);
				e.setFactory(factory);
				e.setPosition(new PartialSourcePositionImpl(cu));

				/*if(e instanceof CtReference) {
					CtReference ref = (CtReference) e;
					System.out.println(ref.toString());
				}*/
			}
		});

		launcherOutput.prettyprint();

		//Arlecchino thinks it has a solution
		try {
			FileUtils.write(arlecchinoLogs,className + "\n", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addToMap(CtTypeMember tm, Map<String, List<CtTypeMember>> map, List<CategorizedProblem> problems) {
		String key;
		if(tm instanceof CtField) {
			key = tm.getParent(CtType.class).getQualifiedName() + "#" + tm.getSimpleName();
		} else if (tm instanceof CtType) {
			key = ((CtType) tm).getQualifiedName();
		} else {
			key = ((CtExecutable) tm).getSignature();
		}
		if (!tm.isImplicit() && readMemberListFromDc && !(trustFirstTypeMemberList && firstPassIsOver)) {
			if (!map.containsKey(key))
				map.put(key, new ArrayList<>());
		}
		if(map.containsKey(key)) {
			if (!hasProblem(problems, tm))
				map.get(key).add(tm);
		} else {
			System.out.println("key: " + key + " not found");
		}
		//}
	}

	public boolean hasProblem(List<CategorizedProblem> problems, CtElement element) {
		try {
			SourcePosition position = element.getPosition();
			int begin = position.getLine();
			int end = position.getEndLine();
			boolean has = false;
			for (CategorizedProblem problem : problems) {
				int line = problem.getSourceLineNumber();
				has |= line >= begin && line <= end;
			}
			return has;
		} catch (Exception e) {
			System.err.println("Problem");
			return true;
		}
	}

}
