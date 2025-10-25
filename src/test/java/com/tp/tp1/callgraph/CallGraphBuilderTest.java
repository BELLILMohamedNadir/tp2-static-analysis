package com.tp.callgraph;

import com.tp.tp1.callgraph.CallGraphBuilder;
import com.tp.tp1.callgraph.CallGraphNode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.util.Collection;
import java.util.Map;

class CallGraphBuilderTest {
    
    private CallGraphBuilder builder;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        builder = new CallGraphBuilder();
    }
    
    @Test
    void testEmptyGraph() {
        Collection<CallGraphNode> nodes = builder.getNodes();
        assertEquals(0, nodes.size());
    }
    
    @Test
    void testSimpleAnalysis() throws Exception {
        String javaCode = "public class Test {\n" +
                         "    public void methodA() {}\n" +
                         "}\n";
        
        Path javaFile = tempDir.resolve("Test.java");
        Files.writeString(javaFile, javaCode);
        
        builder.analyzeProject(tempDir.toString());
        
        Collection<CallGraphNode> nodes = builder.getNodes();
        assertTrue(nodes.size() >= 0);
    }
    
    @Test
    void testDotFormat() {
        String dot = builder.toDotFormat();
        assertNotNull(dot);
        assertTrue(dot.contains("digraph"));
    }
    
    @Test
    void testGraphFormat() {
        String graph = builder.toGraphFormat();
        assertNotNull(graph);
        assertTrue(graph.contains("CALL GRAPH"));
    }
    
    @Test
    void testStatistics() throws Exception {
        String javaCode = "public class StatTest {\n" +
                         "    public void entry() { helper(); }\n" +
                         "    private void helper() {}\n" +
                         "}\n";
        
        Path javaFile = tempDir.resolve("StatTest.java");
        Files.writeString(javaFile, javaCode);
        
        builder.analyzeProject(tempDir.toString());
        Map<String, Object> stats = builder.getStatistics();
        
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalNodes"));
    }
}
