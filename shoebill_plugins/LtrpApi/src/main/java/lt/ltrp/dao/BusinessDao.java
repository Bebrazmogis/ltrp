package lt.ltrp.dao;

import lt.ltrp.constant.BusinessType;
import lt.ltrp.data.BusinessCommodity;
import lt.ltrp.object.Business;

import java.util.List;

/**
 * @author Bebras
 *         2016.04.19.
 */
public interface BusinessDao {

    void update(Business business);
    void insert(Business business);
    void remove(Business business);
    Business get(int uuid);
    void read();

    List<BusinessCommodity> get(Business business);
    void update(BusinessCommodity commodity);
    void insert(BusinessCommodity commodity);
    void remove(BusinessCommodity commodity);

    void insert(BusinessType type, BusinessCommodity commodity);
    void remove(BusinessType type, BusinessCommodity commodity);
    List<BusinessCommodity> get(BusinessType type);

}
