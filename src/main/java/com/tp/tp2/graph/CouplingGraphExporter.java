package com.tp.tp2.graph;

import com.tp.tp2.model.ClassCoupling;
import com.tp.tp2.model.CouplingGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Export du graphe de couplage au format DOT (Graphviz)
 */
public class CouplingGraphExporter {

    /**
     * Exporte le graphe de couplage en format DOT
     */
    public void exportToDot(CouplingGraph graph, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("digraph CouplingGraph {\n");
            writer.write("  rankdir=LR;\n");
            writer.write("  node [shape=box, style=filled, fillcolor=lightblue];\n");
            writer.write("  edge [fontsize=10];\n\n");

            // Ajouter les nœuds (classes)
            for (String className : graph.getAllClasses()) {
                writer.write("  \"" + className + "\";\n");
            }

            writer.write("\n");

            // Ajouter les arêtes (couplages)
            List<ClassCoupling> couplings = graph.getAllCouplings();
            for (ClassCoupling coupling : couplings) {
                if (coupling.getRelationCount() > 0) { // Seuil minimal
                    String label = String.format("%.3f (%d)", 
                        coupling.getCouplingMetric(),
                        coupling.getRelationCount());
                    
                    // Épaisseur proportionnelle au couplage
                    double penwidth = 1 + (coupling.getCouplingMetric() * 5);
                    
                    writer.write(String.format("  \"%s\" -> \"%s\" [label=\"%s\", penwidth=%.2f];\n",
                        coupling.getClassA(),
                        coupling.getClassB(),
                        label,
                        penwidth));
                }
            }

            writer.write("}\n");
            
            System.out.println("✅ Graphe exporté : " + outputPath);
            
        } catch (IOException e) {
            System.err.println("❌ Erreur export : " + e.getMessage());
        }
    }

    /**
     * Génère l'image PNG du graphe avec Graphviz
     */
    public boolean generateImage(String dotFilePath, String pngFilePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFilePath, "-o", pngFilePath);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("✅ Image générée : " + pngFilePath);
                return true;
            } else {
                System.err.println("❌ Erreur génération image (code: " + exitCode + ")");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Graphviz non disponible : " + e.getMessage());
            return false;
        }
    }
}
