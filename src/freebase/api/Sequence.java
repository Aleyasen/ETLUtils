/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api;

/**
 *
 * @author Aale
 */
public class Sequence {

    int step = 1;
    int val = 1;

    public Sequence() {
    }

    public Sequence(int start, int step) {
        this.val = start;
        this.step = step;
    }

    public int get() {
        return val;
    }

    public int next() {
        val++;
        return val;
    }

}
