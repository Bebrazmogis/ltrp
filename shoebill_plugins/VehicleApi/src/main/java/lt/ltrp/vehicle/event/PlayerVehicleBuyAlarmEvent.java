package lt.ltrp.vehicle.event;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.object.PlayerVehicle;
import lt.ltrp.vehicle.object.VehicleAlarm;

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
