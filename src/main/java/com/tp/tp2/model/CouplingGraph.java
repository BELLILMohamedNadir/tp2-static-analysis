package com.tp.tp2.model;

import java.util.*;

/**
 * Graphe de couplage entre les classes d'une application
 */
public class CouplingGraph {
    private Map<String, Map<String, ClassCoupling>> adjacencyMap;
    private int totalRelations;
    private Set<String> allClasses;

    public CouplingGraph() {
        this.adjacencyMap = new HashMap<>();
        this.allClasses = new HashSet<>();
        this.totalRelations = 0;
    }

    /**
     * Ajoute une relation entre deux classes
     */
    public void addRelation(String classA, String classB) {
        if (classA.equals(classB)) {
            return; // Ignorer les auto-relations
        }

        allClasses.add(classA);
        allClasses.add(classB);

        // Créer ou récupérer le couplage A->B
        adjacencyMap.putIfAbsent(classA, new HashMap<>());
        ClassCoupling coupling = adjacencyMap.get(classA)
            .computeIfAbsent(classB, k -> new ClassCoupling(classA, classB));
        
        coupling.incrementRelationCount();
        totalRelations++;
    }

    /**
     * Calcule les métriques de couplage pour toutes les paires
     */
    public void computeCouplingMetrics() {
        if (totalRelations == 0) {
            return;
        }

        for (Map<String, ClassCoupling> couplings : adjacencyMap.values()) {
            for (ClassCoupling coupling : couplings.values()) {
                double metric = (double) coupling.getRelationCount() / totalRelations;
                coupling.setCouplingMetric(metric);
            }
        }
    }

    /**
     * Récupère le couplage entre deux classes
     */
    public ClassCoupling getCoupling(String classA, String classB) {
        if (adjacencyMap.containsKey(classA)) {
            return adjacencyMap.get(classA).get(classB);
        }
        return null;
    }

    /**
     * Récupère tous les couplages
     */
    public List<ClassCoupling> getAllCouplings() {
        List<ClassCoupling> allCouplings = new ArrayList<>();
        for (Map<String, ClassCoupling> couplings : adjacencyMap.values()) {
            allCouplings.addAll(couplings.values());
        }
        return allCouplings;
    }

    public Set<String> getAllClasses() {
        return new HashSet<>(allClasses);
    }

    public int getTotalRelations() {
        return totalRelations;
    }

    public int getClassCount() {
        return allClasses.size();
    }

    @Override
    public String toString() {
        return String.format("CouplingGraph: %d classes, %d relations", 
            getClassCount(), totalRelations);
    }
}
