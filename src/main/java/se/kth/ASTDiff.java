package se.kth;

import java.io.File;

public interface ASTDiff {
	int getNbNode(File original);

	int compare(File original, File actual);
}
