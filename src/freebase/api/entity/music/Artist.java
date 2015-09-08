/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api.entity.music;

import freebase.api.entity.movie.Entity;
import java.util.List;

/**
 *
 * @author Aale
 */
public class Artist extends Entity {

    List<Recording> recordings;
    
    
    public Artist() {
    }

    public Artist(String mid, String name) {
        super(mid, name);
    }

    public Artist(int id, String mid, String name) {
        super(id, mid, name);
    }

    public void setRecordings(List<Recording> recordings) {
        this.recordings = recordings;
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

}
