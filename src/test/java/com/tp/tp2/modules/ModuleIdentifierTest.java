package com.tp.tp2.modules;
import com.tp.tp2.clustering.DendrogramNode;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class ModuleIdentifierTest {
    @Test
    void testIdentifyModules() {
        DendrogramNode n1 = new DendrogramNode("A");
        DendrogramNode n2 = new DendrogramNode("B");
        DendrogramNode root = new DendrogramNode(n1, n2, 0.3, 1);
        ModuleIdentifier identifier = new ModuleIdentifier();
        List<Module> modules = identifier.identifyModules(root, 0.5);
        assertNotNull(modules);
        assertEquals(1, modules.size());
        assertEquals(2, modules.get(0).getSize());
    }
}
