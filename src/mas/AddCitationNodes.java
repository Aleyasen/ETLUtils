/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.List;

/**
 *
 * @author Aale
 */
public class AddCitationNodes {

    static String ROOT = "F:\\Data\\SIGMOD_DATA\\data\\mas_results\\50_conf\\data\\";

    public static void main(String[] args) {
        String p_p = ROOT + "paper_paper.txt";
        int cite_index = 1;
        StringBuffer cite_str = new StringBuffer();
        StringBuffer paper_cite_str = new StringBuffer();
        final List<String> lines = IOUtils.readFileLineByLine(p_p, false);
        for (String l : lines) {
            String[] split = l.split("\\t");
            String p1 = split[0];
            String p2 = split[1];
            cite_str.append(cite_index).append("\tcite").append(cite_index).append("\n");
            paper_cite_str.append(p1).append("\t").append(cite_index).append("\n");
            paper_cite_str.append(p2 + "\t" + cite_index).append("\n");
            cite_index++;
        }
        IOUtils.writeDataIntoFile(cite_str.toString(), ROOT + "citation.txt");
        IOUtils.writeDataIntoFile(paper_cite_str.toString(), ROOT + "paper_citation.txt");
    }
}
