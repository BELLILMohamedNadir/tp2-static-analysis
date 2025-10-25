package com.tp.tp2.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClassCouplingTest {
    
    @Test
    void testClassCouplingInitialization() {
        ClassCoupling coupling = new ClassCoupling("ClassA", "ClassB");
        
        assertEquals("ClassA", coupling.getClassA());
        assertEquals("ClassB", coupling.getClassB());
        assertEquals(0, coupling.getRelationCount());
        assertEquals(0.0, coupling.getCouplingMetric(), 0.001);
    }
    
    @Test
    void testIncrementRelationCount() {
        ClassCoupling coupling = new ClassCoupling("ClassA", "ClassB");
        
        coupling.incrementRelationCount();
        assertEquals(1, coupling.getRelationCount());
        
        coupling.incrementRelationCount();
        assertEquals(2, coupling.getRelationCount());
    }
    
    @Test
    void testSetCouplingMetric() {
        ClassCoupling coupling = new ClassCoupling("ClassA", "ClassB");
        
        coupling.setCouplingMetric(0.5);
        assertEquals(0.5, coupling.getCouplingMetric(), 0.001);
        
        coupling.setCouplingMetric(0.75);
        assertEquals(0.75, coupling.getCouplingMetric(), 0.001);
    }
    
    @Test
    void testToString() {
        ClassCoupling coupling = new ClassCoupling("Main", "Helper");
        coupling.incrementRelationCount();
        coupling.setCouplingMetric(0.25);
        
        String result = coupling.toString();
        assertTrue(result.contains("Main"));
        assertTrue(result.contains("Helper"));
        assertTrue(result.contains("0.25"));
    }
}
