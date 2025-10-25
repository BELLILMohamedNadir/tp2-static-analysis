package com.tp.tp2.clustering;
import com.tp.tp2.model.*;
import java.util.*;
public class HierarchicalClustering {
    public DendrogramNode buildDendrogram(CouplingGraph graph) {
        Map<String, DendrogramNode> clusters = new HashMap<>();
        for (String c : graph.getAllClasses()) clusters.put(c, new DendrogramNode(c));
        int level = 1;
        while (clusters.size() > 1) {
            String[] best = findMostCoupled(clusters, graph);
            if (best == null) break;
            DendrogramNode n1 = clusters.remove(best[0]);
            DendrogramNode n2 = clusters.remove(best[1]);
            clusters.put("C" + level, new DendrogramNode(n1, n2, Double.parseDouble(best[2]), level++));
        }
        return clusters.values().iterator().next();
    }
    private String[] findMostCoupled(Map<String, DendrogramNode> cls, CouplingGraph g) {
        double max = -1;
        String[] result = new String[3];
        List<String> keys = new ArrayList<>(cls.keySet());
        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                double c = calcCoupling(cls.get(keys.get(i)), cls.get(keys.get(j)), g);
                if (c > max) {
                    max = c;
                    result[0] = keys.get(i);
                    result[1] = keys.get(j);
                    result[2] = String.valueOf(c);
                }
            }
        }
        return result[0] == null ? null : result;
    }
    private double calcCoupling(DendrogramNode c1, DendrogramNode c2, CouplingGraph g) {
        double sum = 0;
        int cnt = 0;
        for (String a : c1.getClasses()) {
            for (String b : c2.getClasses()) {
                ClassCoupling cp = g.getCoupling(a, b);
                if (cp != null) {
                    sum += cp.getCouplingMetric();
                    cnt++;
                }
            }
        }
        return cnt > 0 ? sum / cnt : 0;
    }
}
