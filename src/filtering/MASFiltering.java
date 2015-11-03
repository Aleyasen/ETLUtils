/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filtering;

/**
 *
 * @author Amirhossein Aleyasen <aleyase2@illinois.edu>
 * created on Oct 18, 2015, 11:22:30 PM
 */
public class MASFiltering {

    static String root = "H:\\workspace\\RepIndepMining\\MAS\\dataset\\";
    static String root_out = "H:\\workspace\\RepIndepMining\\MAS\\dataset\\filtered_6\\";

    public static void main(String[] args) {
        DL papers = new DL(in("paper.txt"));
        DL keywords = new DL(in("keyword.txt"));
        DL confs = new DL(in("conf.txt"));
        DL domain = new DL(in("domain.txt"));

        DL slc_domain = new DL(in("selected_domain.txt"));

        CN slc_conf_domain = CN.filter(null, slc_domain, in("conf_domain.txt"));
        DL slc_conf = confs.filter(slc_conf_domain.first());
        CN slc_paper_domain = CN.filter(null, slc_domain, in("paper_domain.txt"));
        DL slc_paper = papers.filter(slc_paper_domain.first());
        CN slc_domain_keyword = CN.filter(slc_domain, null, in("domain_keyword.txt"));
        DL slc_keyword = keywords.filter(slc_domain_keyword.second());
        CN slc_paper_conf = CN.filter(slc_paper, slc_conf, in("paper_conf.txt"));

        slc_domain.write(out("domain.txt"));
        slc_conf.write(out("conf.txt"));
        slc_paper.write(out("paper.txt"));
        slc_keyword.write(out("keyword.txt"));

        slc_conf_domain.write(out("conf_domain.txt"));
        slc_paper_domain.write(out("paper_domain.txt"));
        slc_domain_keyword.write(out("domain_keyword.txt"));
        slc_paper_conf.write(out("paper_conf.txt"));

        System.out.println("Nodes#: " + (slc_domain.size() + slc_conf.size() + slc_paper.size() + slc_keyword.size()));
        System.out.println("Edges#: " + (slc_conf_domain.size() + slc_paper_domain.size() + slc_domain_keyword.size() + slc_paper_conf.size()));
    }

    public static String in(String file) {
        return root + file;
    }

    public static String out(String file) {
        return root_out + file;
    }
}
