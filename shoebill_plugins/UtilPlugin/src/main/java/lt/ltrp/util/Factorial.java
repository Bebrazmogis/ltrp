package lt.ltrp.util;

/**
 * @author Bebras
 *         2015.12.19.
 */
public class Factorial {

    public static int get(int number) {
        if(number == 0) {
            return 0;
        }
        return number + get(number-1);
    }



}
