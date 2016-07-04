/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filtering;

/**
 *
 * @author Amirhossein Aleyasen <aleyase2@illinois.edu>
 * created on Oct 18, 2015, 11:22:30 PM
 */
public class IMDBFiltering {

    static String root = "H:\\workspace\\RepIndepMining\\IMDB Datasets\\top10000\\";
    static String root_out = "H:\\workspace\\RepIndepMining\\IMDB Datasets\\top10000\\filtered_1\\";

    public static void main(String[] args) {
        DL movies = new DL(in("movie.txt"));
        DL actors = new DL(in("actor.txt"));
        DL directors = new DL(in("director.txt"));
        DL starrings = new DL(in("starring.txt"));
        DL characters = new DL(in("character.txt"));
        DL directedbys = new DL(in("directedby.txt"));

//        %% inputs
//node_files = { [DAT_LOC '/movie.txt'], [DAT_LOC '/actor.txt'], 
//        [DAT_LOC '/director.txt'], 
//        [DAT_LOC '/starring.txt'],[DAT_LOC '/character.txt'],[DAT_LOC '/directedby.txt'] };
//edge_files = { [DAT_LOC '/starring_actor.txt'], [DAT_LOC '/starring_movie.txt'],
//        [DAT_LOC '/starring_character.txt'],[DAT_LOC '/directedby_director.txt'],
//        [DAT_LOC '/directedby_movie.txt']};
        DL slc_movies = new DL(in("selected_movie.txt"));

        CN slc_starring_movie = CN.filter(null, slc_movies, in("starring_movie.txt"));
        DL slc_starring = starrings.filter(slc_starring_movie.first());
        CN slc_starring_character = CN.filter(slc_starring, null, in("starring_character.txt"));
        DL slc_character = characters.filter(slc_starring_character.second());
        CN slc_starring_actor = CN.filter(slc_starring, null, in("starring_actor.txt"));
        DL slc_actor = actors.filter(slc_starring_actor.second());

        CN slc_directedby_movie = CN.filter(null, slc_movies, in("directedby_movie.txt"));
        DL slc_directedby = directedbys.filter(slc_directedby_movie.first());
        CN slc_directedby_director = CN.filter(slc_directedby, null, in("directedby_director.txt"));
        DL slc_director = directors.filter(slc_directedby_director.second());

        slc_movies.write(out("movie.txt"));
        slc_starring.write(out("starring.txt"));
        slc_character.write(out("character.txt"));
        slc_actor.write(out("actor.txt"));
        slc_directedby.write(out("directedby.txt"));
        slc_director.write(out("director.txt"));

        slc_starring_movie.write(out("starring_movie.txt"));
        slc_starring_character.write(out("starring_character.txt"));
        slc_starring_actor.write(out("starring_actor.txt"));
        slc_directedby_movie.write(out("directedby_movie.txt"));
        slc_directedby_director.write(out("directedby_director.txt"));

        System.out.println("Nodes#: " + (slc_movies.size() + slc_starring.size() + slc_character.size() + slc_actor.size() + slc_directedby.size() + slc_director.size()));
        System.out.println("Edges#: " + (slc_starring_movie.size() + slc_starring_character.size() + slc_starring_actor.size() + slc_directedby_movie.size() + slc_directedby_director.size()));
    }

    public static String in(String file) {
        return root + file;
    }

    public static String out(String file) {
        return root_out + file;
    }
}
