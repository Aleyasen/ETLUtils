/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.ndcg;

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
import mas.IOUtils;

/**
 *
 * @author Aale
 */
public class Extractor {

    static Map<String, String> proc2Area;
    static Map<Integer, String> procStr;
    static Map<Integer, String> areaStr;

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
        procStr = readFile(root + "\\conf.txt");
        areaStr = readFile(root + "\\cat.txt");
        proc2Area = new HashMap<>();
        String proc_area_file = root + "\\conf_cat.txt";
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

//    static String root = "F:\\Data\\SIGMOD_DATA\\data\\mas_results\\200_conf";
    static String root = "F:\\Data\\SIGMOD_DATA\\data\\MAS effectiveness for different paths";

//    static List<String> algs = Arrays.asList("modpathsim1", "pathsim", "rwr", "ave_pathsim", "ave_modpathsim1");
//    static List<String> algs = Arrays.asList("pathsim","modpathsim1","pathsim-target","modpathsim1_paper_star");
static List<String> algs = Arrays.asList("modpathsim2");
    public static void main(String[] args) {
        for (String alg : algs) {
            initMap();
            // String alg = "modpathsim1";
            String dir = root + "\\mas-all-paths\\" + alg;

            String out_dir = root + "\\mas-all-paths\\" + alg + "_ndcg";
            createDir(out_dir);
            String query_file = root + "\\_query.txt";
            calc(dir, out_dir, query_file);
        }
    }

    private static void calc(String dir, String out_dir, String query_file) {
        List<String> qs = readQueryFile(query_file);
        int index = 1;
        for (String q : qs) {
            String queryArea = proc2Area.get(q);
//            if (!areaMap.keySet().contains(queryArea)) {
//                System.out.println("ignore query: " + index + " " + q + " area: " + queryArea);
//                index++;
//                continue;
//            }
            String file = dir + "\\" + "answer.query" + index /*+ ".txt" */;
            List<String> answers = readAnsFile(file);
            StringBuffer outputStr = new StringBuffer();

            for (String ans : answers) {
                String area = proc2Area.get(ans);
                int score = getScore(queryArea, area);
                outputStr.append(score).append("\n");
            }
            IOUtils.writeDataIntoFile(outputStr.toString(), out_dir + "\\" + "cat_group.query" + index + ".txt", false);
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
        final String DM = "Data Mining";
        final String DB = "Databases";
        final String IR = "Information Retrieval";
        final String WWW = "World Wide Web";
        final String ML = "Machine Learning & Pattern Recognition";
        final String ALGO = "Algorithms & Theory";
        final String AI = "Artificial Intelligence";
        final String NLP = "Natural Language & Speech";
        final String VISION = "Computer Vision";
        final String HCL = "Human-Computer Interaction";

// 7	Data Mining	==> DB, IR, WWW, ML
//18	Databases	==> DM, IR, WWW
//8	Information Retrieval	==> DM,	DB, WWW
//15	World Wide Web	==> DM, DB, IR
//1	Algorithms & Theory	
//5	Artificial Intelligence	==> ML, NLP, Vision
//6	Machine Learning & Pattern Recognition	==> AI, NLP, Vision
//9	Natural Language & Speech	==> ML, AI
//11	Computer Vision	==>	ML,	AI, HCI
//12	Human-Computer Interaction	==>	Vision
        areaMap.put(DM, Arrays.asList(DB, IR, WWW, ML));
        areaMap.put(DB, Arrays.asList(DM, IR, WWW));
        areaMap.put(IR, Arrays.asList(DM, DB, WWW));
        areaMap.put(WWW, Arrays.asList(DM, DB, IR));
        areaMap.put(ALGO, new ArrayList<String>());
        areaMap.put(AI, Arrays.asList(ML, NLP, VISION));
        areaMap.put(ML, Arrays.asList(AI, NLP, VISION));
        areaMap.put(NLP, Arrays.asList(ML, AI));
        areaMap.put(VISION, Arrays.asList(ML, AI, HCL));
        areaMap.put(HCL, Arrays.asList(VISION));

    }

    private static int getScore(String queryArea, String area) {
        if (area.equals(queryArea)) {
            return 2;
        } else if (/*areaMap.get(queryArea) != null &&*/areaMap.get(queryArea).contains(area)) {
            return 1;
        } else {
            return 0;
        }
    }

}
