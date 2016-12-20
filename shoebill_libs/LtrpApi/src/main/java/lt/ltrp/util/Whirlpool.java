package lt.ltrp.util;




import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import java.security.NoSuchAlgorithmException;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class Whirlpool {

    public static String hash(String text) {
        AbstractChecksum checksum = null;
        try {
            checksum = JacksumAPI.getChecksumInstance("whirlpool");
            checksum.update(text.getBytes());
            return checksum.getFormattedValue().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
