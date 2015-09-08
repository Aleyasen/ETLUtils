/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACE;

/**
 *
 * @author Aale
 */
public class Relation {

    String id;
    String type;
    String subtype;
    String relclass;
    Entity ent1;
    Entity ent2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public Entity getEnt1() {
        return ent1;
    }

    public void setEnt1(Entity ent1) {
        this.ent1 = ent1;
    }

    public Entity getEnt2() {
        return ent2;
    }

    public void setEnt2(Entity ent2) {
        this.ent2 = ent2;
    }

    public String getRelclass() {
        return relclass;
    }

    public void setRelclass(String relclass) {
        this.relclass = relclass;
    }

}
