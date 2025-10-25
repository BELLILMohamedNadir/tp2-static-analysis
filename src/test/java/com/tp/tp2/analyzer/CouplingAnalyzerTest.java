package com.tp.tp2.analyzer;

import com.tp.tp2.model.CouplingGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class CouplingAnalyzerTest {
    
    private CouplingAnalyzer analyzer;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        analyzer = new CouplingAnalyzer();
    }
    
    @Test
    void testAnalyzeSimpleProject() throws IOException {
        // Create test files
        String mainClass = "package test;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        Helper.help();\n" +
            "    }\n" +
            "}\n";
        
        String helperClass = "package test;\n" +
            "public class Helper {\n" +
            "    public static void help() {\n" +
            "        System.out.println(\"Help\");\n" +
            "    }\n" +
            "}\n";
        
        Path mainFile = tempDir.resolve("Main.java");
        Path helperFile = tempDir.resolve("Helper.java");
        
        Files.writeString(mainFile, mainClass);
        Files.writeString(helperFile, helperClass);
        
        CouplingGraph graph = analyzer.analyzeProject(tempDir.toString());
        
        assertNotNull(graph);
        assertTrue(graph.getClassCount() >= 2);
        assertTrue(graph.getTotalRelations() > 0);
    }
    
    @Test
    void testAnalyzeEmptyDirectory() {
        CouplingGraph graph = analyzer.analyzeProject(tempDir.toString());
        
        assertNotNull(graph);
        assertEquals(0, graph.getClassCount());
        assertEquals(0, graph.getTotalRelations());
    }
    
    @Test
    void testAnalyzeNonExistentDirectory() {
        CouplingGraph graph = analyzer.analyzeProject("/non/existent/path");
        
        assertNotNull(graph);
        assertEquals(0, graph.getClassCount());
    }
}
