package lt.ltrp.dao;

import net.gtaun.shoebill.data.Location;

/**
 * @author Bebras
 *         2016.06.01.
 */
public interface AdvertisementCenterDao {

    Location get();
    void set(Location location);

    int getLetterPrice();
    void setLetterPrice(int letterPrice);

}
