package se.kth.arl;

import org.apache.commons.io.FileUtils;
import se.kth.decompiler.MetaDecompiler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Logger {
	static File logFile = new File("meta-dc-logs.csv");
	static Logger instance;

	List<String> decompilers;

	public static void createInstance(MetaDecompiler dc) {
		instance = new Logger();
		instance.decompilers = dc.getDecompilers().stream().map(d -> d.getName()).collect(Collectors.toList());
		try {
			if(!logFile.exists()) {
				String dcs = dc.getDecompilers().stream().map(d -> d.getName()).collect(Collectors.joining(","));
				FileUtils.write(logFile, "className,mainSolution,perceivedSuccess," + dcs + "\n", Charset.defaultCharset(), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Logger getInstance() {
		if(instance == null) {
			System.err.println("[Logger] Attempt to log with non initialized logger.");
		}
		return instance;
	}

	public void log(String className, String decompiler, boolean perceivedSuccess, Map<String, Integer> fragmentOrigins) {
		try {
			String dcs = fragmentOrigins.values().stream().map(i -> i.toString()).collect(Collectors.joining(","));
			FileUtils.write(logFile, className + "," + decompiler + "," + perceivedSuccess +"," + dcs + "\n", Charset.defaultCharset(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logFailure(String className, String decompiler) {
		try {
			String dcs = decompilers.stream().map(dc -> "0").collect(Collectors.joining(","));
			FileUtils.write(logFile, className + "," + decompiler + ",false," + dcs + "\n", Charset.defaultCharset(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Integer> getFragmentOrigins(Map<Position, String> remainingProblems, Store store, int tms, String mainDc) {
		Map<String, Integer> tmp = remainingProblems.values().stream()
				.map(sig -> store.getFragments(sig).keySet().iterator().next())
				.collect(
						Collectors.groupingBy(dc -> dc,
						Collectors.summingInt(i -> 1))
				);
		Map<String, Integer> res = new LinkedHashMap<>();
		for(String dc: getInstance().decompilers) {
			if(tmp.containsKey(dc)) {
				res.put(dc, tmp.get(dc));
			} else  if(dc.equals(mainDc)) {
				res.put(mainDc, tms);
			} else {
				res.put(dc, 0);
			}
		}
		return res;
	}
}
