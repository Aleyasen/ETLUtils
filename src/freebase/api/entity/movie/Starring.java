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
public class Starring extends Entity {

    Actor actor;
    FCharacter character;
    Film film;

    public Starring() {
    }

    public Starring(String mid, String name) {
        super(mid, name);
    }

    public Starring(int id, String mid, String name) {
        super(id, mid, name);
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Film getFilm() {
        return film;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    public void setCharacter(FCharacter character) {
        this.character = character;
    }

    public FCharacter getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return "\n\tStarring{ mid=" + mid + ", actor=" + actor + ", character=" + character + '}';
    }

    public static List<Starring> to(List<Entity> list) {
        List<Starring> list2 = new ArrayList<>();
        for (Entity ent : list) {
            Starring e = new Starring(ent.id, ent.mid, ent.name);
            list2.add(e);
        }
        return list2;
    }
    
    public static Starring find(int id, List<Starring> ents) {
        for (Starring e : ents) {
            if (e.id == id) {
                return e;
            }
        }
        return null;
    }

}
