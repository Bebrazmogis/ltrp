package lt.ltrp.vehicle;

import net.gtaun.shoebill.object.VehicleParam;

/**
 * @author Bebras
 *         2015.12.18.
 *
 *         A simple {@link lt.ltrp.vehicle.VehicleAlarm} interface
 *         Merely the native alarm will be turned on and a text message
 */
public class SimpleAlarm implements VehicleAlarm {


    private String name;
    private PlayerVehicle vehicle;

    public SimpleAlarm(String name, PlayerVehicle vehicle) {
        this.name = name;
        this.vehicle = vehicle;
    }

    public SimpleAlarm(PlayerVehicle vehicle) {
        this("Paprasta signalizacija", vehicle);
    }



    protected PlayerVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public int getLevel() {
        return 1;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public void activate() {
        vehicle.sendStateMessage("Pypsi tr. priemonës signalizacija");
        vehicle.getState().setAlarm(VehicleParam.PARAM_ON);
    }

    @Override
    public void deactivate() {
        vehicle.getState().setAlarm(VehicleParam.PARAM_OFF);
    }
}
