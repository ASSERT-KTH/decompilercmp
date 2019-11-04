package se.kth.asm;

import org.objectweb.asm.signature.SignatureVisitor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SigVisitor extends SignatureVisitor {
	public SigVisitor(int api, Factory factory, CtClass rootClass) {
		super(api);
		this.factory = factory;
		types.push(rootClass);
	}
	Factory factory;

	enum EndAction { FORMAL_TYPE_PARAMETER, SUPER_CLASS, INTERFACE, INTERFACE_BOUND, CLASS_BOUND, TYPE_ARGUMENT, OTHER }

	Stack<CtType> types = new Stack<>();
	Stack<CtTypeReference> refs = new Stack<>();
	Stack<EndAction> endActions = new Stack<>();

	//TypeSignature = visitBaseType | visitTypeVariable | visitArrayType | ( visitClassType visitTypeArgument* ( visitInnerClassType visitTypeArgument* )* visitEnd ) )

	@Override
	public void visitFormalTypeParameter(String name) {
		visitEnd();
		System.out.println("visitFormalTypeParameter " + name);
		CtTypeParameter t = factory.createTypeParameter();
		t.setSimpleName(name);
		types.push(t);
		endActions.push(EndAction.FORMAL_TYPE_PARAMETER);
	}

	@Override
	public SignatureVisitor visitClassBound() {
		System.out.println("visitClassBound");
		endActions.push(EndAction.CLASS_BOUND);
		return this;
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		System.out.println("visitInterfaceBound");
		endActions.push(EndAction.INTERFACE_BOUND);
		return this;
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		visitEnd();
		System.out.println("visitSuperclass");
		endActions.push(EndAction.SUPER_CLASS);
		return this;
	}

	@Override
	public SignatureVisitor visitInterface() {
		visitEnd();
		System.out.println("visitInterface");
		endActions.push(EndAction.INTERFACE);
		return this;
	}

	@Override
	public SignatureVisitor visitParameterType() {
		System.out.println("visitParameterType");
		return this;
	}

	@Override
	public SignatureVisitor visitReturnType() {
		System.out.println("visitReturnType");
		return this;
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		System.out.println("visitExceptionType");
		return this;
	}

	@Override
	public void visitBaseType(char descriptor) {
		System.out.println("visitBaseType " + descriptor);
	}

	@Override
	public void visitTypeVariable(String name) {
		refs.push(factory.createTypeParameterReference(name));
		System.out.println("visitTypeVariable " + name);
	}

	@Override
	public SignatureVisitor visitArrayType() {
		System.out.println("visitArrayType");
		return this;
	}

	@Override
	public void visitClassType(String name) {
		refs.push(factory.createReference(name.replace("/",".")));
		System.out.println("visitClassType " + name);
	}

	@Override
	public void visitInnerClassType(String name) {
		System.out.println("visitInnerClassType " + name);
	}

	@Override
	public void visitTypeArgument() {
		endActions.push(EndAction.TYPE_ARGUMENT);
		System.out.println("visitTypeArgument ");
	}

	@Override
	public SignatureVisitor visitTypeArgument(char wildcard) {
		endActions.push(EndAction.TYPE_ARGUMENT);
		System.out.println("visitTypeArgument " + wildcard);
		return this;
	}

	@Override
	public void visitEnd() {
		System.out.println("visitEnd");
		while(!endActions.isEmpty()) {
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
					refs.peek().addActualTypeArgument(tref);
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

			}
			System.out.println("Done");
		}
	}
}
