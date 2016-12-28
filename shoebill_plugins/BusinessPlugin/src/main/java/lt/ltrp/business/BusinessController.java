package lt.ltrp.business;

import lt.ltrp.constant.BusinessType;
import lt.ltrp.dao.BusinessDao;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2016.05.18.
 */
public interface BusinessController {

    Business get(LtrpPlayer player);
    Business createBusiness(int id, String name, BusinessType type, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Color labelColor,
                            int money, int resources, int commodityLimit);
    Collection<Business> getBusinesses();
    List<BusinessCommodity> getAvailableCommodities(BusinessType type);
    BusinessDao getBusinessDao();
    void showManagementDialog(LtrpPlayer player);
    void showAvailableCommodityDialog(LtrpPlayer player);
    Business getClosest(Location location, float maxDistance);

    class Instance {
        static BusinessController instance;
    }

    static BusinessController get() {
        return Instance.instance;
    }



}
