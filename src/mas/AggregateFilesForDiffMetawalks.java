/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import dblp.xml.IOUtils;
import dblp.xml.MapUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Amirhossein Aleyasen <aleyase2@illinois.edu>
 * created on Jul 15, 2015, 11:08:01 AM
 */
public class AggregateFilesForDiffMetawalks {

    public static void main(String[] args) {
        String root = "F:\\Data\\SIGMOD_DATA\\data\\MAS effectiveness for different paths\\";

        for (int q = 1; q <= 172; q++) {
            Map<String, Double> scores = new HashMap<>();
            Map<String, Integer> counts = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                if (i == 1) {
                    continue;
                }
                String path = root + "mas-effect-p" + i + "\\modpathsim2\\" + "answer.query" + q;
                final List<String> lines = IOUtils.readFileLineByLine(path, false);
                for (String l : lines) {
                    String[] split = l.split("\\t");
                    String key = split[0];
                    Double sc = Double.parseDouble(split[2]);
                    Double current_sc = scores.get(key);
                    Integer current_count = counts.get(key);
                    if (current_sc == null) {
                        scores.put(key, sc);
                        counts.put(key, 1);
                    } else {
                        scores.put(key, current_sc + sc);
                        counts.put(key, current_count + 1);
                    }
                }
            }
            String output_path = root + "mas-all-paths\\modpathsim2\\" + "answer.query" + q;
            String result = "";
            final Map<String, Double> sorted_scores = MapUtil.sortByValue(scores);
            for (String key : sorted_scores.keySet()) {
                Double ave_score = sorted_scores.get(key) / counts.get(key);
                result += key + "\t" + "|" + "\t" + ave_score + "\n";
            }
            IOUtils.writeDataIntoFile(result, output_path);
            System.out.println("File Done. " + output_path);
        }
    }
}
