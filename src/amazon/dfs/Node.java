/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazon.dfs;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aale
 */
public class Node {

    int id;
    String name;
    List<Node> neighbours;

    public Node() {
        neighbours = new ArrayList<>();
    }

    public Node(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addNeighbour(Node n) {
        if (neighbours == null) {
            neighbours = new ArrayList<>();
        }
        neighbours.add(n);
    }

}
