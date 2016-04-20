package lt.ltrp.event.property;

import lt.ltrp.data.BusinessCommodity;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessCommodityAddEvent extends BusinessEvent {

    private BusinessCommodity commodity;

    public BusinessCommodityAddEvent(Business property, LtrpPlayer player, BusinessCommodity commodity) {
        super(property, player);
        this.commodity = commodity;
    }

    public BusinessCommodityAddEvent(Property property, BusinessCommodity commodity) {
        super(property);
        this.commodity = commodity;
    }

    public BusinessCommodity getCommodity() {
        return commodity;
    }
}
