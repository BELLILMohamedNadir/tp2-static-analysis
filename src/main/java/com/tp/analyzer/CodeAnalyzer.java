package com.tp.analyzer;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CodeAnalyzer {
    
    private int totalClasses = 0;
    private int totalLines = 0;
    private int totalMethods = 0;
    private int totalAttributes = 0;
    private Map<String, Integer> classMethodCounts = new HashMap<>();
    private Map<String, Integer> classLineCounts = new HashMap<>();
    private Map<String, Integer> classAttributeCounts = new HashMap<>();

    public void analyzeDirectory(String projectPath) throws IOException {
        // Réinitialiser les compteurs
        reset();
        
        // Scanner tous les fichiers .java
        List<Path> javaFiles = Files.walk(Paths.get(projectPath))
                .filter(path -> path.toString().endsWith(".java"))
                .collect(Collectors.toList());
        
        for (Path javaFile : javaFiles) {
            analyzeFile(javaFile.toFile());
        }
    }
    
    private void reset() {
        totalClasses = 0;
        totalLines = 0;
        totalMethods = 0;
        totalAttributes = 0;
        classMethodCounts.clear();
        classLineCounts.clear();
        classAttributeCounts.clear();
    }
    
    private void analyzeFile(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        
        // Parser le code avec Eclipse JDT
        ASTParser parser = ASTParser.newParser(AST.JLS11);
        parser.setSource(content.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        
        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_11);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_11);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_11);
        parser.setCompilerOptions(options);
        
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        
        // Visiter l'AST
        cu.accept(new ASTVisitor() {
            
            private String currentClass = "";
            
            @Override
            public boolean visit(TypeDeclaration node) {
                if (!node.isInterface()) {
                    totalClasses++;
                    currentClass = node.getName().getIdentifier();
                    
                    // Compter les méthodes
                    int methodCount = node.getMethods().length;
                    totalMethods += methodCount;
                    classMethodCounts.put(currentClass, methodCount);
                    
                    // Compter les attributs
                    int attributeCount = node.getFields().length;
                    totalAttributes += attributeCount;
                    classAttributeCounts.put(currentClass, attributeCount);
                    
                    // Compter les lignes
                    int startLine = cu.getLineNumber(node.getStartPosition());
                    int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
                    int lineCount = endLine - startLine + 1;
                    totalLines += lineCount;
                    classLineCounts.put(currentClass, lineCount);
                }
                return true;
            }
        });
    }
    
    public int getTotalClasses() {
        return totalClasses;
    }
    
    public int getTotalLines() {
        return totalLines;
    }
    
    public int getTotalMethods() {
        return totalMethods;
    }
    
    public int getTotalAttributes() {
        return totalAttributes;
    }
    
    public double getAverageMethodsPerClass() {
        return totalClasses == 0 ? 0 : (double) totalMethods / totalClasses;
    }
    
    public double getAverageLinesPerMethod() {
        return totalMethods == 0 ? 0 : (double) totalLines / totalMethods;
    }
    
    public double getAverageAttributesPerClass() {
        return totalClasses == 0 ? 0 : (double) totalAttributes / totalClasses;
    }
    
    public List<String> getTopComplexClasses() {
        // Top 10% des classes les plus complexes (par nombre de méthodes)
        int topCount = Math.max(1, (int) Math.ceil(totalClasses * 0.1));
        
        return classMethodCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topCount)
                .map(entry -> String.format("%s (%d méthodes, %d lignes)", 
                        entry.getKey(), 
                        entry.getValue(), 
                        classLineCounts.getOrDefault(entry.getKey(), 0)))
                .collect(Collectors.toList());
    }
}
