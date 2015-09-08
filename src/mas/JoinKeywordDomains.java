/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Aale
 */
public class JoinKeywordDomains {

    private static void initKeywordIdsMap() {
        String file = "C:\\Users\\Aale\\NW\\Dropbox\\RepIndepMining\\PVLDB-other stuffs\\keyword.txt";
        final List<String> lines = IOUtils.readFileLineByLine(file, false);
        for (String l : lines) {
            String[] split = l.split("\\t", 2);
            if (split.length == 2) {
                Integer id = Integer.parseInt(split[0]);
                String name = split[1];
                keywordIds.put(name, id);
            } else {
                System.out.println(l);
                System.out.println("error in parsingggggggggggggggggg");
            }
        }
    }
    static Map<String, Integer> keywordIds = new HashMap<>();

    static String rootDir = "C:\\Users\\Aale\\NW\\Dropbox\\RepIndepMining\\PVLDB-other stuffs\\keywords\\";

    public static void main(String[] args) {
        initKeywordIdsMap();
        readTopKeywordFile(rootDir + "AI.txt", 5);
        readTopKeywordFile(rootDir + "Algo.txt", 1);
        readTopKeywordFile(rootDir + "DB.txt", 18);
        readTopKeywordFile(rootDir + "DM.txt", 7);
        readTopKeywordFile(rootDir + "HCI.txt", 12);
        readTopKeywordFile(rootDir + "IR.txt", 8);
        readTopKeywordFile(rootDir + "ML.txt", 6);
        readTopKeywordFile(rootDir + "NLP.txt", 9);
        readTopKeywordFile(rootDir + "Vision.txt", 11);
        readTopKeywordFile(rootDir + "WWW.txt", 15);
    }

    private static void readTopKeywordFile(String file, int domain) {
        final List<String> lines = IOUtils.readFileLineByLine(file, false);
        String result = "";
        for (String l : lines) {
            Integer id = keywordIds.get(l);
            if (id == null) {
                System.out.println(l + " not found!!!!!!!!!!!!!!!");

                continue;
            }
            result += domain + "\t" + id + "\n";
        }
        IOUtils.writeDataIntoFile(result, rootDir + "domain_keyword.txt", true);
    }
}
