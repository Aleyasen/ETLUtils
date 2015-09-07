/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.util.List;

/**
 *
 * @author Aale
 */
public class Aggregator {

    public static void main(String[] args) {
        String filepath = args[0];
//        String filepath = "C:\\Data\\SIGMOD_DATA\\data\\uw-courses-results-new-schema\\compare_uw_courses_remove_duplicate_rwr.txt";
        final List<String> lines = IOUtils.readFileLineByLine(filepath, false);

        double[] ave20 = getAverage(lines, 2);
        double[] ave10 = getAverage(lines, (int) ave20[2] + 2);
        double[] ave5 = getAverage(lines, (int) ave10[2] + 2);
        double[] ave3 = getAverage(lines, (int) ave5[2] + 2);
        String output = "\n";
        output += "Average20: " + ave20[0] + "\t" + "Var: "+ ave20[3] + "\t" + "Count: " + (int) ave20[1] + "\n";
        output += "Average10: " + ave10[0] + "\t" + "Var: "+ ave10[3] + "\t" + "Count: " + (int) ave10[1] + "\n";
        output += "Average5: " + ave5[0] + "\t" + "Var: "+ ave5[3] + "\t" + "Count: " + (int) ave5[1] + "\n";
        output += "Average3: " + ave3[0] + "\t" + "Var: "+ ave3[3] + "\t" + "Count: " + (int) ave3[1] + "\n";
//        System.out.println(output);
        IOUtils.writeDataIntoFile(output, filepath, true);
    }

    private static double[] getAverage(List<String> strs, int begin) {
        double sum = 0;
        double sum_2 = 0;
        int count = 0;
        int index = begin;
        while (true) {
            if (index >= strs.size() || strs.get(index).startsWith("-")) {
                double result[] = new double[4];
                final double average = sum * 1.0 / count;
                double var = (sum_2 * 1.0 / count) - (average * average);
                result[0] = average;
                result[1] = count;
                result[2] = index;
                result[3] = var;
                return result;
            }
            final String str = strs.get(index);
            System.out.println(str);
            if (str.equals("NaN")) {
                index++;
                continue;
            }
            double num = Double.parseDouble(str);
            sum += num;
            sum_2 += num*num;
            count++;
            index++;
        }
    }

}
