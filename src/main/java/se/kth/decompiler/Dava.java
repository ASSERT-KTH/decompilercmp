package se.kth.decompiler;

import soot.Main;
import se.kth.Decompiler;

import java.io.File;

public class Dava implements Decompiler {

    @Override
    public boolean decompile(File in, File outputDir, String cl) {
        Main.main(new String[]{"-cp", System.getProperty("java.class.path") , "-f", "dava", in.getAbsolutePath(), "-d", outputDir.getAbsolutePath()});
        return true;
    }

    @Override
    public String getName() {
        return "Dava";
    }
}
