package se.kth;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;

import java.io.File;

public class GumtreeASTDiff implements ASTDiff {
	@Override
	public int compare(File original, File actual) {

		try {
			Diff result = new AstComparator().compare(original,actual);
			return result.getAllOperations().size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  Integer.MIN_VALUE;
	}
}
