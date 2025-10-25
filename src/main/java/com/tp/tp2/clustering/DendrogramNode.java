package com.tp.tp2.clustering;
import java.util.*;
public class DendrogramNode {
    private String label;
    private Set<String> classes;
    private DendrogramNode left, right;
    private double distance;
    private int level;
    public DendrogramNode(String className) {
        this.label = className;
        this.classes = new HashSet<>();
        this.classes.add(className);
    }
    public DendrogramNode(DendrogramNode left, DendrogramNode right, double distance, int level) {
        this.left = left;
        this.right = right;
        this.distance = distance;
        this.level = level;
        this.classes = new HashSet<>();
        this.classes.addAll(left.getClasses());
        this.classes.addAll(right.getClasses());
        this.label = "Cluster" + level;
    }
    public String getLabel() { return label; }
    public Set<String> getClasses() { return classes; }
    public DendrogramNode getLeft() { return left; }
    public DendrogramNode getRight() { return right; }
    public double getDistance() { return distance; }
    public int getLevel() { return level; }
    public boolean isLeaf() { return left == null && right == null; }
}
