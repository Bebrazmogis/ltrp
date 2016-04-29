package lt.ltrp.textdraw;

import lt.ltrp.data.Color;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.constant.TextDrawAlign;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.object.PlayerTextdraw;

/**
 * @author Bebras
 *         2016.04.29.
 */
public class GameTextStyle7Textdraw extends AbstractGameTextCustomStyle {

    public static GameTextStyle7Textdraw create(LtrpPlayer player) {
        return new GameTextStyle7Textdraw(player);
    }

    private GameTextStyle7Textdraw(LtrpPlayer player) {
        super(player, PlayerTextdraw.create(player, 320f, 175f, "TYPE 7"));
        getTextdraw().setLetterSize(0.5f, 1.5f);
        getTextdraw().setAlignment(TextDrawAlign.CENTER);
        getTextdraw().setOutlineSize(1);
        getTextdraw().setBackgroundColor(new Color(51));
        getTextdraw().setFont(TextDrawFont.BANK_GOTHIC);
        getTextdraw().setProportional(true);
    }


    @Override
    public int getStyleId() {
        return 7;
    }

}
