/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api.entity.movie;

import freebase.api.Utils;
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
public class Entity {

    int id;
    String mid;
    String name;

    public Entity() {
    }

    public Entity(String mid, String name) {
        this.mid = mid;
        this.name = name;
    }

    public Entity(int id, String mid, String name) {
        this.id = id;
        this.mid = mid;
        this.name = name;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Entity{ id= " + id + ", mid=" + mid + ", name=" + name + '}';
    }

    public static List<Entity> readNodeFile(String filepath) {
        List<Entity> list = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            String line = br.readLine();

            while (line != null) {
                String split[] = line.split("\\t");
                int id = Integer.parseInt(split[0]);
                String name = split[1];
                Entity ent = new Entity(id, null, name);
                list.add(ent);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final Entity other = (Entity) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public static void writeNodesToFile(Collection<Entity> list, String filepath) {
        StringBuilder strbuilder = new StringBuilder();
        for (Entity ent : list) {
            strbuilder.append(ent.getId()).append(Utils.SEPERATOR).append(ent.getName()).append(Utils.NEWLINE);
        }
        Utils.writeDataIntoFile(strbuilder.toString(), filepath);
    }

}
