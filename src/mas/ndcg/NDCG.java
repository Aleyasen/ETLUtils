/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.ndcg;

/**
 *
 * @author Aale
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NDCG {

//    static List<String> algs = Arrays.asList("modpathsim1", "pathsim", "rwr", "ave_pathsim", "ave_modpathsim1");
//    static List<String> algs = Arrays.asList("modpathsim1-w-citation");
    //   static List<String> algs = Arrays.asList("pathsim", "modpathsim1", "pathsim-target", "modpathsim1_paper_star");
    static List<String> algs = Arrays.asList("modpathsim2");

    public static void main(String[] args) {
        for (String alg : algs) {
//            String alg = "ave_modpathsim1";
//        String root = "F:\\Data\\SIGMOD_DATA\\data\\data dblp for effeciency\\random queries";
//            String root = "F:\\Data\\SIGMOD_DATA\\data\\mas_results\\200_conf";
            String root = "F:\\Data\\SIGMOD_DATA\\data\\MAS effectiveness for different paths";
            String dir = root + "\\mas-all-paths\\" + alg + "_ndcg";
            // String dir = root + "\\dblp-target-c100-new-schema-area-metapath\\" + alg + "_ndcg";
            System.out.println();
            System.out.println(">>>>>>>>>>>>>>>>>>>>  " + alg);
            System.out.println();
            for (int i = 1; i <= 200; i++) {
                String file = dir + "\\" + "cat_group.query" + i + ".txt";
                if (!(new File(file).exists())) {
                    continue;
                }
                List<String> ans = Extractor.readAnsFile(file);
                List<Integer> ans_int = new ArrayList<>();
                for (String a : ans) {
                    ans_int.add(Integer.parseInt(a));
                }
                System.out.println(NDCG.getNDCG(ans_int, 10));
//            System.out.println(MAP.getAvePrec(ans_int));
//                System.out.println(MAP.getPrecAtK(ans_int, 10));
            }
        }
    }

    public static void example(String[] args) {
        List<Integer> urls = Arrays.asList(new Integer[]{3, 2, 3, 0, 1, 2});
        System.out.println(NDCG.getNDCG(urls, 6));
    }

    public static double getNDCG(List<Integer> list, int r) {
        // get DCG of urls
        double urlDCG = getDCG(list, r);
//        System.out.println("DCG="+urlDCG);
        // get DCG of perfect ranking
        List<Integer> perfectList = new ArrayList<>();
        perfectList.addAll(list);
        Collections.sort(perfectList);
        Collections.reverse(perfectList);
        double perfectDCG = getDCG(perfectList, r);
//        System.out.println("perfectDCG="+perfectDCG);
        // normalize by dividing
        double normalized = urlDCG / perfectDCG;
        return normalized;
    }

    private static double getDCG(List<Integer> scores, int p) {
        double score = 0;

        if (scores.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < p; i++) {
            double relevance = scores.get(i);
            int ranking = i + 1;

            if (ranking > 1) {
                // for all positions after the first one, reduce the "gain" as ranking increases
                relevance /= logBase2(ranking);
            }

            score += relevance;
        }

        return score;
    }

    private static double logBase2(double value) {
        return Math.log(value) / Math.log(2);
    }
}
