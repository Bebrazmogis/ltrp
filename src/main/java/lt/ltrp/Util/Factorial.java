package lt.ltrp.Util;

/**
 * @author Bebras
 *         2015.12.19.
 */
public class Factorial {

    public static int get(int number) {
        return number + get(number-1);
    }



}
