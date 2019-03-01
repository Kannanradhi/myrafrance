package com.isteer.b2c.utility;

import java.text.DecimalFormat;

public class ValidationUtils {

    public static boolean isEmpty(String l) {

        if (l == null || l.length() == 0 || l.equalsIgnoreCase("") || l.equalsIgnoreCase("null"))
            return true;
        else
            return false;
    }

    public static String stringFormater(String l) {

        if (l == null || l.length() == 0 || l.equalsIgnoreCase("") || l.equalsIgnoreCase("null"))
            return "";
        else
            return l;
    }

    public static String languageFormater(String l) {

        if (l == null || l.length() == 0 || l.equalsIgnoreCase("") || l.equalsIgnoreCase("null"))
            return "en";
        else
            return l;
    }


    public static Integer integerFormat(String i) {
        if (i == null || i.equals(" ") || i.length() == 0 || i.contains(".")) {
            return 0;
        } else {
            return Integer.parseInt(i);
        }


    }

    public static Double doubleFormat(String i) {
        if (i == null || i.equals(" ") || i.length() == 0) {
            return 0.0;
        } else {
            return Double.parseDouble(i);
        }
    }

    public static double doubleValueFormater(String d) {
        return doubleFormat(new DecimalFormat("##.00").format(doubleFormat(d)));
    }

}

