/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api.entity;

import freebase.api.Utils;
import freebase.api.entity.movie.Entity;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

    public static void writeEdgesToFile(List<Edge> edges, String filepath) {
        StringBuilder strbuilder = new StringBuilder();
        for (Edge edge : edges) {
            strbuilder.append(edge.node1).append(Utils.SEPERATOR).append(edge.node2).append(Utils.NEWLINE);
        }
        Utils.writeDataIntoFile(strbuilder.toString(), filepath);
    }
}
