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
    //static File mavenHome = new File("/opt/apache-maven-3.6.0");
    static File tmpDir = new File("tmp");
    static int testExecutionTimeOut = 20*60;//20 minutes in seconds

    //static String compileGoal = "org.apache.maven.plugins:maven-compiler-plugin:3.7.0:compile";
    static String compileGoal = "compile";


    File originalDir;
    File workingDir;
    String pathToSources;
    List<String> classes;
    Map<String, String> tests;
    File report;
    String projectName;
    String compilerId;

    public Project(String pathToProject) {
        this(pathToProject, "src/main/java");
    }

    public Project(String pathToProject, String pathToSources) {
        this(pathToProject,pathToSources,"javac");
    }
    public Project(String pathToProject, String pathToSources, String compilerId) {
        originalDir = new File(pathToProject);
        this.pathToSources = pathToSources;
        projectName = originalDir.getName();
        this.compilerId = compilerId;
        System.setProperty("nolabel", "true");
    }

    public void run(Decompiler dc, File outputDir) throws IOException, JSONException {
        classes = getClasses();
        run(dc, classes, outputDir, false);
    }


    public void run(Decompiler dc, List<String> classesToRun, File outputDir, boolean debug) throws IOException, JSONException {
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


        if(!debug) {
            report = new File(outputDir, projectName + ":" + dc.getName() + ":" + compilerId + ":report.csv");
            FileUtils.write(report, "Class,isDecompilable,distanceToOriginal,nbNodesOriginal,isRecompilable,bytecodeDistance,passTests\n", false);
        }

        for (String cl : classesToRun) {
            boolean isDecompilable = false;
            int distance = Integer.MIN_VALUE;
            int nbNodes = Integer.MIN_VALUE;
            boolean isReCompilable = false;
            int byteCodeDistance = Integer.MIN_VALUE;
            boolean passTests = false;

            //init
            removeClass(cl);

            isDecompilable = decompile(dc, cl);
            if (isDecompilable) {
                distance = compare(cl);
                nbNodes = getNbNode(cl);
                isReCompilable = compile(workingDir, false);
                if (isReCompilable) {
                    byteCodeDistance = compareByteCode(cl);

                    passTests = test(tests.get(cl), debug);
                }
            }

            //report
            if(!debug) {
                report(cl, isDecompilable, distance, nbNodes, isReCompilable, byteCodeDistance, (tests.get(cl) == null) ? "NA" : ("" + passTests));
            }

            //clean up
            if(!debug) {
                restore(cl);
            }
        }
    }

    public boolean compile(File dir, boolean clean) {
        File pomFile = new File(dir, "pom.xml");
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBatchMode(true);
        request.setPomFile(pomFile);
        //request.setDebug(true);
        if(!clean) {
            request.setGoals(Collections.singletonList(compileGoal));
        } else {
            List<String> goals = new LinkedList<>();
            goals.add("clean");
            goals.add(compileGoal);
            request.setGoals(goals);
        }

        Properties properties = new Properties();
        properties.setProperty("maven.compiler.compilerId", compilerId);
        request.setProperties(properties);

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

    public boolean test(String tests, boolean debug) {
        if (tests != null) {
            File pomFile = new File(workingDir, "pom.xml");
            InvocationRequest request = new DefaultInvocationRequest();
            request.setBatchMode(true);
            request.setPomFile(pomFile);
            request.setGoals(Collections.singletonList("test"));
            request.setTimeoutInSeconds(testExecutionTimeOut);

            Properties properties = new Properties();
            if (!tests.equalsIgnoreCase("*")) {
                properties.setProperty("test", tests);
            }
            if(!debug) {
                properties.setProperty("surefire.skipAfterFailureCount", "1");
            }
            if(!properties.isEmpty()) {
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

    public int compareByteCode(String cl) {
        File originalByteCode = new File(originalDir, "target/classes/" + cl + ".class");
        File nByteCode = new File(workingDir, "target/classes/" + cl + ".class");
        return ByteCodeDiff.compare(originalByteCode, nByteCode);
    }

    public int getNbNode(String cl) {
        File src = new File(originalDir, "src/main/java/" + cl + ".java");
        return new GumtreeASTDiff().getNbNode(src);
    }

    public void report(String cl, boolean isDecompilable, int distance, int nbNodes, boolean isRecompilable, int byteCodeDistance, String passTests) throws IOException {
        FileUtils.write(report, cl + "," +
                isDecompilable + "," +
                (distance < 0 ? "NA" : distance) + "," +
                (nbNodes < 0 ? "NA" : nbNodes) + "," +
                isRecompilable + "," +
                (byteCodeDistance < 0 ? "NA" : byteCodeDistance) + "," +
                passTests + "\n",
                true);

        System.out.println("Class " + cl + " dc: " + isDecompilable +
                ", dist: " + distance +
                ", nbNodes: " + nbNodes +
                ", rc: " + isRecompilable +
                ", byteCodeDistance: " + byteCodeDistance +
                ", tests: " + passTests);
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
                if(testList.length() > 0) {
                    tests.put(clName, testList);
                }
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
