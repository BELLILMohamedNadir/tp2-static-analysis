package com.tp.tp2.spoon.analyzer;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.visitor.filter.TypeFilter;
import com.tp.tp2.model.*;
import java.util.*;

public class SpoonCouplingAnalyzer {
    
    private CouplingGraph graph;
    
    public SpoonCouplingAnalyzer() {
        this.graph = new CouplingGraph();
    }
    
    public CouplingGraph analyzeProject(String projectPath) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setComplianceLevel(11);
        
        CtModel model = launcher.buildModel();
        
        List<CtClass<?>> classes = model.getElements(new TypeFilter<>(CtClass.class));
        
        System.out.println("[SPOON] Classes trouvées : " + classes.size());
        
        // Analyser les appels
        int relationCount = 0;
        for (CtClass<?> ctClass : classes) {
            String className = ctClass.getQualifiedName();
            
            for (CtMethod<?> method : ctClass.getMethods()) {
                List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
                
                for (CtInvocation<?> invocation : invocations) {
                    try {
                        if (invocation.getExecutable().getDeclaringType() != null) {
                            String targetClass = invocation.getExecutable().getDeclaringType().getQualifiedName();
                            
                            if (!className.equals(targetClass)) {
                                graph.addRelation(className, targetClass);
                                relationCount++;
                            }
                        }
                    } catch (Exception e) {
                        // Ignorer
                    }
                }
            }
        }
        
        System.out.println("[SPOON] Relations ajoutées : " + relationCount);
        System.out.println("[SPOON] Classes dans le graphe : " + graph.getAllClasses().size());
        System.out.println("[SPOON] Relations dans le graphe : " + graph.getTotalRelations());
        
        return graph;
    }
    
    public CouplingGraph getGraph() {
        return graph;
    }
}
