package com.tp.tp2.model;

/**
 * Représente le couplage entre deux classes A et B
 */
public class ClassCoupling {
    private final String classA;
    private final String classB;
    private int relationCount;  // Nombre de relations entre A et B
    private double couplingMetric;  // Métrique normalisée [0,1]

    public ClassCoupling(String classA, String classB) {
        this.classA = classA;
        this.classB = classB;
        this.relationCount = 0;
        this.couplingMetric = 0.0;
    }

    public void incrementRelationCount() {
        this.relationCount++;
    }

    public void setCouplingMetric(double couplingMetric) {
        this.couplingMetric = couplingMetric;
    }

    public String getClassA() {
        return classA;
    }

    public String getClassB() {
        return classB;
    }

    public int getRelationCount() {
        return relationCount;
    }

    public double getCouplingMetric() {
        return couplingMetric;
    }

    @Override
    public String toString() {
        return String.format("%s <-> %s : %d relations (%.4f)", 
            classA, classB, relationCount, couplingMetric);
    }
}
