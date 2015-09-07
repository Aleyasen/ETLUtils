/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aleyase2-admin
 */
public class DBLPCollection {

    Map<String, Integer> idMap;

    public DBLPCollection() {
        idMap = new HashMap<>();
    }

    public int get(String key) {
        if (!idMap.containsKey(key)) {
            int id = getNextId();
            idMap.put(key, id);
            return id;
        }
        return idMap.get(key);
    }

    public void writeToFile(String path) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (String key : idMap.keySet()) {
                writer.write(idMap.get(key) + " " + key + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    int lastId = 0;

    private Integer getNextId() {
        lastId++;
        return lastId;
    }

    void writeToFileJustFirsToken(String path) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (String key : idMap.keySet()) {
                String firstToken = key.split("\\s+")[0];
                writer.write(idMap.get(key) + " " + firstToken + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
