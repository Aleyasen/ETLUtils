/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genmetapath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author Amirhossein Aleyasen <aleyase2@illinois.edu>
 * created on Sep 29, 2015, 11:20:03 AM
 */
public class GenerateMetapaths {

//    static String[] edge_types = new String[]{"movie-actor", "movie-character", "actor-character", "movie-director"};
//    static String[] edge_types = new String[]{"movie-starring", "character-starring", "actor-starring", "movie-directedby", "director-directedby"};
//    static String[] entities = new String[]{"movie", "character", "actor", "director"};

    static String[] edge_types = new String[]{"paper-domain", "paper-conf", "domain-keyword"};
    static String[] entities = new String[]{"paper", "conf", "domain", "keyword"};

    
    static Map<String, ArrayList<String>> neigh = new HashMap<String, ArrayList<String>>();

    public static void main(String[] args) {
        int metapath_len = 3;
        int entity_numbers = 7;
//        generateMetawalksForSpecificLength(metapath_len);
        generateMetawalksforSpecificNumberOfEntities(entity_numbers);
    }

    public static void generateMetawalksForSpecificLength(int metapath_len) {
        for (String et : edge_types) {
            String[] split = et.split("-");
            String e1 = split[0];
            String e2 = split[1];
            addToGraph(e1, e2);
            addToGraph(e2, e1);
        }
        System.out.println(neigh);

        for (String key : neigh.keySet()) {
//            System.out.println("BFS : " + key);
            bfs(key, metapath_len);
        }
    }

    public static void generateMetawalksforSpecificNumberOfEntities(int number_of_entities) {
        for (String et : edge_types) {
            String[] split = et.split("-");
            String e1 = split[0];
            String e2 = split[1];
            addToGraph(e1, e2);
            addToGraph(e2, e1);
        }
        System.out.println(neigh);
        int max_metapath_len = number_of_entities * 2 + 5;
        List<String> wholelist = new ArrayList<>();
        for (int i = 2; i <= max_metapath_len; i++) {
            for (String key : neigh.keySet()) {
//            System.out.println("BFS : " + key);
                final List<String> list = bfs(key, i);
                wholelist.addAll(list);
//                System.out.println("root=" + key);
//                System.out.println(list);
            }
        }
        List<String> result = new ArrayList<>();

        List<String> entities_list = Arrays.asList(entities);
        for (String hp : wholelist) {
//            System.out.println("hp= " + hp);
            final List<String> half = getMetawalkElements(hp);
//            System.out.println("half= " + half);
            final List<String> full = getFullMetawalk(half);
//            System.out.println("full= " + full);
            if (isAccepted(full, number_of_entities, entities_list)) {
//                System.out.println("Added");
                result.add(hp);
            }
        }

        System.out.println("Metawalks with entity_count = " + number_of_entities);
        for (String str : result) {
            System.out.println(str);
        }
    }

    public static void addToGraph(String e1, String e2) {
        List<String> val = neigh.get(e1);
        if (val == null) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(e2);
            neigh.put(e1, list);
        } else {
            val.add(e2);
        }
    }

    public static List<String> bfs(String node, int max_len) {
        List<String> list = new ArrayList<>();
        //Since queue is a interface
        Queue<String> queue = new LinkedList<String>();
        Queue<Integer> depth = new LinkedList<Integer>();
        Queue<String> path = new LinkedList<String>();

        if (node == null) {
            return null;
        }
        //Adds to end of queue
        queue.add(node);
        depth.add(1);
        path.add(node);

        while (!queue.isEmpty()) {
            //removes from front of queue
            String r = queue.remove();
            int current_depth = depth.remove();
            String current_path = path.remove();
//            System.out.print(r + "\t");
            if (current_depth == max_len) {
//                System.out.println(current_path);
                list.add(current_path);
            }
            if (current_depth == max_len + 1) {
                break;
            }
            //Visit child first before grandchild
            for (String n : neigh.get(r)) {
                queue.add(n);
                depth.add(current_depth + 1);
                path.add(current_path + " " + n);
            }
        }
        return list;
    }

    private static List<String> getMetawalkElements(String str) {
        List<String> list = new ArrayList<>();
        String[] split = str.split("\\s+");
        for (int i = 0; i < split.length; i++) {
            list.add(split[i]);
        }
        return list;
    }

    private static List<String> getFullMetawalk(List<String> half) {
        List<String> list = new ArrayList<>();
        list.addAll(half);
        for (int i = half.size() - 2; i >= 0; i--) {
            list.add(half.get(i));
        }
        return list;
    }

    private static boolean isAccepted(List<String> full, int number_of_entities, List<String> entities_list) {
//        System.out.println("full: " + full);
        
        if (!full.get(0).equals(full.get(full.size() - 1))) {
            return false;
        }
        if (!entities_list.contains(full.get(0))) {
            return false;
        }
        if (!entities_list.contains(full.get(full.size() - 1))) {
            return false;
        }
        int entity_count = 0;
        for (String elem : full) {
            if (entities_list.contains(elem)) {
                entity_count++;
            }
        }
        return entity_count == number_of_entities;
    }

}
