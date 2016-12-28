package lt.ltrp.business.event;

import lt.ltrp.business.commodity.BusinessCommodity;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class SelectCommodityEvent extends PlayerEvent {

    private BusinessCommodity commodity;

    public SelectCommodityEvent(LtrpPlayer player, BusinessCommodity commodity) {
        super(player);
        this.commodity = commodity;
    }

    public BusinessCommodity getCommodity() {
        return commodity;
    }
}
