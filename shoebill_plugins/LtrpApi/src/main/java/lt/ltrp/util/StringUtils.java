package lt.ltrp.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Bebras
 *         2016.02.22.
 */
public class StringUtils {

    private static final Map<Character, Character> ltToLatinChar = new HashMap<>(9);
    private static final String regex = "[ÀÈÆËÁÐØÛÞàèëáðøûþ]";
    private static final Map<String, String> colorsToRGB = new HashMap<>();

    static {
        ltToLatinChar.put('à', 'a');
        ltToLatinChar.put('è', 'c');
        ltToLatinChar.put('æ', 'e');
        ltToLatinChar.put('ë', 'e');
        ltToLatinChar.put('á', 'i');
        ltToLatinChar.put('ð', 's');
        ltToLatinChar.put('ø', 'u');
        ltToLatinChar.put('þ', 'z');

        ltToLatinChar.put('À', 'A');
        ltToLatinChar.put('È', 'C');
        ltToLatinChar.put('Æ', 'E');
        ltToLatinChar.put('Ë', 'E');
        ltToLatinChar.put('Á', 'I');
        ltToLatinChar.put('Ð', 'S');
        ltToLatinChar.put('Ø', 'U');
        ltToLatinChar.put('Þ', 'Z');

        colorsToRGB.put("{RAUDONA}", "{FF0000}");
        colorsToRGB.put("{ZALIA}", "{00FF00}");
        colorsToRGB.put("[GELTONA}", "{FFFF00}");
        colorsToRGB.put("{MELYNA}", "{0000FF}");
        colorsToRGB.put("{ZYDRA}", "{00FFFF}");
        colorsToRGB.put("{VIOLETINE}", "{FF00FF}");
        colorsToRGB.put("{GELTONA}", "{FFFF00}");
    }



    public static boolean equalsIgnoreLtChars(String s1, String s2) {
        String first = replaceLtChars(s1);
        String second = replaceLtChars(s2);
        return first.equals(second);
    }

    public static boolean equalsIgnoreLtCharsAndCase(String s1, String s2) {
        String first = replaceLtChars(s1.toLowerCase());
        String second = replaceLtChars(s2.toLowerCase());
        return first.equals(second);
    }

    public static String replaceLtChars(String s) {
        String retS = s;
        for(Character character : ltToLatinChar.keySet()) {
            retS = retS.replace(character, ltToLatinChar.get(character));
        }
        return retS;
    }

    public static String stripLtChars(String s) {
        return s.replaceAll(regex, "");
    }

    public static boolean isNumeric(String s) {
        for(char c : s.toCharArray()) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parses colour codes like {RAUDONA} or {MELYNA}
     * @param text text to be parsed
     * @return returns the formatted text
     */
    public static String parseTextColors(String text) {
        for(Iterator<String> it = colorsToRGB.keySet().iterator();  it.hasNext();) {
            String key = it.next();
            System.out.println("Searching for: "+  Pattern.quote(key) + " replacing with:" + colorsToRGB.get(key));
            text = text.replaceAll(Pattern.quote(key), colorsToRGB.get(key));
        }
        return text;
    }

    /**
     * Escapes text embedding colors in format {RRGGBB}
     * @param text
     * @return
     */
    public static String escapeColors(String text) {
        return text.replaceAll("\\{", "\\\\\\\\{");
    }

    /**
     * Strips all SAMP color codes
     * @param text
     * @return
     */
    public static String stripColors(String text) {
        return text.replaceAll("(\\{\\p{XDigit}{6}\\})|~[a-z]~", "");
    }

    public static String limit(String text, int len, String suffix) {
        return text.length() + suffix.length() > len ? text.substring(0, len) + suffix : text;
    }

    public static String limit(String text, int len) {
        return limit(text, len, "");
    }


    public static String addLineBreaks(String text, int lineLength) {
        StringBuilder builder = new StringBuilder();
        String[] words = text.split(" ");
        int len = 0;
        for(String s : words) {
            if((len += s.length()) > lineLength) {
                len = 0;
                builder.append("\n");
            }
            builder.append(s);
        }
        return builder.toString();
    }
}
