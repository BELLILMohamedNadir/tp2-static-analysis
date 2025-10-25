package com.tp.tp2.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class CouplingGraphTest {
    
    private CouplingGraph graph;
    
    @BeforeEach
    void setUp() {
        graph = new CouplingGraph();
    }
    
    @Test
    void testAddSingleRelation() {
        graph.addRelation("ClassA", "ClassB");
        
        ClassCoupling coupling = graph.getCoupling("ClassA", "ClassB");
        assertNotNull(coupling);
        assertEquals(1, coupling.getRelationCount());
    }
    
    @Test
    void testAddMultipleRelations() {
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassA", "ClassC");
        
        ClassCoupling couplingAB = graph.getCoupling("ClassA", "ClassB");
        assertEquals(2, couplingAB.getRelationCount());
        
        ClassCoupling couplingAC = graph.getCoupling("ClassA", "ClassC");
        assertEquals(1, couplingAC.getRelationCount());
    }
    
    @Test
    void testGetAllClasses() {
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassB", "ClassC");
        graph.addRelation("ClassC", "ClassA");
        
        Set<String> classes = graph.getAllClasses();
        assertEquals(3, classes.size());
        assertTrue(classes.contains("ClassA"));
        assertTrue(classes.contains("ClassB"));
        assertTrue(classes.contains("ClassC"));
    }
    
    @Test
    void testComputeCouplingMetrics() {
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassB", "ClassC");
        
        graph.computeCouplingMetrics();
        
        ClassCoupling couplingAB = graph.getCoupling("ClassA", "ClassB");
        ClassCoupling couplingBC = graph.getCoupling("ClassB", "ClassC");
        
        assertTrue(couplingAB.getCouplingMetric() > couplingBC.getCouplingMetric());
        assertEquals(2.0/3.0, couplingAB.getCouplingMetric(), 0.001);
        assertEquals(1.0/3.0, couplingBC.getCouplingMetric(), 0.001);
    }
    
    @Test
    void testGetAllCouplings() {
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassB", "ClassC");
        
        List<ClassCoupling> couplings = graph.getAllCouplings();
        assertEquals(2, couplings.size());
    }
    
    @Test
    void testGetClassCount() {
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassB", "ClassC");
        
        assertEquals(3, graph.getClassCount());
    }
    
    @Test
    void testGetTotalRelations() {
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassA", "ClassB");
        graph.addRelation("ClassB", "ClassC");
        
        assertEquals(3, graph.getTotalRelations());
    }
}
