package lt.ltrp.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.02.22.
 */
public class StringUtils {

    private static final Map<Character, Character> ltToLatinChar = new HashMap<>(9);

    static {
        ltToLatinChar.put('à', 'a');
        ltToLatinChar.put('è', 'c');
        ltToLatinChar.put('æ', 'e');
        ltToLatinChar.put('ë', 'ë');
        ltToLatinChar.put('á', 'i');
        ltToLatinChar.put('ð', 's');
        ltToLatinChar.put('ø', 'u');
        ltToLatinChar.put('þ', 'þ');

        ltToLatinChar.put('À', 'A');
        ltToLatinChar.put('È', 'C');
        ltToLatinChar.put('Æ', 'E');
        ltToLatinChar.put('Ë', 'E');
        ltToLatinChar.put('Á', 'I');
        ltToLatinChar.put('Ð', 'S');
        ltToLatinChar.put('Ø', 'U');
        ltToLatinChar.put('Þ', 'Z');
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
        String retS = "";
        for(Character character : ltToLatinChar.keySet()) {
            retS = s.replace(character, ltToLatinChar.get(character));
        }
        return retS;
    }

}
