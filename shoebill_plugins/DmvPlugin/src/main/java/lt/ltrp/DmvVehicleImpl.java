package lt.ltrp;

import lt.ltrp.data.FuelTank;
import lt.ltrp.object.Dmv;
import lt.ltrp.object.DmvVehicle;
import lt.ltrp.vehicle.object.impl.LtrpVehicleImpl;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.14.
 */
public class DmvVehicleImpl extends LtrpVehicleImpl implements DmvVehicle {

    private static String generateLicense(Dmv dmv, int modelId) {
        return dmv.getName().length() >= 3 ? dmv.getName().substring(0, 3) + modelId : dmv.getName() + modelId;
    }

    private Dmv dmv;
    private EventManager eventManager;

    public DmvVehicleImpl(Dmv dmv, int id, int modelId, AngledLocation location, int color1, int color2, float mileage, EventManager eventManager) {
        super(id, modelId, location, color1, color2, new FuelTank(1000f, 1000f), generateLicense(dmv, modelId), mileage, eventManager);
        this.dmv = dmv;
        this.eventManager = eventManager;
    }

    public Dmv getDmv() {
        return dmv;
    }

    public void setDmv(Dmv dmv) {
        this.dmv = dmv;
    }

    @Override
    public void destroy() {
        super.destroy();
        eventManager.dispatchEvent(new DmvVehicleDestroyEvent(this));
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

}
