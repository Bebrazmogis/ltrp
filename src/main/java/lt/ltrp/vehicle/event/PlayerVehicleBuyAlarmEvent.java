package lt.ltrp.vehicle.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.PlayerVehicle;
import lt.ltrp.vehicle.VehicleAlarm;

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
