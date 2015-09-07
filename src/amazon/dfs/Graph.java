/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazon.dfs;

import dblp.xml.IOUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author Aale
 */
public class Graph {

    static Set<Integer> marked = new HashSet<Integer>();
    static Map<Integer, List<Integer>> components = new LinkedHashMap<Integer, List<Integer>>();

    static String node_file;
    static String edge_file;

    public static void main(String[] args) {
        node_file = args[0];
        edge_file = args[1];
//        node_file = "F:\\Data\\SIGMOD_DATA\\Amazon\\output\\product2.txt";
//        edge_file = "F:\\Data\\SIGMOD_DATA\\Amazon\\output\\product_product2.txt";
        readGraph();
        for (Integer id : graph.keySet()) {
            if (!marked.contains(id)) {
                List<Integer> inThisComponent = new ArrayList<>();
                bfs(graph.get(id), inThisComponent);
                components.put(id, inThisComponent);
            }
        }

        for (Integer key : components.keySet()) {
            System.out.println(key + " >> " + components.get(key).size());
        }
    }
    static Map<Integer, Node> graph = new LinkedHashMap<Integer, Node>();

    public static void readGraph() {
        final List<String> node_lines = IOUtils.readFileLineByLine(node_file, false);
        for (String line : node_lines) {
            String[] split = line.split("\\t", 2);
            int id = Integer.parseInt(split[0]);
            String name = null; // split[1];
            Node n = new Node(id, name);
            graph.put(id, n);
        }
        final List<String> edge_lines = IOUtils.readFileLineByLine(edge_file, false);
        for (String line : edge_lines) {
            String[] split = line.split("\\t", 2);
            int node1 = Integer.parseInt(split[0]);
            int node2 = Integer.parseInt(split[1]);
            Node n1 = graph.get(node1);
            Node n2 = graph.get(node2);
            if (n1 != null && n2 != null) {
                n1.addNeighbour(n2);
                n2.addNeighbour(n1);
            } else {
                System.out.println("node1 or node2 is null!!!");
            }
        }
    }

    private static void dfs(Node node, List<Integer> inThisComponent) {
        marked.add(node.id);
        inThisComponent.add(node.id);
        for (Node nb : node.neighbours) {
            if (!marked.contains(nb.id)) {
                dfs(nb, inThisComponent);
            }
        }
    }

    private static void bfs(Node node, List<Integer> inThisComponent) {
        Queue<Node> q = new LinkedList<>();
        q.add(node);
        while (!q.isEmpty()) {
            Node head = q.peek();
            marked.add(head.id);
            inThisComponent.add(head.id);
            for (Node nb : head.neighbours) {
                if (!marked.contains(nb.id)) {
                    q.add(nb);
                }
            }
            q.poll();
        }
    }

}
