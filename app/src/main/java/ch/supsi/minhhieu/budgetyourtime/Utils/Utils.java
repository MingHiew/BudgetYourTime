package ch.supsi.minhhieu.budgetyourtime.Utils;

import java.util.HashMap;

import ch.supsi.minhhieu.budgetyourtime.R;

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

    public static String getCityName(String place){
        String[] a = place.split(", ");
        return a[a.length-2];
    }

    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }
}
