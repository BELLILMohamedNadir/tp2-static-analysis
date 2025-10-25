package com.tp.tp2.graph;

import com.tp.tp2.model.CouplingGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class CouplingGraphExporterTest {
    
    private CouplingGraphExporter exporter;
    private CouplingGraph graph;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        exporter = new CouplingGraphExporter();
        graph = new CouplingGraph();
        
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassB", "ClassC");
        graph.computeCouplingMetrics();
    }
    
    @Test
    void testExportToDot() throws IOException {
        Path dotFile = tempDir.resolve("graph.dot");
        
        exporter.exportToDot(graph, dotFile.toString());
        
        assertTrue(Files.exists(dotFile));
        String content = Files.readString(dotFile);
        
        assertTrue(content.contains("digraph"));
        assertTrue(content.contains("ClassA"));
        assertTrue(content.contains("ClassB"));
        assertTrue(content.contains("->"));
    }
    
    @Test
    void testExportEmptyGraph() throws IOException {
        Path dotFile = tempDir.resolve("empty.dot");
        CouplingGraph emptyGraph = new CouplingGraph();
        
        exporter.exportToDot(emptyGraph, dotFile.toString());
        
        assertTrue(Files.exists(dotFile));
        String content = Files.readString(dotFile);
        assertTrue(content.contains("digraph"));
    }
    
    @Test
    void testGenerateImage() throws IOException {
        Path dotFile = tempDir.resolve("graph.dot");
        Path pngFile = tempDir.resolve("graph.png");
        
        exporter.exportToDot(graph, dotFile.toString());
        boolean success = exporter.generateImage(dotFile.toString(), pngFile.toString());
        
        // Test may fail if Graphviz not installed
        if (success) {
            assertTrue(Files.exists(pngFile));
            assertTrue(Files.size(pngFile) > 0);
        }
    }
}
