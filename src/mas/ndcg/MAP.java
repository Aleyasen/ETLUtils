/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.ndcg;

import java.util.List;

/**
 *
 * @author Aale
 */
public class MAP {

    public static double getPrecAtK(List<Integer> scores, int k) {
        double apSum = 0;
        double cnt = 0;
        for (int i = 0; i < k; i++) {
            if (scores.get(i) > 0) {
                cnt++;
            }
        }
        if (k == 0) {
            return 0;
        }
        return cnt * 1.0 / k;
    }

    public static double getAvePrec(List<Integer> scores) {
        double sum = 0;
        int rel = 0;
        int ones = 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) > 0) {
                rel = 1;
                ones++;
            } else {
                rel = 0;
            }
            sum += rel * getPrecAtK(scores, i);
        }
        return sum * 1.0 / ones;
    }
    
}
