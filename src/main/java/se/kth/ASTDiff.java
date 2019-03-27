package se.kth;

import java.io.File;

public interface ASTDiff {
	int compare(File original, File actual);
}
