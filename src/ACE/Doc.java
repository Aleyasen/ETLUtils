/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Aale
 */
public class Doc {

    String path;
    String text;
    String type;
    Map<String, Entity> entities;
    List<Relation> relations;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Doc(String path) {
        try {
            this.text = FileUtils.readFileToString(new File(path));
        } catch (IOException ex) {
            Logger.getLogger(Doc.class.getName()).log(Level.SEVERE, null, ex);
        }
        entities = new HashMap<>();
        relations = new ArrayList<>();
    }

    public void addRelation(Relation rel) {
        if (relations == null) {
            relations = new ArrayList<>();
        }
        relations.add(rel);
    }

    public void addEntity(Entity ent) {
        if (entities == null) {
            entities = new HashMap<>();
        }
        entities.put(ent.getId(), ent);
    }
}
