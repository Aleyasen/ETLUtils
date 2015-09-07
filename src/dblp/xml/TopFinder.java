/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import uw.Utils;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Aale
 */
public class TopFinder {

    public static void main(String[] args) {
        String filepath = "C:\\Data\\SIGMOD_DATA\\data\\output_y2005_conf50_schema1_top2000\\author.txt";
        List<Integer> nums = Arrays.asList(181, 311, 3095, 1655, 775, 67, 2287, 1777, 9748, 4634,
                488, 29, 99, 198, 2656, 4633, 9813, 460, 678, 2667, 612, 51, 2739, 5936, 5968, 83, 7266,
                3082, 1909, 1963, 18, 2910, 5935, 1649, 1449, 171, 4521, 43, 422, 65, 1123, 349, 66, 1213,
                3186, 13818, 591, 384, 1385, 8731);
        final List<String> lines = Utils.readFileLineByLine(filepath, false);
        for (String l : lines) {
            String[] split = l.split("\\s", 2);
//            System.out.println(split[0] + "<><><>" + split[1]);
            if (nums.contains(Integer.parseInt(split[0]))) {
                System.out.println(split[1]);
            }
        }
    }

}
