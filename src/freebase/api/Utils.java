/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aale
 */
public class Utils {
    public static String SEPERATOR = "\t";
    public static String NEWLINE = "\n";

    public static void writeDataIntoFile(String content, String filepath) {
        FileWriter fw = null;
        try {
            File file = new File(filepath);
//            System.out.println("writeDataIntoFile filepath=" + file.getAbsolutePath());
            fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }


    public static List<String> readFileLineByLine(String filepath, boolean hasHeader) {
        return readFileLineByLine(filepath, null, hasHeader);
    }

    public static List<String> readFileLineByLine(String filepath, Integer limit, boolean hasHeader) {
        int count = 0;
        int max;
        if (limit == null) {
            max = Integer.MAX_VALUE;
        } else {
            max = limit;
        }
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String line = null;
            //ignore fist line
            if (hasHeader) {
                line = br.readLine();
            }
            line = br.readLine();
            count++;
            while (line != null) {
                lines.add(line);
                line = br.readLine();
                count++;
                if (count > max) {
                    break;
                }
            }
            br.close();
            return lines;
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
