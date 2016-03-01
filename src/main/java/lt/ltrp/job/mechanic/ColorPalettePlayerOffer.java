package lt.ltrp.job.mechanic;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerOffer;
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
