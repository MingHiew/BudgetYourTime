package ch.supsi.minhhieu.budgetyourtime.Utils;

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


}
