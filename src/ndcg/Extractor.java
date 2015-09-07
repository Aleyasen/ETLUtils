/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ndcg;

import dblp.xml.IOUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Aale
 */
public class Extractor {

    static Map<String, String> proc2Area = new HashMap<>();
    static Map<Integer, String> procStr = new HashMap<>();
    static Map<Integer, String> areaStr = new HashMap<>();

    public static Map<Integer, String> readFile(String filename) {
        Map<Integer, String> map = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();

            while (line != null) {
                String[] split = line.split("\\t");
                Integer id = Integer.parseInt(split[0]);
                String name = split[1];
                map.put(id, name);
                line = br.readLine();
            }
            return map;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> readQueryFile(String filename) {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();

            while (line != null) {
                list.add(line);
                line = br.readLine();
            }
            return list;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void initMap() {
        procStr = readFile(root + "\\proc.txt");
        areaStr = readFile(root + "\\area.txt");
        String proc_area_file = root + "\\proc_area.txt";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(proc_area_file));
            String line = br.readLine();

            while (line != null) {
                String[] split = line.split("\\s");
                Integer proc = Integer.parseInt(split[0]);
                Integer area = Integer.parseInt(split[1]);
                proc2Area.put(procStr.get(proc), areaStr.get(area));
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createDir(String dir) {
        File theDir = new File(dir);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + dir);
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
    }

    static String root = "F:\\Data\\SIGMOD_DATA\\data\\mas_results\\50_conf\\result top keywords\\";
        
    public static void main(String[] args) {
        initMap();
        String alg = "modpathsim2";
        String dir = root + "\\top queries\\dblp-target-c100-new-schema-area-metapath\\" + alg;

        String out_dir = root + "\\top queries\\dblp-target-c100-new-schema-area-metapath\\" + alg + "_ndcg";
        createDir(out_dir);
        String query_file = root + "\\_query_proc_100_top.txt";
        calc(dir, out_dir, query_file);
    }

    private static void calc(String dir, String out_dir, String query_file) {
        List<String> qs = readQueryFile(query_file);
        int index = 1;
        for (String q : qs) {
            String queryArea = proc2Area.get(q);
            if (!areaMap.keySet().contains(queryArea)) {
                System.out.println("ignore query: " + index + " " + q + " area: " + queryArea);
                index++;
                continue;
            }
            String file = dir + "\\" + "answer.query" + index /*+ ".txt" */;
            List<String> answers = readAnsFile(file);
            StringBuffer outputStr = new StringBuffer();

            for (String ans : answers) {
                String area = proc2Area.get(ans);
                int score = getScore(queryArea, area);
                outputStr.append(score).append("\n");
            }
            IOUtils.writeDataIntoFile(outputStr.toString(), out_dir + "\\" + "area_group.query" + index + ".txt", false);
            index++;
        }
    }

    public static List<String> readAnsFile(String file) {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                String[] split = line.split("\\t");
                list.add(split[0]);
                line = br.readLine();
            }
            return list;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;

    }

    final static Map<String, List<String>> areaMap = new HashMap<>();

    static {
//        areaMap.put("AI", Arrays.asList(""));
//        areaMap.put("WWW", Arrays.asList("DB", "IR", "DM"));
        areaMap.put("DB", Arrays.asList("WWW", "IR", "DM"));
//        areaMap.put("NA", Arrays.asList(""));
//        areaMap.put("ENG", Arrays.asList(""));
//        areaMap.put("NETWORK", Arrays.asList(""));
        areaMap.put("IR", Arrays.asList("DB", "DM", "WWW"));
        areaMap.put("DM", Arrays.asList("DB", "WWW", "IR"));
//        areaMap.put("UN", Arrays.asList(""));
//        areaMap.put("CS", Arrays.asList(""));
//        areaMap.put("MM", Arrays.asList(""));
//        areaMap.put("DIST", Arrays.asList(""));
//        areaMap.put("SEC", Arrays.asList(""));

    }

    private static int getScore(String queryArea, String area) {
        if (area.equals(queryArea)) {
            return 2;
        } else if (areaMap.get(queryArea).contains(area)) {
            return 1;
        } else {
            return 0;
        }
    }

}
