package se.kth;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.json.JSONException;
import se.kth.decompiler.CFR;
import se.kth.decompiler.DecompilerRegistry;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * Hello world!
 *
 */
public class DecompilerComparator {

    @Parameter(names = {"--help", "-h"}, help = true, description = "Display this message.")
    private boolean help;
    @Parameter(names = {"--project", "-p"}, description = "Directory containing test reports")
    private String projectPath;
    @Parameter(names = {"--decompiler-name", "-d"}, description = "Name of the decompiler to test. See se.kth.Decompiler for more details. Default CFR-0.141")
    private String decompilerName = "CFR-0.141";
    @Parameter(names = {"--debug-class", "-c"}, description = "Optional. Run a single class")
    String classToRun;


    public static void main( String[] args ) throws IOException, JSONException {
        System.out.println( "Start" );
        DecompilerComparator decompilerComparator = new DecompilerComparator();
        JCommander jc = new JCommander(decompilerComparator,args);

        if(decompilerComparator.help || decompilerComparator.projectPath == null) {
            jc.usage();
            System.out.println("Available decompilers: ");
            for(String dc: DecompilerRegistry.decompilers.keySet()) {
                System.out.println("\t\t" + dc);
            }
        } else {
            File projectDir = new File(decompilerComparator.projectPath);
            if(!projectDir.exists()) {
                System.err.println("Project " + decompilerComparator.projectPath + " not found.");
                return;
            }

            Project project = new Project(projectDir.getAbsolutePath());
            Decompiler decompiler = DecompilerRegistry.decompilers.get(decompilerComparator.decompilerName);
            if(decompiler == null) {
                System.err.println("Error: " + decompilerComparator.decompilerName + " not in the decompiler list.");
                return;
            }
            if(decompilerComparator.classToRun != null) {
                project.run(decompiler, Collections.singletonList(decompilerComparator.classToRun));
            } else {
                project.run(decompiler);
            }
        }
    }
}
