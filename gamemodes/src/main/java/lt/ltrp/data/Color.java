package lt.ltrp.data;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class Color extends net.gtaun.shoebill.data.Color {

    public static final net.gtaun.shoebill.data.Color LIGHTRED = new net.gtaun.shoebill.data.Color(0xFF, 0x63, 0x47);
    public static final net.gtaun.shoebill.data.Color NEWS = new net.gtaun.shoebill.data.Color(0xFF, 0xA5, 0x00);

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
