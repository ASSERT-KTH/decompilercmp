package se.kth;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Project {
    static File mavenHome = new File("/usr/share/maven");
    static File tmpDir = new File("tmp");
    File originalDir;
    File workingDir;
    String pathToSources;
    List<String> classes;
    Map<String, String> tests;
    File report;
    String projectName;

    public Project(String pathToProject) {
        this(pathToProject, "src/main/java");
    }

    public Project(String pathToProject, String pathToSources) {
        originalDir = new File(pathToProject);
        this.pathToSources = pathToSources;
        projectName = originalDir.getName();
    }

    public void run(Decompiler dc, File outputDir) throws IOException, JSONException {
        classes = getClasses();
        run(dc, classes, outputDir);
    }


    public void run(Decompiler dc, List<String> classesToRun, File outputDir) throws IOException, JSONException {
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
        compile(originalDir);

        report = new File(outputDir, projectName + "-" + dc.getName() + "-report.csv");
        FileUtils.write(report, "Class,isDecompilable,distanceToOriginal,isRecompilable,passTests\n", false);

        for (String cl : classesToRun) {
            boolean isDecompilable = false;
            int distance = Integer.MIN_VALUE;
            boolean isReCompilable = false;
            boolean passTests = false;

            //init
            removeClass(cl);

            isDecompilable = decompile(dc, cl);
            if (isDecompilable) {
                distance = compare(cl);
                isReCompilable = compile(workingDir);
                if (isReCompilable) {
                    passTests = test(tests.get(cl));
                }
            }

            //report
            report(cl, isDecompilable, distance, isReCompilable, passTests);

            //clean up
            restore(cl);
        }
    }

    public boolean compile(File dir) {
        File pomFile = new File(dir, "pom.xml");
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBatchMode(true);
        request.setPomFile(pomFile);
        request.setGoals(Collections.singletonList("compile"));
		/*Properties properties = new Properties();
		if (sourceType == MavenLauncher.SOURCE_TYPE.APP_SOURCE) {
			properties.setProperty("includeScope", "runtime");
		}
		properties.setProperty("mdep.outputFile", getSpoonClasspathTmpFileName(sourceType));
		request.setProperties(properties);*/

        request.getOutputHandler(s -> System.out.println(s));
        request.getErrorHandler(s -> System.out.println(s));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(mavenHome);
        invoker.setWorkingDirectory(dir);
        invoker.setErrorHandler(s -> System.out.println(s));
        invoker.setOutputHandler(s -> System.out.println(s));
        try {
            InvocationResult result = invoker.execute(request);
            return result.getExitCode() == 0;
        } catch (MavenInvocationException e) {
            return false;
        }
    }

    public boolean test(String tests) {
        if (tests != null) {
            File pomFile = new File(workingDir, "pom.xml");
            InvocationRequest request = new DefaultInvocationRequest();
            request.setBatchMode(true);
            request.setPomFile(pomFile);
            request.setGoals(Collections.singletonList("test"));
            if (!tests.equalsIgnoreCase("*")) {
                Properties properties = new Properties();
                properties.setProperty("test", tests);
                request.setProperties(properties);
            }

            request.getOutputHandler(s -> System.out.println(s));
            request.getErrorHandler(s -> System.out.println(s));

            Invoker invoker = new DefaultInvoker();
            invoker.setMavenHome(mavenHome);
            invoker.setWorkingDirectory(workingDir);
            invoker.setErrorHandler(s -> System.out.println(s));
            invoker.setOutputHandler(s -> System.out.println(s));
            try {
                InvocationResult result = invoker.execute(request);
                return result.getExitCode() == 0;
            } catch (MavenInvocationException e) {
                return false;
            }
        } else {
            System.err.println("Not tests found for current class");
        }
        return true;
    }

    public void removeClass(String cl) {
        File src = new File(workingDir, "src/main/java/" + cl + ".java");
        src.delete();

    }

    public void restore(String cl) throws IOException {
        File src = new File(originalDir, "src/main/java/" + cl + ".java");
        File nsrc = new File(workingDir, "src/main/java/" + cl + ".java");

        FileUtils.copyFile(src, nsrc);
    }

    public boolean decompile(Decompiler dc, String cl) {
        File bc = new File(originalDir, "target/classes/" + cl + ".class");
        File output = new File(workingDir, "src/main/java/");
        boolean dcRes = dc.decompile(bc, output, cl);
        File expected = new File(workingDir, "src/main/java/" + cl + ".java");
        return dcRes && expected.exists();
    }

    public int compare(String cl) {
        File src = new File(originalDir, "src/main/java/" + cl + ".java");
        File nsrc = new File(workingDir, "src/main/java/" + cl + ".java");
        return new GumtreeASTDiff().compare(src, nsrc);
    }

    public void report(String cl, boolean isDecompilable, int distance, boolean isRecompilable, boolean passTests) throws IOException {
        FileUtils.write(report, cl + "," + isDecompilable + "," + distance + "," + isRecompilable + "," + passTests + "\n", true);
        System.out.println("Class " + cl + " dc: " + isDecompilable + ", dist: " + distance + ", rc: " + isRecompilable + ", tests: " + passTests);
    }

    public Map<String, String> loadTests() throws JSONException, IOException {
        try {
            Map<String, String> tests = new HashMap<>();
            String jsonString = FileUtils.readFileToString(new File(originalDir, "tie-report.json"));

            JSONObject testRaw = new JSONObject(jsonString);
            JSONArray classList = testRaw.getJSONArray("methodList");
            for (int i = 0; i < classList.length(); i++) {
                JSONObject cl = classList.getJSONObject(i);
                String clName = cl.getString("method").replace(".", "/");
                JSONArray clTests = cl.getJSONArray("called-in");
                String testList = "";
                for (int j = 0; j < clTests.length(); j++) {
                    if (j != 0) testList += ",";
                    testList += clTests.getString(j);
                }
                tests.put(clName, testList);
            }
            return tests;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<String> getClasses() {
        File srcDir = new File(originalDir, pathToSources);

        List<String> result = new LinkedList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(srcDir.toURI()))) {

            result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".java"))
                    .map(
                            s -> s.replace(srcDir.getAbsolutePath() + "/", "")
                                    .replace(".java", "")
                    ).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
