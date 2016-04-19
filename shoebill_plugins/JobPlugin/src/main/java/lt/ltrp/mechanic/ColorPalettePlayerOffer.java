package lt.ltrp.mechanic;

import lt.ltrp.data.PlayerOffer;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class ColorPalettePlayerOffer extends PlayerOffer {

    public ColorPalettePlayerOffer(LtrpPlayer player, LtrpPlayer offeredBy, EventManager eventManager) {
        super(player, offeredBy, eventManager, 60, ColorPalettePlayerOffer.class);
    }
}
