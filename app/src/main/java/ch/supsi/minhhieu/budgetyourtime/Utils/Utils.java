package ch.supsi.minhhieu.budgetyourtime.Utils;

import java.util.HashMap;

/**
 * Created by acer on 09/08/2016.
 */

public class Utils {

    public static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static HashMap<String, String> toMap(String[] a) {
        HashMap<String, String> map = new HashMap<>();
        for (String s : a) {
            String[] kv = s.split("=");
            if (kv.length > 1) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    public static HashMap<String, String> toMap1(String[] a) {
        HashMap<String, String> map = new HashMap<>();
        for (String s : a) {
            String[] kv = s.split(":");
            if (kv.length > 1) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
