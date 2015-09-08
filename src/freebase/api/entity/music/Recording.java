/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api.entity.music;

import freebase.api.entity.movie.Entity;

/**
 *
 * @author Aale
 */
public class Recording extends Entity {

    Artist artist;

    public Recording() {
    }

    public Recording(String mid, String name) {
        super(mid, name);
    }

    public Recording(int id, String mid, String name) {
        super(id, mid, name);
    }
    
    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Artist getArtist() {
        return artist;
    }

}
