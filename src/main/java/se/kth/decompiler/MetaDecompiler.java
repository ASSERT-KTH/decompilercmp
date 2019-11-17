package se.kth.decompiler;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import se.kth.Decompiler;
import se.kth.arl.Decompilation;
import se.kth.arl.Logger;
import se.kth.arl.Store;
import se.kth.asm.ClassAPIVisitor;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MetaDecompiler implements Decompiler {
	public static boolean innerClassGranularity = true;

	boolean test = false;
	boolean bestNotAssembleWhenUnnecessary = false;
	File tmpOutputDir = new File("meta-dc");

	public MetaDecompiler(List<Decompiler> decompilers) {
		this.decompilers = decompilers;
		cleanTmpDir();
	}

	public MetaDecompiler(List<Decompiler> decompilers, boolean test) {
		this.test = test;
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

	public List<Decompiler> getDecompilers() {
		return decompilers;
	}

	@Override
	public boolean decompile(File in, File outDir, String cl, String[] classpath) {
		if(Logger.getInstance() == null) {
			Logger.createInstance(this);
		}
		boolean firstPass = true;

		boolean success = false;
		//Map<String, CtTypeMember> store = new HashMap<>();
		Store store = new Store();
		List<Decompilation> decompilations = new ArrayList<>();

		final Launcher launcherOutput = new Launcher();
		//launcherOutput.getEnvironment().setNoClasspath(true);
		launcherOutput.setSourceOutputDirectory(outDir);
		try {
			if(!test) {
				ClassAPIVisitor cav = ClassAPIVisitor.readClass(in, launcherOutput.getFactory());
				decompilations.add(new Decompilation(cav));
			}
			for (Decompiler dc : decompilers) {
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

					//Update store with new solutions
					/*List<CtTypeMember> tms = aa.getTypeMembers();
					for(CtTypeMember tm: tms) {
						if(!Decompilation.hasProblem(problems, tm)) {
							String signature = Decompilation.signature(tm);
							//if(!store.containsKey(signature)) {
							if(!store.containsFragment(signature)) {
								//store.put(signature, tm);
								store.addFragment(signature,dc.getName(),tm);
								if(!firstPass) {
									System.out.println("[" + getName() + "][" + dc.getName() + "] new signature: " + signature);
								}
							}
						}
					}*/
					store.update((CtClass) aa,problems,dc.getName());

					//If not, let's store the new type members correctly decompiled (i.e. without decompilation error)
					Decompilation decompilation = new Decompilation((CtClass) aa, problems, dc.getName());
					int index = test ? decompilations.size() : decompilations.size()-1;
					decompilations.add(index, decompilation);

					//List<Decompilation> solutions = new ArrayList<>(decompilations);
					//solutions.sort(Comparator.comparing(Decompilation::initialProblems));

					for(Decompilation solution: decompilations) {
						int r = solution.remainingProblems(store);
						System.out.println("[" + getName() + "] " + solution.decompilerName + "'s solution contains " + r + " remaining problems.");
						if(r == 0) {
							success = solution.isSolutionRecompilable(outDir, store, classpath);
							if(success) break;
						}
					}

					//If no incorrectly decompiled type member remain, call it a day.
					System.out.println("[" + getName() + "] Type is correct ? " + success);



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
				firstPass = false;
			}
			if(!success) {
				System.out.println("[" + getName() + "][Second round] -------------------------------------------");
				store.rotate();
				for(Decompilation solution: decompilations) {
					if(solution.doomed) {
						solution.doomed = false;
						int r = solution.remainingProblems(store);
						System.out.println("[" + getName() + "][Second round] " + solution.decompilerName + "'s solution contains " + r + " remaining problems.");
						if(r == 0) {
							success = solution.isSolutionRecompilable(outDir, store, classpath);
							if(success) break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!success) {
			Logger.getInstance().logFailure(cl,getName());
		}
		return success;
	}

	@Override
	public String getName() {
		return "MetaDecompiler";
	}

}
