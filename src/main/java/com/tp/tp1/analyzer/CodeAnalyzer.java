package com.tp.tp1.analyzer;

import org.eclipse.jdt.core.dom.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class CodeAnalyzer {
    private List<ClassInfo> classes = new ArrayList<>();
    private Set<String> packages = new HashSet<>();

    public void analyzeSourceFile(String filePath) throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source.toCharArray());

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // Add package
        PackageDeclaration pkg = cu.getPackage();
        if (pkg != null) {
            packages.add(pkg.getName().toString());
        }

        // Visit top level types
        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration node) {
                ClassInfo ci = new ClassInfo(node, cu);
                classes.add(ci);
                return true;
            }
        });
    }

    public void analyzeDirectory(String directoryPath) throws IOException {
        Files.walk(Paths.get(directoryPath))
            .filter(path -> path.toString().endsWith(".java"))
            .forEach(path -> {
                try {
                    analyzeSourceFile(path.toString());
                } catch (IOException e) {
                    System.err.println("Error analyzing " + path + ": " + e.getMessage());
                }
            });
    }

    public void printStatistics(int methodThreshold) {
        System.out.println("=== Code Analysis Statistics ===\n");
        
        // 1. Number of classes
        int totalClasses = classes.size();
        System.out.println("1. Number of classes: " + totalClasses);
        
        // 2. Total lines of code
        int totalLinesOfCode = classes.stream()
            .mapToInt(c -> c.totalLinesOfCode)
            .sum();
        System.out.println("2. Total lines of code: " + totalLinesOfCode);
        
        // 3. Total number of methods
        int totalMethods = classes.stream()
            .mapToInt(ClassInfo::getMethodCount)
            .sum();
        System.out.println("3. Total number of methods: " + totalMethods);
        
        // 4. Total number of packages
        System.out.println("4. Total number of packages: " + packages.size());
        
        // 5. Average methods per class
        double avgMethodsPerClass = totalClasses > 0 ? (double) totalMethods / totalClasses : 0;
        System.out.println("5. Average methods per class: " + String.format("%.2f", avgMethodsPerClass));
        
        // 6. Average lines of code per method
        int totalMethodLines = classes.stream()
            .flatMap(c -> c.methods.stream())
            .mapToInt(m -> m.linesOfCode)
            .sum();
        int methodCount = classes.stream()
            .flatMap(c -> c.methods.stream())
            .mapToInt(m -> 1)
            .sum();
        double avgLinesPerMethod = methodCount > 0 ? (double) totalMethodLines / methodCount : 0;
        System.out.println("6. Average lines of code per method: " + String.format("%.2f", avgLinesPerMethod));
        
        // 7. Average attributes per class
        int totalAttributes = classes.stream()
            .mapToInt(ClassInfo::getAttributeCount)
            .sum();
        double avgAttributesPerClass = totalClasses > 0 ? (double) totalAttributes / totalClasses : 0;
        System.out.println("7. Average attributes per class: " + String.format("%.2f", avgAttributesPerClass));
        
        // 8. Top 10% classes with most methods
        List<ClassInfo> topMethodClasses = getTopPercentile(classes, 
            Comparator.comparingInt(ClassInfo::getMethodCount).reversed(), 0.1);
        System.out.println("\n8. Top 10% classes with most methods:");
        topMethodClasses.forEach(c -> 
            System.out.println("   - " + c.name + " (" + c.getMethodCount() + " methods)"));
        
        // 9. Top 10% classes with most attributes
        List<ClassInfo> topAttributeClasses = getTopPercentile(classes,
            Comparator.comparingInt(ClassInfo::getAttributeCount).reversed(), 0.1);
        System.out.println("\n9. Top 10% classes with most attributes:");
        topAttributeClasses.forEach(c ->
            System.out.println("   - " + c.name + " (" + c.getAttributeCount() + " attributes)"));
        
        // 10. Classes in both categories
        Set<String> topMethodNames = topMethodClasses.stream()
            .map(c -> c.name)
            .collect(Collectors.toSet());
        List<ClassInfo> inBothCategories = topAttributeClasses.stream()
            .filter(c -> topMethodNames.contains(c.name))
            .collect(Collectors.toList());
        System.out.println("\n10. Classes in both top 10% categories:");
        if (inBothCategories.isEmpty()) {
            System.out.println("   - None");
        } else {
            inBothCategories.forEach(c ->
                System.out.println("   - " + c.name));
        }
        
        // 11. Classes with more than X methods
        List<ClassInfo> classesAboveThreshold = classes.stream()
            .filter(c -> c.getMethodCount() > methodThreshold)
            .collect(Collectors.toList());
        System.out.println("\n11. Classes with more than " + methodThreshold + " methods:");
        if (classesAboveThreshold.isEmpty()) {
            System.out.println("   - None");
        } else {
            classesAboveThreshold.forEach(c ->
                System.out.println("   - " + c.name + " (" + c.getMethodCount() + " methods)"));
        }
        
        // 12. Top 10% methods with most lines of code (per class)
        System.out.println("\n12. Top 10% methods with most lines of code:");
        for (ClassInfo c : classes) {
            if (!c.methods.isEmpty()) {
                List<MethodInfo> topMethods = getTopPercentile(c.methods,
                    Comparator.comparingInt((MethodInfo m) -> m.linesOfCode).reversed(), 0.1);
                if (!topMethods.isEmpty()) {
                    System.out.println("   Class " + c.name + ":");
                    topMethods.forEach(m ->
                        System.out.println("      - " + m.name + " (" + m.linesOfCode + " lines)"));
                }
            }
        }
        
        // 13. Maximum parameters for all methods
        int maxParameters = classes.stream()
            .flatMap(c -> c.methods.stream())
            .mapToInt(m -> m.parameterCount)
            .max()
            .orElse(0);
        System.out.println("\n13. Maximum number of parameters: " + maxParameters);
        
        // Show which methods have max parameters
        System.out.println("    Methods with " + maxParameters + " parameters:");
        int finalMaxParameters = maxParameters;
        classes.forEach(c -> {
            c.methods.stream()
                .filter(m -> m.parameterCount == finalMaxParameters)
                .forEach(m -> System.out.println("      - " + c.name + "." + m.name));
        });
    }

    /**
     * Get statistics as a Map for GUI display
     * @param methodThreshold threshold for question 11
     * @return Map containing all statistics
     */
    public Map<String, Object> getStatistics(int methodThreshold) {
        Map<String, Object> stats = new HashMap<>();
        
        // Calculate all statistics
        int totalClasses = classes.size();
        int totalMethods = classes.stream().mapToInt(ClassInfo::getMethodCount).sum();
        int totalAttributes = classes.stream().mapToInt(ClassInfo::getAttributeCount).sum();
        int totalLinesOfCode = classes.stream().mapToInt(c -> c.totalLinesOfCode).sum();
        
        double avgMethodsPerClass = totalClasses > 0 ? (double) totalMethods / totalClasses : 0;
        double avgAttributesPerClass = totalClasses > 0 ? (double) totalAttributes / totalClasses : 0;
        
        int totalMethodLines = classes.stream()
            .flatMap(c -> c.methods.stream())
            .mapToInt(m -> m.linesOfCode)
            .sum();
        int methodCount = classes.stream().flatMap(c -> c.methods.stream()).mapToInt(m -> 1).sum();
        double avgLinesPerMethod = methodCount > 0 ? (double) totalMethodLines / methodCount : 0;
        
        // Top 10% classes
        List<ClassInfo> topMethodClasses = getTopPercentile(classes, 
            Comparator.comparingInt(ClassInfo::getMethodCount).reversed(), 0.1);
        List<ClassInfo> topAttributeClasses = getTopPercentile(classes,
            Comparator.comparingInt(ClassInfo::getAttributeCount).reversed(), 0.1);
        
        // Classes in both categories
        Set<String> topMethodNames = topMethodClasses.stream()
            .map(c -> c.name)
            .collect(Collectors.toSet());
        List<ClassInfo> inBothCategories = topAttributeClasses.stream()
            .filter(c -> topMethodNames.contains(c.name))
            .collect(Collectors.toList());
        
        // Classes above threshold
        List<ClassInfo> classesAboveThreshold = classes.stream()
            .filter(c -> c.getMethodCount() > methodThreshold)
            .collect(Collectors.toList());
        
        // Top methods per class
        Map<String, List<String>> topMethodsPerClass = new LinkedHashMap<>();
        for (ClassInfo c : classes) {
            if (!c.methods.isEmpty()) {
                List<MethodInfo> topMethods = getTopPercentile(c.methods,
                    Comparator.comparingInt((MethodInfo m) -> m.linesOfCode).reversed(), 0.1);
                if (!topMethods.isEmpty()) {
                    List<String> methodStrings = topMethods.stream()
                        .map(m -> m.name + " (" + m.linesOfCode + " lines)")
                        .collect(Collectors.toList());
                    topMethodsPerClass.put(c.name, methodStrings);
                }
            }
        }
        
        // Maximum parameters
        int maxParameters = classes.stream()
            .flatMap(c -> c.methods.stream())
            .mapToInt(m -> m.parameterCount)
            .max()
            .orElse(0);
        
        int finalMaxParameters = maxParameters;
        List<String> maxParameterMethods = new ArrayList<>();
        classes.forEach(c -> {
            c.methods.stream()
                .filter(m -> m.parameterCount == finalMaxParameters)
                .forEach(m -> maxParameterMethods.add(c.name + "." + m.name));
        });
        
        // Put all statistics in map
        stats.put("totalClasses", totalClasses);
        stats.put("totalLines", totalLinesOfCode);
        stats.put("totalMethods", totalMethods);
        stats.put("totalPackages", packages.size());
        stats.put("avgMethodsPerClass", avgMethodsPerClass);
        stats.put("avgLinesPerMethod", avgLinesPerMethod);
        stats.put("avgAttributesPerClass", avgAttributesPerClass);
        stats.put("topMethodClasses", topMethodClasses);
        stats.put("topAttributeClasses", topAttributeClasses);
        stats.put("inBothCategories", inBothCategories);
        stats.put("classesAboveThreshold", classesAboveThreshold);
        stats.put("topMethodsPerClass", topMethodsPerClass);
        stats.put("maxParameters", maxParameters);
        stats.put("maxParameterMethods", maxParameterMethods);
        
        return stats;
    }

    private <T> List<T> getTopPercentile(List<T> items, Comparator<T> comparator, double percentile) {
        if (items.isEmpty()) return new ArrayList<>();
        
        List<T> sorted = new ArrayList<>(items);
        sorted.sort(comparator);
        
        int count = Math.max(1, (int) Math.ceil(items.size() * percentile));
        return sorted.subList(0, Math.min(count, sorted.size()));
    }
}