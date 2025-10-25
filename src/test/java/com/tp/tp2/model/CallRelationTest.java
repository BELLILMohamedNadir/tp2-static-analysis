package com.tp.tp2.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CallRelationTest {
    
    @Test
    void testCallRelationCreation() {
        CallRelation relation = new CallRelation("ClassA", "methodA", "ClassB", "methodB");
        
        assertEquals("ClassA", relation.getCallerClass());
        assertEquals("methodA", relation.getCallerMethod());
        assertEquals("ClassB", relation.getCalleeClass());
        assertEquals("methodB", relation.getCalleeMethod());
    }
    
    @Test
    void testToString() {
        CallRelation relation = new CallRelation("Main", "main", "Helper", "help");
        // Ajuster selon le format réel de toString()
        String result = relation.toString();
        assertTrue(result.contains("Main"));
        assertTrue(result.contains("main"));
        assertTrue(result.contains("Helper"));
        assertTrue(result.contains("help"));
    }
    
    @Test
    void testObjectCreation() {
        CallRelation r1 = new CallRelation("A", "m1", "B", "m2");
        CallRelation r2 = new CallRelation("A", "m1", "B", "m2");
        CallRelation r3 = new CallRelation("A", "m1", "C", "m3");
        
        // Test que les objets sont bien créés
        assertNotNull(r1);
        assertNotNull(r2);
        assertNotNull(r3);
        
        // Vérifier que les valeurs sont correctes
        assertEquals("A", r1.getCallerClass());
        assertEquals("B", r2.getCalleeClass());
        assertNotEquals(r1.getCalleeClass(), r3.getCalleeClass());
    }
}
