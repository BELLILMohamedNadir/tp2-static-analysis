package com.tp.tp1.analyzer;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class ClassInfo {
    public String name;
    public String superclassName;
    public String packageName;
    public List<String> attributes = new ArrayList<>();
    public List<MethodInfo> methods = new ArrayList<>();
    public int totalLinesOfCode;

    public ClassInfo(TypeDeclaration node, CompilationUnit cu) {
        this.name = node.getName().toString();
        
        // Extract superclass
        Type superclass = node.getSuperclassType();
        this.superclassName = (superclass != null) ? superclass.toString() : "<none>";
        
        // Extract package
        PackageDeclaration pkg = cu.getPackage();
        this.packageName = (pkg != null) ? pkg.getName().toString() : "<default>";
        
        // Extract attributes (fields)
        for (FieldDeclaration field : node.getFields()) {
            for (Object fragment : field.fragments()) {
                VariableDeclarationFragment vdf = (VariableDeclarationFragment) fragment;
                attributes.add(vdf.getName().toString());
            }
        }
        
        // Extract methods
        for (MethodDeclaration method : node.getMethods()) {
            MethodInfo mi = new MethodInfo(method, cu);
            methods.add(mi);
        }
        
        // Calculate total lines of code for the class
        int startLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        this.totalLinesOfCode = endLine - startLine + 1;
    }
    
    public int getMethodCount() {
        return methods.size();
    }
    
    public int getAttributeCount() {
        return attributes.size();
    }
}
