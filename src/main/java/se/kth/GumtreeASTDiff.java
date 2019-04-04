package se.kth;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtScanner;

import java.io.File;

public class GumtreeASTDiff implements ASTDiff {

	@Override
	public int getNbNode(File original) {
		try {
			AstComparator astComparator = new AstComparator();
			CtType originalType = astComparator.getCtType(original);

			NodeCounter countNode = new NodeCounter();

			originalType.accept(countNode);
			return countNode.nbNodes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  Integer.MIN_VALUE;
	}

	@Override
	public int compare(File original, File actual) {

		try {
			AstComparator astComparator = new AstComparator();
			CtType originalType, actualType;
			originalType = astComparator.getCtType(original);
			actualType = astComparator.getCtType(actual);

			int i = 0;
			NodeCounter countNode = new NodeCounter();

			originalType.accept(countNode);

			Diff result = astComparator.compare(originalType,actualType);
			return result.getAllOperations().size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  Integer.MIN_VALUE;
	}

	class NodeCounter extends CtScanner {
		public int nbNodes = 0;
		@Override
		public void scan(CtElement element) {
			super.scan(element);
			nbNodes++;
		}

	}
}
