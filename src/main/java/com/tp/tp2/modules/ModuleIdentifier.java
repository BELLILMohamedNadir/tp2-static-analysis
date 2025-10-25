package com.tp.tp2.modules;
import com.tp.tp2.clustering.DendrogramNode;
import java.util.*;
public class ModuleIdentifier {
    public List<Module> identifyModules(DendrogramNode root, double threshold) {
        List<Module> modules = new ArrayList<>();
        int id = 1;
        Queue<DendrogramNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            DendrogramNode n = q.poll();
            if (n.isLeaf()) {
                Module m = new Module(id++);
                m.addClass(n.getLabel());
                modules.add(m);
            } else if (n.getDistance() < threshold) {
                Module m = new Module(id++);
                n.getClasses().forEach(m::addClass);
                modules.add(m);
            } else {
                if (n.getLeft() != null) q.add(n.getLeft());
                if (n.getRight() != null) q.add(n.getRight());
            }
        }
        return modules;
    }
}
