package lt.ltrp.Util;

/**
 * @author Bebras
 *         2015.11.30.
 */
public class Sql {


    public static java.util.Date convert(java.sql.Date date) {
        return date;
    }

    public static java.sql.Date convert(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }


}
