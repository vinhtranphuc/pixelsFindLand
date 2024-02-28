
package tpvinh.config.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

     private static ObjectMapper objectMapper;
     static {
        objectMapper = new ObjectMapper();
     }

    /**
     * check exists for optional argument
     * @param prm
     * @return
     */
    public static boolean isOptExists(Object[] prm) {
        return Objects.nonNull(prm) && prm.length > 0 && Objects.nonNull(prm[0]);
    }

    /**
     * list all times between minute
     * @param eachMinute
     * @return
     */
    public static List<String> times(int eachMinute) {
        List<String> quarterHours = quarterHours(eachMinute);
        List<String> times = new ArrayList<String>(); // <-- List instead of array
        for(int i = 0; i < 24; i++) {
            for(int j = 0; j < quarterHours.size(); j++) {
                String time = i + ":" + quarterHours.get(j);
                if(i < 10) {
                    time = "0" + time;
                }
                times.add(time); // <-- no need to care about indexes
            }
        }
        return times;
    }

    /**
     * list quarter hours for each minute
     * @param eachMinute
     * @return
     */
    public static List<String> quarterHours(int eachMinute) {
        DecimalFormat formatter = new DecimalFormat("00");
        List<String> quarterHours = new ArrayList<String>();
        for(int m = 0; m < 60; m++) {
            if((m % eachMinute) == 0) {
                quarterHours.add(formatter.format(m));
            }
        }
        return quarterHours;
    }

    @SuppressWarnings("unchecked")
    public static Map<String,Object> toMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }
}
