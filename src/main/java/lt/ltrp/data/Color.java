package lt.ltrp.data;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class Color extends net.gtaun.shoebill.data.Color {

    public static final net.gtaun.shoebill.data.Color LIGHTRED = new net.gtaun.shoebill.data.Color(0xFF, 0x63, 0x47);
    public static final net.gtaun.shoebill.data.Color NEWS = new net.gtaun.shoebill.data.Color(0xFF, 0xA5, 0x00);
    public static final net.gtaun.shoebill.data.Color ACTION = new net.gtaun.shoebill.data.Color(0xC2, 0xA2, 0xDA); //C2 A2 DA AA
    public static final net.gtaun.shoebill.data.Color SMS_SENT = new net.gtaun.shoebill.data.Color(0x00, 0x00, 0x00);
    public static final net.gtaun.shoebill.data.Color SMS_RECEIVED = new net.gtaun.shoebill.data.Color(0xF5, 0xDE, 0xB3);  //0xF5DEB3AA
    public static final net.gtaun.shoebill.data.Color MEGAPHONE = new net.gtaun.shoebill.data.Color(0x42, 0x61, 0xCC); //0x4261CCFF
    public static final net.gtaun.shoebill.data.Color POLICE = new net.gtaun.shoebill.data.Color(0x35, 0xA5, 0xCA); // 0x35A5CAFF
    public static final net.gtaun.shoebill.data.Color DMV = new net.gtaun.shoebill.data.Color(0xFF, 0xFF, 0xFF);
    public static final net.gtaun.shoebill.data.Color RADIO = new net.gtaun.shoebill.data.Color(0x8D, 0x8D, 0xFF); // 0x8D8DFF00
    public static final net.gtaun.shoebill.data.Color DIALOG = new net.gtaun.shoebill.data.Color(0xA9, 0xC4, 0xE4); // A9C4E4
    public static final net.gtaun.shoebill.data.Color PM_RECEIVED = new net.gtaun.shoebill.data.Color(0xBB, 0xA0, 0x33); //0xBBA033AA
    public static final net.gtaun.shoebill.data.Color PM_SENT = new net.gtaun.shoebill.data.Color(0xE5, 0xC4, 0x3E); //0xE5C43EAA
    public static final net.gtaun.shoebill.data.Color MODERATOR = new net.gtaun.shoebill.data.Color(0xA4, 0xDE6, 0x3); // 0xA4DE63FF

    public Color(int value) {
        super(value);
    }

    public Color(int r, int g, int b) {
        super(r, g, b);
    }

    public Color(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public Color(net.gtaun.shoebill.data.Color color) {
        super(color);
    }

    public Color() {
    }
}
