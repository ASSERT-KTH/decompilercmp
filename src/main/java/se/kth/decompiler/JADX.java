package se.kth.decompiler;

import org.apache.commons.io.FileUtils;
import se.kth.Decompiler;


import java.io.File;

public class JADX implements Decompiler {

    @Override
    public boolean decompile(File in, File outputDir, String cl) {

        File inBase = new File(in.getAbsolutePath().replace(cl + ".class", ""));
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "./lib/jadx-0.9.0/bin/jadx", // build-in script to run the decompiler
                    "-r", // do not decode resources
                    "-d", "tmp", // output directory
                    in.getAbsolutePath() // input file (.dex, .apk, .jar or .class)
            ).inheritIO();
            //pb.directory(new File("lib/jadx-0.9.0/bin"));

            Process p = pb.start();
            int r =  p.waitFor();
            FileUtils.moveFile(new File("tmp/sources/" + cl + ".java"), new File(outputDir, cl + ".java"));
            if(new File("tmp/sources").exists()) FileUtils.deleteQuietly(new File("tmp/sources"));
            return r==0;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return false;
    }

    @Override
    public String getName() {
        return "JADX-0.9.0";
    }
}
