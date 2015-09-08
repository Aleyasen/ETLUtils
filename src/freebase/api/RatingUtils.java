/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Aale
 */
public class RatingUtils {

    public static void main(String[] args) {
        String ratingFile = "C:\\Data\\SIGMOD_DATA\\data\\top2000_imdb_source\\rating.txt";
        String actorFile = "C:\\Data\\SIGMOD_DATA\\data\\top2000_imdb_source\\movie_only.txt";
        Map<String, Double> ratingMap = new HashMap<>();
        final List<String> lines = Utils.readFileLineByLine(ratingFile, false);
        for (String line : lines) {
            String split[] = line.split("\\t");
//            System.out.println(split[0]);
            if (split.length == 2) {
                String movie = split[0];
                Double rate = Double.parseDouble(split[1]);
                ratingMap.put(movie, rate);
            }
        }
        System.out.println("RatingMap#:" + ratingMap.size());
        final List<String> actorLines = Utils.readFileLineByLine(actorFile, false);
        Map<String, Double> myMovies = new HashMap<>();
        for (String act : actorLines) {
            final Double rate = ratingMap.get(act);
            if (rate == null) {
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            } else {
                myMovies.put(act, rate);
//                System.out.println(act + "\t" + rate);
            }
        }
        final Set<String> selectedMovie = stratifiedSample(myMovies, 100);
        for (String m : selectedMovie) {
//            System.out.println(m + "\t\t" + myMovies.get(m));
            System.out.println(m);
        }
    }

    private static Set<String> stratifiedSample(Map<String, Double> data, int num) {
        Double sum = 0.0;
        for (String key : data.keySet()) {
            sum += data.get(key);
        }
        System.out.println("SUM=" + sum);
        Set<String> output = new HashSet<>();
        while (true) {
            if (output.size() == num) {
                break;
            }
            final double r = randomInRange(0, sum);
//            System.out.println(r);
            double curSum = 0;
            for (String key : data.keySet()) {
                if (curSum >= r) {
                    output.add(key);
                    break;
                }
                curSum += data.get(key);
            }
        }
        return output;
    }

    protected static Random random = new Random();

    public static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }
}
