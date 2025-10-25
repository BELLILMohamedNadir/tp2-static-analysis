package com.tp.tp2.analyzer;

import com.tp.tp2.model.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.JavaCore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Analyseur de couplage entre classes
 */
public class CouplingAnalyzer {
    
    private CouplingGraph couplingGraph;
    private List<CallRelation> callRelations;
    private Map<String, String> classMap; // Pour mapper les noms courts aux noms complets

    public CouplingAnalyzer() {
        this.couplingGraph = new CouplingGraph();
        this.callRelations = new ArrayList<>();
        this.classMap = new HashMap<>();
    }

    /**
     * Analyse un projet Java et construit le graphe de couplage
     */
    public CouplingGraph analyzeProject(String projectPath) {
        System.out.println("🔍 Analyse du projet : " + projectPath);
        
        // 1. Scanner tous les fichiers Java
        List<File> javaFiles = scanJavaFiles(new File(projectPath));
        System.out.println("📁 Fichiers Java trouvés : " + javaFiles.size());

        // 2. Analyser chaque fichier pour détecter les appels
        for (File javaFile : javaFiles) {
            analyzeFile(javaFile);
        }

        System.out.println("📞 Relations d'appel détectées : " + callRelations.size());

        // 3. Afficher quelques exemples
        if (!callRelations.isEmpty()) {
            System.out.println("\n🔍 Exemples de relations :");
            for (int i = 0; i < Math.min(5, callRelations.size()); i++) {
                System.out.println("  " + callRelations.get(i));
            }
        }

        // 4. Construire le graphe de couplage
        buildCouplingGraph();

        System.out.println("\n✅ Graphe construit !");
        System.out.println(couplingGraph);

        return couplingGraph;
    }

    /**
     * Scanner récursivement les fichiers Java
     */
    private List<File> scanJavaFiles(File directory) {
        List<File> javaFiles = new ArrayList<>();
        
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        javaFiles.addAll(scanJavaFiles(file));
                    } else if (file.getName().endsWith(".java")) {
                        javaFiles.add(file);
                    }
                }
            }
        }
        
        return javaFiles;
    }

    /**
     * Analyser un fichier Java
     */
    private void analyzeFile(File javaFile) {
        try {
            String source = new String(Files.readAllBytes(javaFile.toPath()));
            
            ASTParser parser = ASTParser.newParser(AST.JLS11);
            parser.setSource(source.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setResolveBindings(true);  // Activer la résolution
            parser.setBindingsRecovery(true);
            
            Map<String, String> options = JavaCore.getOptions();
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_11);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_11);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_11);
            parser.setCompilerOptions(options);
            
            // Configuration pour la résolution des bindings
            parser.setUnitName(javaFile.getName());
            parser.setEnvironment(null, null, null, true);

            CompilationUnit cu = (CompilationUnit) parser.createAST(null);
            
            // Visiter l'AST pour trouver les appels de méthodes
            cu.accept(new MethodCallVisitor());
            
        } catch (IOException e) {
            System.err.println("❌ Erreur lecture fichier : " + javaFile.getName());
        }
    }

    /**
     * Construire le graphe de couplage à partir des relations d'appel
     */
    private void buildCouplingGraph() {
        for (CallRelation relation : callRelations) {
            String classA = relation.getCallerClass();
            String classB = relation.getCalleeClass();
            
            if (classA != null && classB != null && 
                !classA.isEmpty() && !classB.isEmpty() && 
                !classA.equals(classB)) {
                couplingGraph.addRelation(classA, classB);
            }
        }
        
        // Calculer les métriques
        couplingGraph.computeCouplingMetrics();
    }

    /**
     * Visitor pour détecter les appels de méthodes
     */
    private class MethodCallVisitor extends ASTVisitor {
        private String currentClass = "";
        private String currentMethod = "";
        private String currentPackage = "";

        @Override
        public boolean visit(PackageDeclaration node) {
            currentPackage = node.getName().getFullyQualifiedName();
            return super.visit(node);
        }

        @Override
        public boolean visit(TypeDeclaration node) {
            currentClass = node.getName().getIdentifier();
            classMap.put(currentClass, currentPackage + "." + currentClass);
            return super.visit(node);
        }

        @Override
        public boolean visit(MethodDeclaration node) {
            currentMethod = node.getName().getIdentifier();
            return super.visit(node);
        }

        @Override
        public boolean visit(MethodInvocation node) {
            String calledMethod = node.getName().getIdentifier();
            
            // Essayer de déterminer la classe appelée
            String calledClass = extractClassName(node);
            
            if (calledClass != null && !calledClass.isEmpty() && 
                !calledClass.equals("System") && !calledClass.equals("String")) {
                
                CallRelation relation = new CallRelation(
                    currentClass, currentMethod,
                    calledClass, calledMethod
                );
                callRelations.add(relation);
            }
            
            return super.visit(node);
        }

        @Override
        public boolean visit(ClassInstanceCreation node) {
            // Détecter les instanciations (new ClassName())
            Type type = node.getType();
            if (type instanceof SimpleType) {
                String className = ((SimpleType) type).getName().getFullyQualifiedName();
                
                if (!className.equals("String") && !className.isEmpty()) {
                    CallRelation relation = new CallRelation(
                        currentClass, currentMethod,
                        className, "<init>"
                    );
                    callRelations.add(relation);
                }
            }
            
            return super.visit(node);
        }

        /**
         * Extraire le nom de la classe depuis l'invocation
         */
        private String extractClassName(MethodInvocation node) {
            Expression expression = node.getExpression();
            
            // Appel direct : maMethode() -> même classe
            if (expression == null) {
                return currentClass;
            }
            
            // Appel sur variable : obj.methode()
            if (expression instanceof SimpleName) {
                SimpleName name = (SimpleName) expression;
                ITypeBinding typeBinding = name.resolveTypeBinding();
                if (typeBinding != null) {
                    return typeBinding.getName();
                }
                return name.getIdentifier();
            }
            
            // Appel sur champ : this.field.methode()
            if (expression instanceof FieldAccess) {
                FieldAccess fieldAccess = (FieldAccess) expression;
                ITypeBinding typeBinding = fieldAccess.resolveTypeBinding();
                if (typeBinding != null) {
                    return typeBinding.getName();
                }
                return fieldAccess.getName().getIdentifier();
            }
            
            // Résolution via binding
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                return typeBinding.getName();
            }
            
            // Fallback
            String exprStr = expression.toString();
            return exprStr.contains(".") ? exprStr.substring(0, exprStr.indexOf('.')) : exprStr;
        }
    }

    public List<CallRelation> getCallRelations() {
        return callRelations;
    }
}
