package se.kth.decompiler;

import jd.core.loader.Loader;
import jd.core.loader.LoaderException;
import jd.core.preferences.Preferences;
import jd.core.util.ClassFileUtil;
import jd.core.process.DecompilerImpl;
import jd.core.util.TypeNameUtil;
import org.jd.gui.util.decompiler.GuiPreferences;
import org.jd.gui.util.decompiler.PlainTextPrinter;
import se.kth.Decompiler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class JDGui implements Decompiler {

	@Override
	public String getName() {
		return "JD-GUI-1.4.1";
	}

	@Override
	public boolean decompile(File in, File out, String cl) {
		try {
			final File tempClass = in;
			final File tempJava = new File(out, cl + ".java");
			File inBase = new File(in.getAbsolutePath().replace(cl + ".java", ""));

			String pathToClass = in.getAbsolutePath();

			Preferences preferences = new Preferences();

			Loader loader = new Loader() {
				@Override
				public DataInputStream load(String s) throws LoaderException {
					//System.err.println("[RE] load ? " + s);
					File file;
					if(s.startsWith("/"))
						file = new File(s);
					else
						file = new File(inBase + "/" + s);

					try {
						return new DataInputStream(
								new BufferedInputStream(new FileInputStream(file)));
					} catch (FileNotFoundException e) {
						throw new LoaderException(
								"'" + file.getAbsolutePath() + "'  not found.");
					}
				}

				@Override
				public boolean canLoad(String s) {
					//System.err.println("[RE] canLoad ? " + s);
					File file = new File(inBase, s);
					return file.exists() && file.isFile();
				}
			};

			PrintStream ps = new PrintStream(tempJava.getAbsolutePath());
			PlainTextPrinter printer = new PlainTextPrinter();
			printer.setPrintStream(ps);
			printer.setPreferences(new GuiPreferences(false, false, false, false, false));

			System.err.println("JDGUI out: " + tempJava.getAbsolutePath());

			jd.core.Decompiler decompiler = new DecompilerImpl();
			decompiler.decompile(preferences, loader, printer, pathToClass);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(getName() + " failed with e: " + e.getMessage());
		}
		return false;
	}
}
