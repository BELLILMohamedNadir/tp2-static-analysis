package com.tp.tp1.callgraph;

import org.eclipse.jdt.core.dom.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Builds a method call graph from Java source code
 * Uses Eclipse JDT for AST parsing
 */
public class CallGraphBuilder {
    
    private final Map<String, CallGraphNode> nodes;
    private final ASTParser parser;
    
    public CallGraphBuilder() {
        this.nodes = new HashMap<>();
        this.parser = ASTParser.newParser(AST.JLS17);
        this.parser.setKind(ASTParser.K_COMPILATION_UNIT);
    }
    
    /**
     * Analyze a Java project directory
     */
    public void analyzeProject(String projectPath) throws IOException {
        Files.walk(Paths.get(projectPath))
            .filter(path -> path.toString().endsWith(".java"))
            .forEach(path -> {
                try {
                    analyzeFile(path.toString());
                } catch (IOException e) {
                    System.err.println("Error analyzing: " + path);
                }
            });
    }
    
    /**
     * Analyze a single Java file
     */
    private void analyzeFile(String filePath) throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(filePath)));
        
        parser.setSource(source.toCharArray());
        parser.setUnitName(filePath);
        
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        
        cu.accept(new CallGraphVisitor());
    }
    
    /**
     * AST Visitor to extract method declarations and invocations
     */
    private class CallGraphVisitor extends ASTVisitor {
        private String currentClass = "";
        private String currentMethod = "";
        private String currentSignature = "";
        
        @Override
        public boolean visit(TypeDeclaration node) {
            currentClass = node.getName().getIdentifier();
            return true;
        }
        
        @Override
        public boolean visit(MethodDeclaration node) {
            currentMethod = node.getName().getIdentifier();
            currentSignature = getMethodSignature(node);
            
            // Create node for current method
            String fullName = currentClass + "." + currentMethod + currentSignature;
            nodes.putIfAbsent(fullName, 
                new CallGraphNode(currentClass, currentMethod, currentSignature));
            
            return true;
        }
        
        @Override
        public boolean visit(MethodInvocation node) {
            String calledMethod = node.getName().getIdentifier();
            String calledClass = extractClassName(node);
            
            // Create caller and callee nodes
            String callerKey = currentClass + "." + currentMethod + currentSignature;
            String calleeKey = calledClass + "." + calledMethod + "()";
            
            CallGraphNode caller = nodes.get(callerKey);
            CallGraphNode callee = nodes.computeIfAbsent(calleeKey,
                k -> new CallGraphNode(calledClass, calledMethod, "()"));
            
            if (caller != null) {
                caller.addCall(callee);
            }
            
            return true;
        }
        
        /**
         * Extract method signature with parameter types
         */
        private String getMethodSignature(MethodDeclaration method) {
            StringBuilder sig = new StringBuilder("(");
            List<?> params = method.parameters();
            
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) sig.append(", ");
                SingleVariableDeclaration param = (SingleVariableDeclaration) params.get(i);
                sig.append(param.getType().toString());
            }
            
            sig.append(")");
            return sig.toString();
        }
        
        /**
         * Extract class name from method invocation
         */
        private String extractClassName(MethodInvocation node) {
            Expression expr = node.getExpression();
            
            if (expr == null) {
                return currentClass; // Same class
            }
            
            if (expr instanceof SimpleName) {
                return ((SimpleName) expr).getIdentifier();
            }
            
            return "Unknown";
        }
    }
    
    /**
     * Get all nodes in the call graph
     */
    public Collection<CallGraphNode> getNodes() {
        return nodes.values();
    }
    
    /**
     * Export graph to text format
     */
    public String toGraphFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CALL GRAPH ===\n\n");
        
        for (CallGraphNode node : nodes.values()) {
            sb.append(node.getFullName()).append("\n");
            
            if (!node.getCalls().isEmpty()) {
                sb.append("  Calls:\n");
                for (CallGraphNode called : node.getCalls()) {
                    sb.append("    → ").append(called.getFullName()).append("\n");
                }
            }
            
            if (!node.getCalledBy().isEmpty()) {
                sb.append("  Called by:\n");
                for (CallGraphNode caller : node.getCalledBy()) {
                    sb.append("    ← ").append(caller.getFullName()).append("\n");
                }
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Export to DOT format (Graphviz)
     */
    public String toDotFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph CallGraph {\n");
        sb.append("  rankdir=LR;\n");
        sb.append("  node [shape=box, style=rounded];\n\n");
        
        for (CallGraphNode node : nodes.values()) {
            String nodeId = sanitizeId(node.getFullName());
            
            for (CallGraphNode called : node.getCalls()) {
                String calledId = sanitizeId(called.getFullName());
                sb.append("  \"").append(nodeId).append("\" -> \"")
                  .append(calledId).append("\";\n");
            }
        }
        
        sb.append("}\n");
        return sb.toString();
    }
    
    /**
     * Sanitize string for DOT format
     */
    private String sanitizeId(String id) {
        return id.replace("\"", "\\\"");
    }
    
    /**
     * Get statistics about the call graph
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalNodes", nodes.size());
        stats.put("totalEdges", nodes.values().stream()
            .mapToInt(n -> n.getCalls().size()).sum());
        
        // Find entry points (methods never called)
        List<String> entryPoints = new ArrayList<>();
        for (CallGraphNode node : nodes.values()) {
            if (node.getCalledBy().isEmpty()) {
                entryPoints.add(node.getFullName());
            }
        }
        stats.put("entryPoints", entryPoints);
        
        // Find leaf methods (methods that don't call others)
        List<String> leafMethods = new ArrayList<>();
        for (CallGraphNode node : nodes.values()) {
            if (node.getCalls().isEmpty()) {
                leafMethods.add(node.getFullName());
            }
        }
        stats.put("leafMethods", leafMethods);
        
        return stats;
    }
}
