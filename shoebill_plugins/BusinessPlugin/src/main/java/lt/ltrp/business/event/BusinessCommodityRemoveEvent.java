package lt.ltrp.business.event;

import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.Property;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessCommodityRemoveEvent extends BusinessEvent {

    private BusinessCommodity commodity;

    public BusinessCommodityRemoveEvent(Business property, LtrpPlayer player, BusinessCommodity commodity) {
        super(property, player);
        this.commodity = commodity;
    }

    public BusinessCommodityRemoveEvent(Property property, BusinessCommodity commodity) {
        super(property);
        this.commodity = commodity;
    }

    public BusinessCommodity getCommodity() {
        return commodity;
    }
}
