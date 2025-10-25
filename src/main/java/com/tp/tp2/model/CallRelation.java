package com.tp.tp2.model;

/**
 * Représente une relation d'appel entre deux méthodes
 */
public class CallRelation {
    private final String callerClass;
    private final String callerMethod;
    private final String calleeClass;
    private final String calleeMethod;

    public CallRelation(String callerClass, String callerMethod, 
                       String calleeClass, String calleeMethod) {
        this.callerClass = callerClass;
        this.callerMethod = callerMethod;
        this.calleeClass = calleeClass;
        this.calleeMethod = calleeMethod;
    }

    public String getCallerClass() {
        return callerClass;
    }

    public String getCallerMethod() {
        return callerMethod;
    }

    public String getCalleeClass() {
        return calleeClass;
    }

    public String getCalleeMethod() {
        return calleeMethod;
    }

    @Override
    public String toString() {
        return callerClass + "." + callerMethod + " -> " + 
               calleeClass + "." + calleeMethod;
    }
}
