package lt.ltrp.event.vehicle;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import lt.ltrp.object.VehicleAlarm;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class PlayerVehicleBuyAlarmEvent extends PlayerVehicleEvent {

    private VehicleAlarm alarm;

    public PlayerVehicleBuyAlarmEvent(LtrpPlayer player, PlayerVehicle vehicle, VehicleAlarm alarm) {
        super(player, vehicle);
        this.alarm = alarm;
    }

    public VehicleAlarm getAlarm() {
        return alarm;
    }
}
