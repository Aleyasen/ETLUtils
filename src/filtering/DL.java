/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filtering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mas.IOUtils;

/**
 *
 * @author Amirhossein Aleyasen <aleyase2@illinois.edu>
 * created on Oct 18, 2015, 11:16:31 PM
 */
public class DL {

    public static String SEPERATOR = "\t";
    public static String NEWLINE = "\n";
    Map<String, DataElement> map;

    public DL() {
        map = new HashMap<>();
    }

    public DL(String file) {
        this();
        read(file);
    }

    public void read(String file) {
        System.out.println("DL:read file=" + file);
        final List<String> lines = IOUtils.readFileLineByLine(file, false);
        for (String l : lines) {
            String[] split = l.split(SEPERATOR);
            String id = split[0];
            String data = l;
            DataElement de = new DataElement(id, data);
            map.put(id, de);
        }
    }

    public void write(String file) {
        System.out.println("DL:write file=" + file);
        StringBuilder stb = new StringBuilder();
        for (String key : map.keySet()) {
            stb.append(map.get(key).data).append(NEWLINE);
        }
        IOUtils.writeDataIntoFile(stb.toString(), file, false);
    }

    public DataElement get(String id) {
        final DataElement de = map.get(id);
        return de;
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public void put(String id, DataElement dt) {
        map.put(id, dt);
    }

    public DL filter(List<String> keys) {
        System.out.println("DL:filter keys=" + keys);
        DL filtered = new DL();
        for (String key : map.keySet()) {
            if (keys.contains(key)) {
                filtered.put(key, map.get(key));
            }
        }
        return filtered;
    }

    public int size() {
        if (map == null) {
            return 0;
        }
        return map.size();
    }
}
