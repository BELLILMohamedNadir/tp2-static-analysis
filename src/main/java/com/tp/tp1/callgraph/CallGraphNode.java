package com.tp.tp1.callgraph;

import java.util.*;

/**
 * Represents a node in the call graph (a method)
 * Tracks bidirectional call relationships
 */
public class CallGraphNode {
    private final String className;
    private final String methodName;
    private final String signature;
    private final Set<CallGraphNode> calledBy;    // Who calls this method
    private final Set<CallGraphNode> calls;        // This method calls who
    
    public CallGraphNode(String className, String methodName, String signature) {
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.calledBy = new HashSet<>();
        this.calls = new HashSet<>();
    }
    
    /**
     * Add a call from this method to target method
     */
    public void addCall(CallGraphNode target) {
        this.calls.add(target);
        target.calledBy.add(this);
    }
    
    /**
     * Get full method name: ClassName.methodName(signature)
     */
    public String getFullName() {
        return className + "." + methodName + signature;
    }
    
    // Getters
    public String getClassName() { return className; }
    public String getMethodName() { return methodName; }
    public String getSignature() { return signature; }
    public Set<CallGraphNode> getCalledBy() { return Collections.unmodifiableSet(calledBy); }
    public Set<CallGraphNode> getCalls() { return Collections.unmodifiableSet(calls); }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallGraphNode)) return false;
        CallGraphNode that = (CallGraphNode) o;
        return Objects.equals(getFullName(), that.getFullName());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getFullName());
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
}
