package dblp.xml;

import java.io.*;
import java.util.*;

public class DblpDB3 {

    public static void main(String[] args) {
        String loc = "C:\\Data\\SIGMOD_DATA\\data\\new_dblp_experiment\\output_y2005_conf100_new_schema1_top2000\\";

        // nodes
        HashMap<Integer, String> authors = readNodes(loc + "author.txt");
        HashMap<Integer, String> years = readNodes(loc + "year.txt");
        HashMap<Integer, String> confs = readNodes(loc + "conf.txt");
        HashMap<Integer, String> titles = readNodes(loc + "title.txt");

        // edges
        HashMap<Integer, HashSet<Integer>> title_author = readEdges(loc + "title_author.txt");
        HashMap<Integer, HashSet<Integer>> title_conf = readEdges(loc + "title_conf.txt");
        HashMap<Integer, HashSet<Integer>> title_year = readEdges(loc + "title_year.txt");

		// re-arrange
        // : adding proceedings connecting to title,year,conf and remove title-year
        HashMap<Integer, String> procs = new HashMap<Integer, String>();
        HashMap<String, Integer> procs2 = new HashMap<String, Integer>();
        HashMap<Integer, HashSet<Integer>> title_proc = new HashMap<Integer, HashSet<Integer>>();
        HashMap<Integer, Integer> proc_year = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> proc_conf = new HashMap<Integer, Integer>();
        for (Integer t : titles.keySet()) {
			//if(title_conf.get(t).size()>1) System.out.println(titles.get(t)+" has more than one conferences : "+t+" - "+title_conf.get(t));
            //if(title_conf.get(t).size()>1) System.out.println(titles.get(t)+" has more than one years");
            HashSet<Integer> tp = new HashSet<Integer>();
            for (Integer c : title_conf.get(t)) {
                for (Integer y : title_year.get(t)) {
                    String proc = confs.get(c) + "-" + years.get(y);
                    int p = 0;
                    if (procs2.get(proc) == null) {
                        p = procs.size();
                        procs.put(p, proc);
                        procs2.put(proc, p);
                        proc_year.put(p, y);
                        proc_conf.put(p, c);
                    } else {
                        p = procs2.get(proc);
                    }
                    //title_proc.put(t,p);
                    tp.add(p);
                }
            }
            if (!tp.isEmpty()) {
                title_proc.put(t, tp);
            }
        }
		//System.out.println(title_conf);

		//System.out.println(confs);
        //System.out.println(new_confs);
        //System.out.println(new_conf_author);
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(loc + "proc.txt"));
            for (Integer p : procs.keySet()) {
                writer1.write(p + "\t" + procs.get(p) + "\n");
            }
            writer1.close();
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(loc + "title_proc.txt"));
            writer2.write("title\tproc\n");
            for (Integer t : title_proc.keySet()) {
                for (Integer p : title_proc.get(t)) {
                    writer2.write(t + "\t" + p + "\n");
                }
            }
            writer2.close();
            BufferedWriter writer3 = new BufferedWriter(new FileWriter(loc + "proc_year.txt"));
            writer3.write("proc\tyear\n");
            for (Integer p : proc_year.keySet()) {
                writer3.write(p + "\t" + proc_year.get(p) + "\n");
            }
            writer3.close();
            BufferedWriter writer4 = new BufferedWriter(new FileWriter(loc + "proc_conf.txt"));
            writer4.write("proc\tconf\n");
            for (Integer p : proc_conf.keySet()) {
                writer4.write(p + "\t" + proc_conf.get(p) + "\n");
            }
            writer4.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashSet<Integer> getDomains(HashMap<Integer, HashSet<Integer>> map, Integer id) {
        HashSet<Integer> set = new HashSet<Integer>();
        for (Integer obj : map.keySet()) {
            if (map.get(obj).contains(id)) {
                set.add(obj);
            }
        }
        return set;
    }

    public static HashMap<Integer, String> readNodes(String filename) {
        HashMap<Integer, String> map = new HashMap<Integer, String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                //System.out.println(line);
                StringTokenizer tok = new StringTokenizer(line);
                String s = tok.nextToken();
                Integer id = Integer.parseInt(s);
                String name = line.replaceFirst(s, "").trim();
                //System.out.println("> "+name);
                map.put(id, name);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static HashMap<Integer, HashSet<Integer>> readEdges(String filename) {
        HashMap<Integer, HashSet<Integer>> map = new HashMap<Integer, HashSet<Integer>>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.matches("[0-9\\s]+")) {
                    System.out.println(line);
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                StringTokenizer tok = new StringTokenizer(line);
                Integer id1 = Integer.parseInt(tok.nextToken());
                Integer id2 = Integer.parseInt(tok.nextToken());
                //map.put(id1,id2);
                if (map.containsKey(id1)) {
                    HashSet<Integer> s = map.get(id1);
                    s.add(id2);
                    map.put(id1, s);
                } else {
                    HashSet<Integer> s = new HashSet<Integer>();
                    s.add(id2);
                    map.put(id1, s);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

}
