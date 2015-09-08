/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api.entity.movie;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aale
 */
public class Film extends Entity {

    Directedby directedBy;
    List<Starring> starrings;

    public Film() {
    }

    public Film(String mid, String name) {
        super(mid, name);
    }

    public Film(int id, String mid, String name) {
        super(id, mid, name);
    }

    public Directedby getDirectedBy() {
        return directedBy;
    }

    public void setDirectedBy(Directedby directedBy) {
        this.directedBy = directedBy;
    }

    public void setStarrings(List<Starring> starrings) {
        this.starrings = starrings;
    }

    public List<Starring> getStarrings() {
        return starrings;
    }

    public void addStarring(Starring starring) {
        if (starrings == null) {
            starrings = new ArrayList<>();
        }
        starrings.add(starring);
    }

    @Override
    public String toString() {
        return "Film{id = " + id + "\nmid= " + mid + "\nname=" + name + "\ndirectedby=" + directedBy + ", \nstarrings=" + starrings + '}';
    }

    public static List<Film> to(List<Entity> list) {
        List<Film> list2 = new ArrayList<>();
        for (Entity ent : list) {
            Film e = new Film(ent.id, ent.mid, ent.name);
            list2.add(e);
        }
        return list2;
    }

    public static Film find(int id, List<Film> ents) {
        for (Film e : ents) {
            if (e.id == id) {
                return e;
            }
        }
        return null;
    }

}
