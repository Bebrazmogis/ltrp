package lt.ltrp.util;



import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.util.Base64;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class Whirlpool {

    private static final gnu.crypto.hash.Whirlpool whirlpool = new gnu.crypto.hash.Whirlpool();

    public static String hash(String text) {
        //AbstractChecksum checksum;
        IMessageDigest instance = HashFactory.getInstance("WHIRLPOOl");
        //instance.update(text.getBytes(), 0, text.getBytes().length);
        whirlpool.update(text.getBytes(), 0, text.getBytes().length);
        /*try {
            checksum = JacksumAPI.getChecksumInstance("whirlpool");
            checksum.update(text.getBytes());
            return checksum.getFormattedValue().toUpperCase();
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }*/
        return Base64.encode(whirlpool.digest());
    }
}
