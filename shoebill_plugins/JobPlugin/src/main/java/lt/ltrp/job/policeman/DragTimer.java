package lt.ltrp.job.policeman;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Timer;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Bebras
 *         2016.02.05.
 */
public class DragTimer implements Destroyable {

    private static final int DRAG_INTERVAL = 500;

    private Timer timesInstance;
    private LtrpPlayer player,target;


    public static DragTimer create(LtrpPlayer player, LtrpPlayer dragging) {
        return new DragTimer(player, dragging, Timer.create(DRAG_INTERVAL, (i) -> {
            LtrpVehicle vehicle = player.getVehicle();
            if(vehicle != null && !dragging.isInAnyVehicle()) {
                List<LtrpPlayer> passengers = LtrpPlayer.get().stream().filter(p -> p.isInVehicle(vehicle)).collect(Collectors.toList());
                if(passengers.size() < VehicleModel.getSeats(vehicle.getModelId())) {
                    vehicle.putPlayer(dragging);
                }
            }
            else {
                AngledLocation location = player.getLocation();
                location.setY(location.getY() + 1f);
            }
        }));
    }

    public DragTimer(LtrpPlayer p, LtrpPlayer p2, Timer t) {
        this.player = p;
        this.target = p2;
        this.timesInstance = t;
    }


    @Override
    public void destroy() {
        timesInstance.stop();
        timesInstance.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return timesInstance.isDestroyed();
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public LtrpPlayer getTarget() {
        return target;
    }
}
