package se.kth.asm;

import org.objectweb.asm.signature.SignatureVisitor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SigVisitor extends SignatureVisitor {
	static boolean debug = false;

	public SigVisitor(int api, Factory factory, CtClass rootClass) {
		super(api);
		this.factory = factory;
		types.push(rootClass);
	}
	Factory factory;

	enum EndAction { FORMAL_TYPE_PARAMETER, SUPER_CLASS, INTERFACE, INTERFACE_BOUND, CLASS_BOUND, TYPE_ARGUMENT, OTHER, MARKER }

	Stack<CtType> types = new Stack<>();
	Stack<CtTypeReference> refs = new Stack<>();
	Stack<EndAction> endActions = new Stack<>();
	Stack<Character> wildcards = new Stack<>();

	//TypeSignature = visitBaseType | visitTypeVariable | visitArrayType | ( visitClassType visitTypeArgument* ( visitInnerClassType visitTypeArgument* )* visitEnd ) )

	@Override
	public void visitFormalTypeParameter(String name) {
		//endActions.push(EndAction.MARKER);
		//unstack();
		finish();
		if(debug) System.out.println("visitFormalTypeParameter " + name);
		CtTypeParameter t = factory.createTypeParameter();
		t.setSimpleName(name);
		types.push(t);
		endActions.push(EndAction.FORMAL_TYPE_PARAMETER);
	}

	@Override
	public SignatureVisitor visitClassBound() {
		if(debug) System.out.println("visitClassBound");
		endActions.push(EndAction.CLASS_BOUND);
		return this;
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		if(debug) System.out.println("visitInterfaceBound");
		endActions.push(EndAction.INTERFACE_BOUND);
		return this;
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		finish();
		//endActions.push(EndAction.MARKER);
		//unstack();
		if(debug) System.out.println("visitSuperclass");
		endActions.push(EndAction.SUPER_CLASS);
		return this;
	}

	@Override
	public SignatureVisitor visitInterface() {
		finish();
		//endActions.push(EndAction.MARKER);
		//unstack();
		if(debug) System.out.println("visitInterface");
		endActions.push(EndAction.INTERFACE);
		return this;
	}

	@Override
	public SignatureVisitor visitParameterType() {
		if(debug) System.out.println("visitParameterType");
		return this;
	}

	@Override
	public SignatureVisitor visitReturnType() {
		if(debug) System.out.println("visitReturnType");
		return this;
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		if(debug) System.out.println("visitExceptionType");
		return this;
	}

	@Override
	public void visitBaseType(char descriptor) {
		if(debug) System.out.println("visitBaseType " + descriptor);
	}

	@Override
	public void visitTypeVariable(String name) {
		refs.push(factory.createTypeParameterReference(name));
		if(debug) System.out.println("visitTypeVariable " + name);
	}

	@Override
	public SignatureVisitor visitArrayType() {
		if(debug) System.out.println("visitArrayType");
		return this;
	}

	@Override
	public void visitClassType(String name) {
		endActions.push(EndAction.MARKER);
		refs.push(factory.createReference(name.replace("/",".")));
		if(debug) System.out.println("visitClassType " + name);
	}

	@Override
	public void visitInnerClassType(String name) {
		if(debug) System.out.println("visitInnerClassType " + name);
	}

	@Override
	public void visitTypeArgument() {
		endActions.push(EndAction.TYPE_ARGUMENT);
		if(debug) System.out.println("visitTypeArgument ");
	}

	@Override
	public SignatureVisitor visitTypeArgument(char wildcard) {
		endActions.push(EndAction.TYPE_ARGUMENT);
		wildcards.push(wildcard);
		if(debug) System.out.println("visitTypeArgument " + wildcard);
		return this;
	}

	public void unstack() {
		boolean stop = false;
		while(!endActions.isEmpty() && !stop) {
			if(debug) System.out.println("unstack");
			EndAction action = endActions.pop();
			switch (action) {
				case INTERFACE:
					types.peek().addSuperInterface(refs.pop());
					break;
				case SUPER_CLASS:
					types.peek().setSuperclass(refs.pop());
					break;
				case TYPE_ARGUMENT:
					CtTypeReference tref = refs.pop();
					char wildcard = wildcards.pop();
					if (wildcard == INSTANCEOF) {
						refs.peek().addActualTypeArgument(tref);
					} else if (wildcard == SUPER) {
						CtWildcardReference wref = factory.createWildcardReference();
						wref.setBoundingType(tref);
						wref.setUpper(false);
						refs.peek().addActualTypeArgument(wref);
					} else if (wildcard == EXTENDS) {
						CtWildcardReference wref = factory.createWildcardReference();
						wref.setBoundingType(tref);
						wref.setUpper(true);
						refs.peek().addActualTypeArgument(wref);
					}
					break;
				case FORMAL_TYPE_PARAMETER:
					CtTypeParameter ftp = (CtTypeParameter) types.pop();
					types.peek().addFormalCtTypeParameter(ftp);
					break;
				case CLASS_BOUND:
				case INTERFACE_BOUND:
					if (types.peek().getSuperclass() != null) {
						if (types.peek().getSuperclass() instanceof CtIntersectionTypeReference) {
							CtIntersectionTypeReference intersectionTypeReference = (CtIntersectionTypeReference) types.peek().getSuperclass();
							intersectionTypeReference.addBound(refs.pop());
							types.peek().setSuperclass(intersectionTypeReference);
						} else {
							List<CtTypeReference<?>> bounds = new ArrayList<>();
							bounds.add(types.peek().getSuperclass());
							bounds.add(refs.pop());
							CtIntersectionTypeReference itr = factory.createIntersectionTypeReferenceWithBounds(bounds);
							types.peek().setSuperclass(itr);
						}
					} else {
						types.peek().setSuperclass(refs.pop());
					}
					break;
				case MARKER:
					stop = true;
			}
		}
	}

	public void finish() {
		while(!endActions.isEmpty()) {
			unstack();
		}
	}

	@Override
	public void visitEnd() {
		if(debug) System.out.println("visitEnd");
		//if(!endActions.isEmpty()) {
		//while(!endActions.isEmpty()) {
			unstack();
		//}
	}
}
