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
public class Actor extends Entity {

    
    List<Starring> starrings;
    
    public Actor(String mid, String name) {
        super(mid, name);
    }

    public Actor() {
    }

    public Actor(int id, String mid, String name) {
        super(id, mid, name);
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
    
    public static List<Actor> to(List<Entity> list) {
        List<Actor> list2 = new ArrayList<>();
        for (Entity ent : list) {
            Actor e = new Actor(ent.id, ent.mid, ent.name);
            list2.add(e);
        }
        return list2;
    }
    
    public static Actor find(int id, List<Actor> ents) {
        for (Actor e : ents) {
            if (e.id == id) {
                return e;
            }
        }
        return null;
    }

}
