package com.sanyecao.hu.fever_thermometer.utils;

import java.util.HashSet;

/**
 * Created by huhaisong on 2017/9/12 16:24.
 */

public class StringUtils {

    public static String medicinesHashSetToString(HashSet<String> medicines) {
        String string = "";
        for (String item : medicines) {
            string +=  item +"," ;
        }
        return string;
    }

    public static HashSet<String> medicinesStringToHashSet(String string) {
        HashSet<String> medicines = new HashSet<>();
        String[] strings = string.split(",");
        for (String item : strings) {
            medicines.add(item);
        }
        return medicines;
    }
}
