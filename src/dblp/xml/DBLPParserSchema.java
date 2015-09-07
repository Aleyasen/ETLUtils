/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aleyase2-admin
 */
public class DBLPParserSchema {

    public List<Edge> readEdgeFile(String path) {
        return readEdgeFile(path, false);
    }

    public List<Edge> readEdgeFile(String path, boolean hasHeader) {
        try {
            List<Edge> edges = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = null;
            if (hasHeader) {
                line = br.readLine();
            }
            line = br.readLine();
            while (line != null) {
                String[] split = line.split("\\s+");
                edges.add(new Edge(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                line = br.readLine();
            }
            br.close();
            return edges;
        } catch (IOException ex) {
            Logger.getLogger(DBLPParserSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Map<Integer, String> readNodeFile(String path) {
        try {
            Map<Integer, String> nodes = new HashMap<>();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            while (line != null) {
                String[] split = line.split("\\s+");
                String other = line.substring(split[0].length() + 1);
                nodes.put(Integer.parseInt(split[0]), other);
                line = br.readLine();
            }
            br.close();
            return nodes;
        } catch (IOException ex) {
            Logger.getLogger(DBLPParserSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Integer> getTopAuthors(int count, String title_author_file) {
        List<Integer> topAuthors = new ArrayList<>();
        Map<Integer, Integer> pubCount = new HashMap<Integer, Integer>();
        final List<Edge> title_authors = readEdgeFile(title_author_file);
        for (Edge e : title_authors) {
            if (!pubCount.containsKey(e.node2)) {
                pubCount.put(e.node2, 0);
            }
            int c = pubCount.get(e.node2);
            pubCount.put(e.node2, c + 1);
        }
        pubCount = MapUtil.sortByValue(pubCount);
        int index = 0;
        for (Integer key : pubCount.keySet()) {
            if (index >= count) {
                break;
            }
            topAuthors.add(key);
            System.out.println(key + " " + pubCount.get(key));
            index++;
        }
        return topAuthors;
    }

    public List<Integer> getNeighbourEntities(List<Integer> nodes, List<Edge> edges, int neighbourColumn) {
        Set<Integer> other = new HashSet<>();
        Set<Integer> nodesSet = new HashSet<>();
        nodesSet.addAll(nodes);
        for (Edge e : edges) {
            if (neighbourColumn == 1) {
                if (nodesSet.contains(e.node1)) {
                    other.add(e.node2);
                }
            } else if (neighbourColumn == 0) {
                if (nodesSet.contains(e.node2)) {
                    other.add(e.node1);
                }
            }
        }
        return Arrays.asList(other.toArray(new Integer[0]));
    }

    public void writeNodesToFile(List<Integer> selected, Map<Integer, String> all, String path) {
        Writer writer = null;
        Set<Integer> selectedSet = new HashSet<>();
        selectedSet.addAll(selected);
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (Integer key : all.keySet()) {
                if (selectedSet.contains(key)) {
                    writer.write(key + " " + all.get(key) + "\n");
                }
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeNodesToFile(Map<Integer, String> all, String path) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (Integer key : all.keySet()) {
                writer.write(key + " " + all.get(key) + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeEdgesToFile(List<Integer> selected, List<Edge> all_edges, int columnIndex, String path) {
        Writer writer = null;
        Set<Integer> selectedSet = new HashSet<>();
        selectedSet.addAll(selected);
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (Edge e : all_edges) {
                if (columnIndex == 0) {
                    if (selectedSet.contains(e.node1)) {
                        writer.write(e.node1 + " " + e.node2 + "\n");
                    }
                } else if (columnIndex == 1) {
                    if (selectedSet.contains(e.node2)) {
                        writer.write(e.node1 + " " + e.node2 + "\n");
                    }
                }
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeEdgesToFile(List<Edge> all_edges, String path) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (Edge e : all_edges) {
                writer.write(e.node1 + " " + e.node2 + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static int topAuthorCount = 2000;
    static String conf_filter_path = "data/conf-select-100.txt";
    static int year_threshold = 2005;

    public static void createDir(String dir) {
        File theDir = new File(dir);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + dir);
            boolean result = theDir.mkdir();

            if (result) {
                System.out.println("DIR created");
            }
        }
    }
}
