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
        ltToLatinChar.put('�', 'a');
        ltToLatinChar.put('�', 'c');
        ltToLatinChar.put('�', 'e');
        ltToLatinChar.put('�', 'e');
        ltToLatinChar.put('�', 'i');
        ltToLatinChar.put('�', 's');
        ltToLatinChar.put('�', 'u');
        ltToLatinChar.put('�', 'z');

        ltToLatinChar.put('�', 'A');
        ltToLatinChar.put('�', 'C');
        ltToLatinChar.put('�', 'E');
        ltToLatinChar.put('�', 'E');
        ltToLatinChar.put('�', 'I');
        ltToLatinChar.put('�', 'S');
        ltToLatinChar.put('�', 'U');
        ltToLatinChar.put('�', 'Z');
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

    public static boolean isNumeric(String s) {
        for(char c : s.toCharArray()) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}
