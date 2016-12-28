package lt.ltrp;

import lt.ltrp.object.LtrpVehicle;import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.20.
 */
public interface DmvVehicle extends LtrpVehicle {

    static DmvVehicle create(Dmv dmv, int id, int modelId, AngledLocation location, int color1, int color2, float mileage, EventManager eventManager) {
        return DmvController.get().createVehicle(dmv, id, modelId, location, color1, color2, mileage, eventManager);
    }

    Dmv getDmv();
    void setDmv(Dmv dmv);

}
