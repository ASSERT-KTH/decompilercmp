package se.kth;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import se.kth.decompiler.CFR;
import se.kth.decompiler.Dava;
import se.kth.decompiler.Fernflower;
import se.kth.decompiler.JADX;
import se.kth.decompiler.JDCore;
import se.kth.decompiler.JDGui;
import se.kth.decompiler.Jode;
import se.kth.decompiler.Krakatau;
import se.kth.decompiler.MetaDecompiler;
import se.kth.decompiler.Procyon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MetaDecompile extends Project {

	List<Decompiler> decompilerOrder;


	public MetaDecompile(String pathToProject) {
		this(pathToProject, "src/main/java");
	}

	public MetaDecompile(String pathToProject, String pathToSources) {
		this(pathToProject,pathToSources,"javac");
	}
	public MetaDecompile(String pathToProject, String pathToSources, String compilerId) {
		super(pathToProject,pathToSources,compilerId);
		decompilerOrder = new ArrayList<>();

		decompilerOrder.add(new MetaDecompiler(
						Arrays.asList(
								new Procyon(),
								new CFR(),
								new Fernflower(),
								new JDCore(),
								new JADX(),
								new Jode(),
								new Dava(),
								new Krakatau()
						)
				)
		);

		decompilerOrder.add(new CFR());//Best quality first
		decompilerOrder.add(new Procyon());
		decompilerOrder.add(new Fernflower());

		decompilerOrder.add(new JADX());
		decompilerOrder.add(new JDCore());
		decompilerOrder.add(new Jode());
	}

	public void run(File outputDir, boolean withTest) throws IOException, JSONException {
		classes = getClasses();
		run(classes, outputDir, withTest, false);
	}

	public void run(List<String> classesToRun, File outputDir, boolean withTest, boolean debug) throws IOException, JSONException {
		workingDir = new File(tmpDir, originalDir.getName());
		if (workingDir.exists()) {
			FileUtils.deleteDirectory(workingDir);
		}
		//Copy project
		FileUtils.copyDirectory(originalDir, workingDir);

		tests = loadTests();
		if (tests == null) {
			tests = new HashMap<>();
			for (String cl : classesToRun) {
				tests.put(cl, "*");
			}
		}

		//mvn compile original
		compile(originalDir, true);

		//read classpath
		buildClassPath(originalDir);


		if(!debug) {
			report = new File(outputDir, projectName + ":Meta:" + compilerId + ":report.csv");
			FileUtils.write(report, "Class,isDecompilable,distanceToOriginal,nbNodesOriginal,isRecompilable,bytecodeDistance,passTests,nbAttempt\n", false);
		}

		for (String cl : classesToRun) {
			boolean isDecompilable = false;
			int distance = Integer.MIN_VALUE;
			int nbNodes = Integer.MIN_VALUE;
			boolean isReCompilable = false;
			int byteCodeDistance = Integer.MIN_VALUE;
			boolean passTests = false;
			int nbAttempt = 0;

			//init

			for(Decompiler decompiler: decompilerOrder) {
				System.out.println("[MetaDecompiler] try with " + decompiler.getName());

				try {
					removeClass(cl);
				} catch (Exception e) {}

				nbAttempt++;
				int r = tryDecompile(cl, decompiler, withTest, debug);
				if(withTest && r == 2) {
					isDecompilable = true;
					isReCompilable = true;
					passTests = true;
					distance = compare(cl);
					nbNodes = getNbNode(cl);
					byteCodeDistance = compareByteCode(cl);
					break;
				} else if (!withTest && r == 1) {
					isDecompilable = true;
					isReCompilable = true;
					distance = compare(cl);
					nbNodes = getNbNode(cl);
					byteCodeDistance = compareByteCode(cl);

					passTests = test(tests.get(cl), debug);
					break;
				}
			}

			//report
			if(!debug) {
				report(cl, isDecompilable, distance, nbNodes, isReCompilable, byteCodeDistance, (tests.get(cl) == null) ? "NA" : ("" + passTests), nbAttempt);
			}

			//clean up
			if(!debug) {
				restore(cl);
			}
		}
	}

	public int tryDecompile(String cl, Decompiler decompiler, boolean withTest, boolean debug) {

		boolean isDecompilable;
		boolean isReCompilable;
		boolean passTests;

		isDecompilable = decompile(decompiler, cl);
		if (isDecompilable) {
			isReCompilable = compile(workingDir, false);
			if (isReCompilable && withTest) {
				passTests = test(tests.get(cl), debug);
				return passTests ? 2 : 1;
			} else {
				return  isReCompilable ? 1 : 0;
			}
		}
		return 0;
	}

	public void report(String cl, boolean isDecompilable, int distance, int nbNodes, boolean isRecompilable, int byteCodeDistance, String passTests, int nbAttempt) throws IOException {
		FileUtils.write(report, cl + "," +
						isDecompilable + "," +
						(distance < 0 ? "NA" : distance) + "," +
						(nbNodes < 0 ? "NA" : nbNodes) + "," +
						isRecompilable + "," +
						(byteCodeDistance < 0 ? "NA" : byteCodeDistance) + "," +
						passTests + "," +
						nbAttempt + "\n",
				true);

		System.out.println("Class " + cl + " dc: " + isDecompilable +
				", dist: " + distance +
				", nbNodes: " + nbNodes +
				", rc: " + isRecompilable +
				", byteCodeDistance: " + byteCodeDistance +
				", tests: " + passTests +
				", nbAttempt: " + nbAttempt);
	}
}
