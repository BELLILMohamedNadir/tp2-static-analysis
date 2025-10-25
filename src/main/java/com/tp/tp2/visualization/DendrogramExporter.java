package com.tp.tp2.visualization;
import com.tp.tp2.clustering.DendrogramNode;
import java.io.*;
import java.util.*;
public class DendrogramExporter {
    public void exportToDot(DendrogramNode root, String path) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            w.write("digraph D {\n  rankdir=TB;\n  node [shape=box, style=filled];\n");
            export(root, w, new HashSet<>());
            w.write("}\n");
        } catch (IOException e) {}
    }
    private void export(DendrogramNode n, BufferedWriter w, Set<DendrogramNode> v) throws IOException {
        if (n == null || v.contains(n)) return;
        v.add(n);
        String id = n.getLabel().replace(" ", "_");
        w.write("  \"" + id + "\" [label=\"" + n.getLabel() + "\", fillcolor=" + (n.isLeaf() ? "lightgreen" : "lightblue") + "];\n");
        if (!n.isLeaf()) {
            if (n.getLeft() != null) {
                w.write("  \"" + id + "\" -> \"" + n.getLeft().getLabel().replace(" ", "_") + "\";\n");
                export(n.getLeft(), w, v);
            }
            if (n.getRight() != null) {
                w.write("  \"" + id + "\" -> \"" + n.getRight().getLabel().replace(" ", "_") + "\";\n");
                export(n.getRight(), w, v);
            }
        }
    }
    public boolean generateImage(String dot, String png) {
        try { return new ProcessBuilder("dot", "-Tpng", dot, "-o", png).start().waitFor() == 0; } catch (Exception e) { return false; }
    }
}
