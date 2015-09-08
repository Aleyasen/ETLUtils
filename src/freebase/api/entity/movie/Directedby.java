/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api.entity.movie;

/**
 *
 * @author Aale
 */
public class Directedby extends Entity {

    Director director;

    public Directedby() {
    }

    public Directedby(String mid, String name) {
        super(mid, name);
    }

    public Directedby(int id, String mid, String name) {
        super(id, mid, name);
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

}
