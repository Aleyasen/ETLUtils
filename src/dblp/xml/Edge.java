/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

/**
 *
 * @author aleyase2-admin
 */
public class Edge {

    int node1;
    int node2;

    public Edge(int node1, int node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.node1;
        hash = 37 * hash + this.node2;
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
