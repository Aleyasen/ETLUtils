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
public class FCharacter extends Entity {

    
    List<Starring> starrings;
    
    public FCharacter(String mid, String name) {
        super(mid, name);
    }

    public FCharacter() {
    }

    public FCharacter(int id, String mid, String name) {
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
    
    public static List<FCharacter> to(List<Entity> list) {
        List<FCharacter> list2 = new ArrayList<>();
        for (Entity ent : list) {
            FCharacter e = new FCharacter(ent.id, ent.mid, ent.name);
            list2.add(e);
        }
        return list2;
    }
    
    public static FCharacter find(int id, List<FCharacter> ents) {
        for (FCharacter e : ents) {
            if (e.id == id) {
                return e;
            }
        }
        return null;
    }

}
