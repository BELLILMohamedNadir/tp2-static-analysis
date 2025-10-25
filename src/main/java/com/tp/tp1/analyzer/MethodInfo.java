package com.tp.tp1.analyzer;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class MethodInfo {
    public String name;
    public int linesOfCode;
    public int parameterCount;
    public List<String> parameters = new ArrayList<>();
    
    public MethodInfo(MethodDeclaration method, CompilationUnit cu) {
        this.name = method.getName().toString();
        
        // Calculate lines of code
        int startLine = cu.getLineNumber(method.getStartPosition());
        int endLine = cu.getLineNumber(method.getStartPosition() + method.getLength());
        this.linesOfCode = endLine - startLine + 1;
        
        // Extract parameters
        for (Object param : method.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) param;
            parameters.add(svd.getName().toString());
        }
        this.parameterCount = parameters.size();
    }
}
