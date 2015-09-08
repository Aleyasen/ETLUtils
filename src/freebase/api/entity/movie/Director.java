/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api.entity.movie;

/**
 *
 * @author Aale
 */
public class Director extends Entity {

    public Director() {
    }

    public Director(String mid, String name) {
        super(mid, name);
    }

    public Director(int id, String mid, String name) {
        super(id, mid, name);
    }
    
    

}
