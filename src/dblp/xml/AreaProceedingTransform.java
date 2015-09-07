/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Aale
 */
public class AreaProceedingTransform {

    public static void main(String[] args) {

        String root_dir = "C:\\Data\\SIGMOD_DATA\\data\\dblp_year_metapath_data\\100conf_dataset\\";
//        generateConfAreaFile(root_dir);
//        generateProcAreaFile(root_dir);
        generateTitleAreaFile(root_dir);
    }

    public static void generateConfAreaFile(String root_dir) {
        String map_conf_area_file = root_dir + "map_conf_area.txt";
        String area_file = root_dir + "area.txt";
        String proc_area_file = root_dir + "proc_area.txt";
        String conf_area_file = root_dir + "conf_area.txt";
        String proc_conf_file = root_dir + "proc_conf.txt";
        int area_index = 1;
        List<Edge> proc_area_edges = new ArrayList<>();
        List<Edge> conf_area_edges = new ArrayList<>();
        Map<String, Integer> area_nodes = new HashMap<>();
        final List<String> lines = IOUtils.readFileLineByLine(map_conf_area_file, false);
        for (String l : lines) {
            String[] split = l.split("\\t");
            int conf_id = Integer.parseInt(split[0]);
            String conf_name = split[1];
            String area = split[2];
            if (!area_nodes.containsKey(area)) {
                area_nodes.put(area, area_index);
                area_index++;
            }
            final Integer area_id = area_nodes.get(area);
            conf_area_edges.add(new Edge(conf_id, area_id));
        }
        DBLPParserSchema parser = new DBLPParserFirstSchema();
        Map<Integer, String> inv_area_nodes = new HashMap<>();
        for (String area_n : area_nodes.keySet()) {
            inv_area_nodes.put(area_nodes.get(area_n), area_n);
        }
        parser.writeNodesToFile(inv_area_nodes, area_file);
        parser.writeEdgesToFile(conf_area_edges, conf_area_file);

    }

    public static void generateProcAreaFile(String root_dir) {
        String proc_area_file = root_dir + "proc_area.txt";

        String path_conf_area = root_dir + "conf_area.txt";
        DBLPParserSchema parser1 = new DBLPParserSchema();
        final List<Edge> conf_area_edges = parser1.readEdgeFile(path_conf_area, true);
        Map<Integer, Integer> conf_area_map = new HashMap<Integer, Integer>();
        for (Edge e : conf_area_edges) {
            System.out.println(e.node1 + "\t" + e.node2 + " conf_area");
            conf_area_map.put(e.node1, e.node2);
        }

        String path_proc_conf = root_dir + "proc_conf.txt";
        DBLPParserSchema parser2 = new DBLPParserSchema();
        final List<Edge> proc_conf_edges = parser2.readEdgeFile(path_proc_conf, true);
        //Map<Integer, Integer> proc_area_map = new HashMap<Integer, Integer>();
        List<Edge> proc_area_edges = new ArrayList<>();
        for (Edge e : proc_conf_edges) {
            System.out.println(e.node1 + " \t " + e.node2 + "\t" + conf_area_map.get(e.node2));
            proc_area_edges.add(new Edge(e.node1, conf_area_map.get(e.node2)));
//            System.out.println(e.node1 + "\t" + proc_area_map.get(e.node2));
        }
        parser1.writeEdgesToFile(proc_area_edges, proc_area_file);
    }

    public static void generateTitleAreaFile(String root_dir) {
        String title_area_file = root_dir + "title_area.txt";

        String path_proc_area = root_dir + "proc_area.txt";
        DBLPParserSchema parser1 = new DBLPParserSchema();
        final List<Edge> proc_area_edges = parser1.readEdgeFile(path_proc_area, true);
        Map<Integer, Integer> proc_area_map = new HashMap<Integer, Integer>();
        for (Edge e : proc_area_edges) {
            proc_area_map.put(e.node1, e.node2);
        }

        String path_title_proc = root_dir + "title_proc.txt";
        DBLPParserSchema parser2 = new DBLPParserSchema();
        final List<Edge> title_proc_edges = parser2.readEdgeFile(path_title_proc, true);
        //Map<Integer, Integer> proc_area_map = new HashMap<Integer, Integer>();
        List<Edge> title_area_edges = new ArrayList<>();
        for (Edge e : title_proc_edges) {
            title_area_edges.add(new Edge(e.node1, proc_area_map.get(e.node2)));
//            System.out.println(e.node1 + "\t" + proc_area_map.get(e.node2));
        }
        parser1.writeEdgesToFile(title_area_edges, title_area_file);
    }
}
