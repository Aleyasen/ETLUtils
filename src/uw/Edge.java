/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Aale
 */
public class Edge {

    public int node1;
    public int node2;

    public Edge(int node1, int node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public Edge() {
    }

    public static List<Edge> readEdgeFile(String filepath) {
        List<Edge> list = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            // skip first line
            String line = br.readLine();
            line = br.readLine();

            while (line != null) {
                String split[] = line.split("\\t");
                int node1 = Integer.parseInt(split[0]);
                int node2 = Integer.parseInt(split[1]);
                Edge edge = new Edge(node1, node2);
                list.add(edge);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static void writeEdgesToFile(Collection<Edge> edges, String filepath) {
        writeEdgesToFile(edges, filepath, false);
    }

    public static void writeEdgesToFile(Collection<Edge> edges, String filepath, boolean hasHeader) {
        StringBuilder strbuilder = new StringBuilder();
        strbuilder.append("ntype1").append(Utils.SEPERATOR).append("ntype2").append(Utils.NEWLINE);
        for (Edge edge : edges) {
            strbuilder.append(edge.node1).append(Utils.SEPERATOR).append(edge.node2).append(Utils.NEWLINE);
        }
        Utils.writeDataIntoFile(strbuilder.toString(), filepath);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.node1;
        hash = 43 * hash + this.node2;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        if (this.node1 != other.node1) {
            return false;
        }
        if (this.node2 != other.node2) {
            return false;
        }
        return true;
    }

}
