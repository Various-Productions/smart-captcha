package me.lucacw.smartcaptcha.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
public final class StringUtils {

    public static boolean isValidPrefix(String s) {
        return s != null && s.matches("^[a-zA-Z0-9!@#$&()-`.+,/\"]*$");
    }

    public static int parseInt(String s, int d) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return d;
        }
    }

    public static List<String> formatList(List<String> list, String... replace) {
        List<String> r = new ArrayList<>();
        List<String> o = new ArrayList<>();
        List<String> ret = new ArrayList<>();
        fillLists(r, o, replace);
        for (String format : list) {
            for (int i = 0; i < r.size(); i++) {
                format = (format.replaceAll(r.get(i), o.get(i)));
            }
            ret.add(format);
        }
        return ret;
    }

    public static String format(String s, String... replace) {
        if (replace.length < 2) {
            return s;
        }
        List<String> r = new ArrayList<>();
        List<String> o = new ArrayList<>();
        fillLists(r, o, replace);
        return formatString(s, r, o);
    }

    public static void fillLists(List<String> r, List<String> o, String... replace) {
        for (int i = 0; i < replace.length; i++) {
            if ((i & 0x1) > 0) {
                o.add(replace[i]);
            } else {
                r.add(replace[i]);
            }
        }
    }

    public static String formatString(String s, List<String> r, List<String> o) {
        if (r.size() != o.size()) {
            throw new IllegalArgumentException("Die beiden List<String> r und o dürfen keine Unterschiedliche Länge haben!");
        }
        for (int i = 0; i < r.size(); i++) {
            String replace = r.get(i);
            String object = o.get(i);
            if (s.contains(replace)) {
                if (object != null) {
                    s = s.replace(replace, object);
                } else {
                    System.out.println("Object " + o + " for replace " + replace + " is null!");
                }
            }
        }
        return s;
    }

    public static String split(String s, String regex, int index, String defaultValue) {
        String[] a = s.split(regex);
        return index < a.length ? a[index] : defaultValue;
    }

    public static String addPadding(String s, int l) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = sb.length(); i < l; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String removeLeading(String s, Predicate<Character> predicate) {
        boolean b = true;
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!(b && predicate.test(c))) {
                b = false;
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String removeBack(String s, Predicate<Character> predicate) {
        boolean b = true;
        StringBuilder sb = new StringBuilder();
        char[] a = s.toCharArray();
        for (int i = a.length - 1; i >= 0; i--) {
            char c = a[i];
            if (!(b && predicate.test(c))) {
                b = false;
                sb.append(c);
            }
        }
        return sb.reverse().toString();
    }

    public static String removeLeadingWhiteSpaces(String s) {
        boolean b = true;
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!(b && c == ' ')) {
                b = false;
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static int count(String s, char c) {
        int count = 0;
        for (char e : s.toCharArray()) {
            if (e == c) {
                count++;
            }
        }
        return count;
    }

    public static String shortenString(String s, int maxChars) {
        if (s.length() <= maxChars) return s;

        return s.substring(0, maxChars) + "...";
    }

    public static boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}
