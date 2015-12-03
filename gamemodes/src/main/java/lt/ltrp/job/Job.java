package lt.ltrp.job;

import net.gtaun.shoebill.data.Location;

/**
 * @author Bebras
 *         2015.12.03.
 */
public interface Job {

    int getId();
    Location getLocation();
    void setLocation(Location loc);


}
