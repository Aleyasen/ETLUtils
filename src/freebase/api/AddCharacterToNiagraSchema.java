/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Aale
 */
public class AddCharacterToNiagraSchema {

    public static Map<String, String> toMap(String filepath) {
        Map<String, String> x_y_map = new HashMap();
        final List<String> x_y_lines = Utils.readFileLineByLine(filepath, true);
        for (String ln : x_y_lines) {
            String[] split = ln.split("\\t");
            String x = split[0];
            String y = split[1];
            x_y_map.put(x, y);
        }
        return x_y_map;
    }

    public static Map<String, String> toMapInv(String filepath) {
        Map<String, String> x_y_map = new HashMap();
        final List<String> x_y_lines = Utils.readFileLineByLine(filepath, true);
        for (String ln : x_y_lines) {
            String[] split = ln.split("\\t");
            String x = split[0];
            String y = split[1];
            x_y_map.put(y, x);
        }
        return x_y_map;
    }

    public static void main(String[] args) {
        Map<String, String> starring_actor_map = toMap("C:\\Data\\SIGMOD_DATA\\data\\top2000_data\\starring_actor.txt");
        Map<String, String> starring_character_map = toMap("C:\\Data\\SIGMOD_DATA\\data\\top2000_data\\starring_character.txt");
        Map<String, String> starring_movie_map = toMap("C:\\Data\\SIGMOD_DATA\\data\\top2000_data\\starring_movie.txt");
        Map<String, String> cast_movie_map = toMapInv("C:\\Data\\SIGMOD_DATA\\data\\top2000_data\\movie_cast.txt");
        String actor_cast_path = "C:\\Data\\SIGMOD_DATA\\data\\top2000_data\\actor_cast.txt";
        String character_cast_path = "C:\\Data\\SIGMOD_DATA\\data\\top2000_data\\character_cast.txt";
        Map<String, String> actormovie_to_character_map = new HashMap<>();
        for (String starring : starring_actor_map.keySet()) {
            String actor = starring_actor_map.get(starring);
            String character = starring_character_map.get(starring);
            String movie = starring_movie_map.get(starring);
            if (actor == null) {
                System.out.println("actor is null for starring = " + starring);
            } else if (character == null) {
                System.out.println("character is null for starring = " + starring);
            } else if (movie == null) {
                System.out.println("movie is null for starring = " + starring);
            } else {
                String actor_movie = actor + "-" + movie;
                if (actormovie_to_character_map.containsKey(actor_movie)) {
                    System.out.println("actormovie contains key = " + actor_movie);
                } else {
                    actormovie_to_character_map.put(actor_movie, character);
                }
            }
        }

        StringBuffer character_cast_str = new StringBuffer("character\tcast\n");
        final List<String> actor_cast_lines = Utils.readFileLineByLine(actor_cast_path, true);
        for (String ln : actor_cast_lines) {
            String[] split = ln.split("\\t");
            String actor = split[0];
            String cast = split[1];
            String movie = cast_movie_map.get(cast);
            String actor_movie = actor + "-" + movie;
            String character = actormovie_to_character_map.get(actor_movie);
            if (character == null) {
                System.out.println("character is null for actor-movie = " + actor_movie);
            } else {
                character_cast_str.append(character).append("\t").append(cast).append("\n");
            }
        }

        Utils.writeDataIntoFile(character_cast_str.toString(), character_cast_path);
    }
}
