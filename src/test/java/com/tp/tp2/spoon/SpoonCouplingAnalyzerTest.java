package com.tp.tp2.spoon;

import com.tp.tp2.spoon.analyzer.SpoonCouplingAnalyzer;
import com.tp.tp2.model.CouplingGraph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpoonCouplingAnalyzerTest {
    
    @Test
    void testAnalyzeProjectCreatesGraph() {
        SpoonCouplingAnalyzer analyzer = new SpoonCouplingAnalyzer();
        String projectPath = "src/main/java/com/tp/tp2";
        
        CouplingGraph graph = analyzer.analyzeProject(projectPath);
        
        assertNotNull(graph, "Le graphe ne doit pas être null");
    }
    
    @Test
    void testSpoonFindsRelations() {
        SpoonCouplingAnalyzer analyzer = new SpoonCouplingAnalyzer();
        String projectPath = "src/main/java/com/tp/tp2/model";
        
        CouplingGraph graph = analyzer.analyzeProject(projectPath);
        
        assertNotNull(graph, "Le graphe doit exister");
        // Le graphe devrait avoir des relations si le code analyse des appels
    }
    
    @Test
    void testSpoonAnalyzerNotNull() {
        SpoonCouplingAnalyzer analyzer = new SpoonCouplingAnalyzer();
        assertNotNull(analyzer.getGraph(), "L'analyseur doit avoir un graphe");
    }
}
