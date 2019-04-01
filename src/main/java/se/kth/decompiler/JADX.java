package se.kth.decompiler;

import se.kth.Decompiler;


import java.io.File;

public class JADX implements Decompiler {

    @Override
    public boolean decompile(File in, File outDir, String cl) {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "./jadx", // build-in script to run the decompiler
                    "-r", // do not decode resources
                    "-d", outDir.getAbsolutePath(), // output directory
                    in.getAbsolutePath() // input file (.dex, .apk, .jar or .class)
            );
            pb.directory(new File("lib/jadx-0.9.0/bin"));
            Process p = pb.start();
            int r =  p.waitFor();
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
