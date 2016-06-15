package lt.ltrp.object;

import net.gtaun.shoebill.data.Color;

/**
 * @author Bebras
 *         2016.06.14.
 */
public interface LabelWeaponShop extends WeaponShop {

    Color getColor();
    void setColor(Color color);

    String getText();
    void setText(String text);

}
