package lt.ltrp.dmv;

import lt.ltrp.vehicle.FuelTank;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.object.Vehicle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.02.14.
 */
public class DmvVehicle extends LtrpVehicle {

    private static final List<WeakReference<DmvVehicle>> dmvVehicles = new ArrayList<>();

    public static DmvVehicle create(Dmv dmv, int id, int modelid, AngledLocation location, int color1, int color2) {
        DmvVehicle v = new DmvVehicle(dmv, id, modelid, location, color1, color2);
        dmvVehicles.add(new WeakReference<DmvVehicle>(v));
        return v;
    }

    public static DmvVehicle create(Dmv dmv, int id, Vehicle vehicle, FuelTank fuelTank) {
        DmvVehicle v = new DmvVehicle(dmv, id, vehicle, fuelTank);
        dmvVehicles.add(new WeakReference<DmvVehicle>(v));
        return v;
    }

    private Dmv dmv;

    private DmvVehicle(Dmv dmv, int id, int modelid, AngledLocation location, int color1, int color2) {
        super(id, modelid, location, color1, color2, dmv.getName().length() >= 3 ? dmv.getName().substring(0, 3) + modelid : dmv.getName() + modelid);
        this.dmv = dmv;
    }

    private DmvVehicle(Dmv dmv, int id, Vehicle vehicle, FuelTank fuelTank) {
        super(id, vehicle, fuelTank);
        this.dmv = dmv;
    }

    public Dmv getDmv() {
        return dmv;
    }

    public void setDmv(Dmv dmv) {
        this.dmv = dmv;
    }
}
