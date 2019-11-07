package se.kth.asm;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.TraceSignatureVisitor;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class ClassAPIVisitor extends ClassVisitor implements Opcodes {

	Factory factory;
	public CtClass toBuild;

	public ClassAPIVisitor(ClassVisitor cv, Factory factory) {
		super(ASM5, cv);
		this.factory = factory;
	}

	public Map<String, List<CtTypeMember>> classApi = new HashMap<>();
	public Set<ModifierKind> modifiers = new HashSet<>();

	String packageName;
	String className;

	@Override
	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		super.visit(version,access,name,signature,superName,interfaces);
		int separatorIndex = name.lastIndexOf('/');
		packageName = name.substring(0, separatorIndex).replace('/','.');
		className = name.substring(separatorIndex+1);

		toBuild = factory.createClass(name.replace("/", "."));
		if(signature != null) {
			SigVisitor v = new SigVisitor(api, factory, toBuild);
			SignatureReader r = new SignatureReader(signature);
			r.accept(v);
			v.finish();
			System.out.println(signature);
		} else {
			if(superName != null && !superName.equals("java/lang/Object")) {
				toBuild.setSuperclass(factory.createReference(superName.replace('/','.')));
			}
			if(interfaces != null) {
				for(String iface: interfaces) {
					toBuild.addSuperInterface(factory.createReference(iface.replace('/','.')));
				}
			}
		}

		if(Modifier.isAbstract(access)) {
			modifiers.add(ModifierKind.ABSTRACT);
		}
		if(Modifier.isPublic(access)) {
			modifiers.add(ModifierKind.PUBLIC);
		}
		if(Modifier.isProtected(access)) {
			modifiers.add(ModifierKind.PROTECTED);
		}
		if(Modifier.isPrivate(access)) {
			modifiers.add(ModifierKind.PRIVATE);
		}
		if(Modifier.isStatic(access)) {
			modifiers.add(ModifierKind.STATIC);
		}
		toBuild.setModifiers(modifiers);
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef,
	                                             TypePath typePath, String desc, boolean visible) {
		System.out.println("Annotation: " + desc);
		return cv.visitTypeAnnotation(typeRef,typePath,desc,visible);
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name,
	                                 final String desc, final String signature, final String[] exceptions) {

		TraceSignatureVisitor tsv = new TraceSignatureVisitor(access);
		SignatureReader r = new SignatureReader(desc);
		r.accept(tsv);
		String dec = tsv.getDeclaration();
		String mname = name;

		if(mname.equals("<init>")) {
			mname = packageName + "." + className;
		}
		mname += dec.replace(" ","");

		//Method is not synthetic
		if((access & Opcodes.ACC_SYNTHETIC) == 0) {
			classApi.put(mname, new LinkedList<>());
		}
		if(name.equals("<clinit>")) {
			List<CtTypeMember> tms = new Stack<>();
			CtAnonymousExecutable clinit = factory.Executable().createAnonymous(toBuild,factory.createBlock());
			clinit.setImplicit(true);
			tms.add(clinit);
			classApi.put(mname, tms);
		}

		return cv.visitMethod(access, name, desc, signature, exceptions);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
	                               String signature, Object value) {
		//Field is not synthetic
		if((access & Opcodes.ACC_SYNTHETIC) == 0) {
			classApi.put(packageName + "." + className + "#" + name, new LinkedList<>());
		}
		return cv.visitField(access, name, desc, signature, value);
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		if(outerName != null && outerName.replace("/",".").equals(packageName + "." + className)) {
			//InnerClass is not synthetic
			if((access & Opcodes.ACC_SYNTHETIC) == 0) {
				classApi.put(packageName + "." + className + "$" + innerName, new LinkedList<>());
			}
		}
	}

	public static ClassAPIVisitor readClass(File in, Factory factory) throws IOException {
		ClassAPIVisitor cv;
		try (InputStream classFileInputStream = new FileInputStream(in)) {
			ClassReader cr = new ClassReader(classFileInputStream);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cv = new ClassAPIVisitor(cw, factory);
			cr.accept(cv, 0);
			return cv;
		}
	}
}
