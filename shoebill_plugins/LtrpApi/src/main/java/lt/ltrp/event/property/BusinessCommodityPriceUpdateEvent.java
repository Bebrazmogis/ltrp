package lt.ltrp.event.property;

import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessCommodityPriceUpdateEvent extends BusinessEvent {

    private BusinessCommodity commodity;

    public BusinessCommodityPriceUpdateEvent(Business property, LtrpPlayer player, BusinessCommodity commodity) {
        super(property, player);
        this.commodity = commodity;
    }

    public BusinessCommodityPriceUpdateEvent(Property property, BusinessCommodity commodity) {
        super(property);
        this.commodity = commodity;
    }

    public BusinessCommodity getCommodity() {
        return commodity;
    }
}
