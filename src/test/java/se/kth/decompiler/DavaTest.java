package se.kth.decompiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class DavaTest {

    Dava dava;

    @Before
    public void setUp() throws Exception {
//
//        System.out.println("classpath");
//        System.out.println(System.getProperty("java.class.path"));
//
//        System.out.println("librarypath");
//        System.out.println(System.getProperty("java.library.path"));
        dava = new Dava();
    }

    @Test
    public void decompileClassFile() {

        File in = new File("src/test/resources/DiffImpl.class");
        File out = new File("src/test/resources/");

        System.out.println(in.getAbsolutePath());
        dava.decompile(in, out, "");
    }
}