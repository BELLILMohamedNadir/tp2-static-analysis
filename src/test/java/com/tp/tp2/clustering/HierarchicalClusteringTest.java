package com.tp.tp2.clustering;
import com.tp.tp2.model.CouplingGraph;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
class HierarchicalClusteringTest {
    @Test
    void testBuildDendrogram() {
        CouplingGraph graph = new CouplingGraph();
        graph.addRelation("A", "B");
        graph.addRelation("B", "C");
        graph.computeCouplingMetrics();
        HierarchicalClustering clustering = new HierarchicalClustering();
        DendrogramNode root = clustering.buildDendrogram(graph);
        assertNotNull(root);
        assertEquals(3, root.getClasses().size());
    }
}
