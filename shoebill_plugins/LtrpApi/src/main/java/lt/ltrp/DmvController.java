package lt.ltrp;

import lt.ltrp.dao.DmvDao;
import lt.ltrp.object.Dmv;
import lt.ltrp.object.DmvVehicle;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.20.
 */
public interface DmvController {

    DmvVehicle createVehicle(Dmv dmv, int id, int modelId, AngledLocation location, int color1, int color2, float mileage, EventManager eventManager);
    DmvDao getDao();

    static class Instance {
        static DmvController instance;
    }

    static DmvController get() {
        return Instance.instance;
    }



}
