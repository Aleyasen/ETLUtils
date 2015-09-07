/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Aale
 */
public class DBIS {

    public static void main(String[] args) {
//        String path = "data//DBIS//paper_conf.txt";
        String path = "data//DBIS//DBLP//title_conf.txt";
        DBLPParserSchema parser = new DBLPParserSchema();
        final List<Edge> paper_conf_edges = parser.readEdgeFile(path);
        Map<Integer, Integer> conf_pop = new HashMap<Integer, Integer>();
        for (Edge e : paper_conf_edges) {
            Integer val = conf_pop.get(e.node2);
            if (val == null) {
                conf_pop.put(e.node2, 1);
            } else {
                conf_pop.put(e.node2, val + 1);
            }
        }
        final Map<Integer, Integer> sorted_map = MapUtil.sortByValue(conf_pop);
        System.out.println(sorted_map);
//        String node_path = "data//DBIS//conf.txt";
        String node_path = "data//DBIS//DBLP//conf.txt";
        final Map<Integer, String> conf_nodes = parser.readNodeFile(node_path);
        for (Integer conf : sorted_map.keySet()) {
            String name = conf_nodes.get(conf);
            if (name == null) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
            } else {
                System.out.println(conf + "\t" + name);
            }
        }
    }
}
