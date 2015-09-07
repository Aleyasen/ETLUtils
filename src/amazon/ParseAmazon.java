/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazon;

import dblp.xml.IOUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aale
 */
public class ParseAmazon {

    public static Map<String, String> idToASIN = new HashMap<String, String>();
    public static Map<String, String> ASINToId = new HashMap<String, String>();
    public static Map<String, String> idToTitle = new HashMap<String, String>();
    public static Map<String, String> idToGroup = new HashMap<String, String>();
    public static List<String> ids_edges = new ArrayList<String>();

    public static void main(String[] args) {
        String file = "F:\\Data\\SIGMOD_DATA\\Amazon\\amazon_all.txt";

        String idToASIN_file = "F:\\Data\\SIGMOD_DATA\\Amazon\\amazon_id_asin.txt";
        String idToGroup_file = "F:\\Data\\SIGMOD_DATA\\Amazon\\amazon_id_group.txt";
        String idToTitle_file = "F:\\Data\\SIGMOD_DATA\\Amazon\\amazon_id_title.txt";
        String idsEdge_file = "F:\\Data\\SIGMOD_DATA\\Amazon\\amazon_id_id_edges.txt";
        parse(file, true);
        writeMapToFile(idToASIN, idToASIN_file);
        writeMapToFile(idToGroup, idToGroup_file);
        writeMapToFile(idToTitle, idToTitle_file);
        parse(file, false);
        writeListToFile(ids_edges, idsEdge_file);

    }

    private static void parse(String file, boolean storeMap) {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(file);
            sc = new Scanner(inputStream, "UTF-8");
            while (true) {
                if (!sc.hasNextLine()) {
                    break;
                }
                String line = sc.nextLine();
                if (!line.startsWith("Id:")) {
                    System.out.println("Id mismatch");
                    continue;
                }
                String id = line.split("\\s+")[1];
                line = sc.nextLine();
                if (!line.startsWith("ASIN:")) {
                    System.out.println("ASIN mismatch");
                    continue;
                }
                String asin = line.split("\\s+")[1];
                line = sc.nextLine();
                if (!line.startsWith("  title:")) {
                    System.out.println("title mismatch");
                    continue;
                }
                String title = line.split("\\s+", 3)[2];
                line = sc.nextLine();
                if (!line.startsWith("  group:")) {
                    System.out.println("group mismatch");
                    continue;
                }
                String group = line.split("\\s+", 3)[2];
                line = sc.nextLine();
                if (!line.startsWith("  similar:")) {
                    System.out.println("similar mismatch");
                    continue;
                }
                String similar = line.split("\\s+", 3)[2];
                //process data
                if (storeMap) {
                    idToASIN.put(id, asin);
                    ASINToId.put(asin, id);
                    idToGroup.put(id, group);
                    idToTitle.put(id, title);
                } else {
                    String[] split = similar.split("\\s+");
                    int count = Integer.parseInt(split[0]);
                    for (int i = 0; i < count; i++) {
//                        System.out.println(split[i + 1]);
                        String sec_id = ASINToId.get(split[i + 1]);
                        if (sec_id == null) {
                            System.out.println("sec_id is not available. ignore <>" + split[i + 1] + "<>");
                            continue;
                        }
                        ids_edges.add(id + "\t" + sec_id);
                    }
                }
//                System.out.println("id=" + id + "<>" + asin + "<>" + title + "<>" + group + "<>" + similar);

            }
            sc.close();
            inputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseAmazon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParseAmazon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void writeMapToFile(Map<String, String> map, String file) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append(key).append("\t").append(map.get(key)).append("\n");
        }
        IOUtils.writeDataIntoFile(sb.toString(), file, false);
    }

    private static void writeListToFile(List<String> list, String file) {
        StringBuilder sb = new StringBuilder();
        for (String line : list) {
            sb.append(line).append("\n");
        }
        IOUtils.writeDataIntoFile(sb.toString(), file, false);
    }
}
