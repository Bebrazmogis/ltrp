package lt.ltrp.data;

import lt.ltrp.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class PlayerFriskOffer extends PlayerOffer {

    public PlayerFriskOffer(LtrpPlayer player, LtrpPlayer offeredBy, EventManager eventManager) {
        super(player, offeredBy, eventManager, 45, PlayerFriskOffer.class);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerFriskOffer && getPlayer().equals(((PlayerFriskOffer) obj).getPlayer()) && getOfferedBy().equals(((PlayerFriskOffer) obj).getOfferedBy());
    }
}
