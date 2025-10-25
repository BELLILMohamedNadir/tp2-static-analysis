package com.tp.tp2.modules;
import java.util.*;
public class Module {
    private int id;
    private Set<String> classes = new HashSet<>();
    public Module(int id) { this.id = id; }
    public void addClass(String c) { classes.add(c); }
    public int getId() { return id; }
    public Set<String> getClasses() { return classes; }
    public int getSize() { return classes.size(); }
    public String toString() { return "Module" + id + ": " + classes; }
}
