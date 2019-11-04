package se.kth.asm;

import org.objectweb.asm.signature.SignatureReader;

public class MethodSignatureVisitor extends SignatureReader {
	String res = "";

	public MethodSignatureVisitor(String signature) {
		super(signature);
	}
}
