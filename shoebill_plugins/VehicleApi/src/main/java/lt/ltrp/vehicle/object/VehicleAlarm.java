package lt.ltrp.vehicle.object;


import lt.ltrp.vehicle.object.PlayerVehicle;
import lt.ltrp.vehicle.VehicleController;

/**
 * @author Bebras
 *         2015.12.18.
 */
public interface VehicleAlarm {

    static VehicleAlarm get(PlayerVehicle vehicle, int level) {
        return VehicleController.get().createAlarm(vehicle, level);
        /*switch(level) {
            case 1:
                return new SimpleAlarm("Paprasta signalizacija", vehicle);
            case 2:
                return new PoliceAlertAlarm("Profesonali signalizacija su GPS ir PD ryðiu", vehicle);
            case 3:
                return new PersonalAlarm("Pro. Signalizacija su GPS, policijos ir asmeniniu praneðikliu ", vehicle);
            default:
                return null;
        }*/
    }

    int getLevel();

    String getName();
    void setName(String s);

    void activate();
    void deactivate();

    /**
     * Returns true if the implementation supports In character "gps tracking"
     * @return
     */
    boolean isFindable();
}
