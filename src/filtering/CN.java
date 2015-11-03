/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filtering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mas.IOUtils;

/**
 *
 * @author Amirhossein Aleyasen <aleyase2@illinois.edu>
 * created on Oct 18, 2015, 11:16:31 PM
 */
class CNEntity {

    String first;
    String second;

    public CNEntity() {
    }

    public CNEntity(String first, String second) {
        this.first = first;
        this.second = second;
    }

}

public class CN {

    public static String SEPERATOR = "\t";
    public static String NEWLINE = "\n";
    List<CNEntity> list;

    public CN() {
        list = new ArrayList<>();
    }

    public CN(String file) {
        this();
        read(file);
    }

    public void read(String file) {
        System.out.println("CN:read file=" + file);
        final List<String> lines = IOUtils.readFileLineByLine(file, false);
        for (String l : lines) {
            String[] split = l.split(SEPERATOR);
            String first = split[0];
            String second = split[1];
            CNEntity ce = new CNEntity(first, second);
            list.add(ce);
        }
    }

    public void write(String file) {
        System.out.println("CN:write file=" + file);
        StringBuilder stb = new StringBuilder();
        for (CNEntity ce : list) {
            stb.append(ce.first).append(SEPERATOR).append(ce.second).append(NEWLINE);
        }
        IOUtils.writeDataIntoFile(stb.toString(), file, false);
    }

    public static CN filter(DL first, DL second, String conn_file) {
        System.out.println("CN:filter first=" + first + " second=" + second + " conn_file=" + conn_file);
        final List<String> lines = IOUtils.readFileLineByLine(conn_file, false);
        CN filtered = new CN();
        for (String l : lines) {
            String[] split = l.split(SEPERATOR);
            String first_key = split[0];
            String second_key = split[1];
            if (first != null && second != null) {
                if (first.contains(first_key) && second.contains(second_key)) {
                    filtered.add(first_key, second_key);
                }
            } else if (first != null) {
//                final DataElement de = first.get(first_key);
                if (first.contains(first_key)) {
                    filtered.add(first_key, second_key);
                }
            } else if (second != null) {
//                final DataElement de = first.get(first_key);
                if (second.contains(second_key)) {
                    filtered.add(first_key, second_key);
                }
            } else {
                filtered.add(first_key, second_key);
            }
        }
        return filtered;
    }

    public List<String> first() {
        List<String> firsts = new ArrayList<>();
        for (CNEntity ce : list) {
            firsts.add(ce.first);
        }
        return firsts;
    }

    public List<String> second() {
        List<String> seconds = new ArrayList<>();
        for (CNEntity ce : list) {
            seconds.add(ce.second);
        }
        return seconds;
    }

    private void add(String first_key, String second_key) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(new CNEntity(first_key, second_key));
    }

    public int size() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }
}
