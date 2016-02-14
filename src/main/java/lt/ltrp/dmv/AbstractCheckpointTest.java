package lt.ltrp.dmv;

import lt.ltrp.dmv.event.PlayerDrivingTestEndEvent;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.event.SpeedometerTickEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Bebras
 *         2015.12.26.
 */
public abstract class AbstractCheckpointTest implements DmvTest {

    private LtrpPlayer player;
    private LtrpVehicle vehicle;
    private int cpCount;
    private boolean finished, passed;
    private EventManager eventManager;
    private HandlerEntry onPlayerDisconnectEntry;
    private HandlerEntry onVehicleDeathEntry;
    private HandlerEntry onPlayerStateChangeEntry;
    private CheckpointDmv dmv;

    private final Consumer<Player> onEnterConsumer = (p) -> {
        DmvCheckpoint[] checkpoints = dmv.getCheckpoints();
        if (++cpCount == checkpoints.length) {
            onFinish();
        } else {
            showNextCheckpoint();
        }
    };


    public AbstractCheckpointTest(LtrpPlayer p, LtrpVehicle vehicle, CheckpointDmv dmv, EventManager manager) {
        System.out.println(String.format("AbstractCheckpointTest constructor"));
        this.dmv = dmv;
        this.cpCount = 0;
        this.player = p;
        this.eventManager = manager;
        this.vehicle = vehicle;
        this.passed = true; // This is important! This is the only place where it is set to true, change this nobody will ever get a license :/
        showNextCheckpoint();

        vehicle.getState().setDoors(VehicleParam.PARAM_ON);

        onPlayerDisconnectEntry = eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if (player != null && player.equals(this.player)) {
                onFinish();
            }
        });

        onVehicleDeathEntry = eventManager.registerHandler(VehicleDeathEvent.class, e -> {
            LtrpVehicle v = LtrpVehicle.getByVehicle(e.getVehicle());
            if (v != null && vehicle.equals(e.getVehicle())) {
                onFinish();
            }
        });

        onPlayerStateChangeEntry = eventManager.registerHandler(PlayerStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if (p != null && p.equals(player)) {
                onFinish();
            }
        });
    }


    private void showNextCheckpoint() {
        DmvCheckpoint checkpoint = dmv.getCheckpoints()[cpCount];
        System.out.println("AbstractCheckpointTest showNextCheckpoint. cpCount:" + cpCount + " Checkpoint location:" + checkpoint.getRadius());
        checkpoint.create(onEnterConsumer).set(player);
    }

    protected void onFinish() {
        player.disableCheckpoint();
        finished = true;
        onPlayerDisconnectEntry.cancel();
        onVehicleDeathEntry.cancel();
        onPlayerStateChangeEntry.cancel();
        vehicle.getState().setDoors(VehicleParam.PARAM_OFF);
        vehicle.respawn();
        player.setLocation(dmv.getLocation());

        if (cpCount < dmv.getCheckpoints().length) {
            passed = false;
            finished = false;
        }
        dispatchEvent();
    }

    protected abstract void dispatchEvent();



    protected EventManager getEventManager() {
        return eventManager;
    }

    protected void setPassed(boolean passed) {
        this.passed = passed;
    }

    protected void setFinished(boolean finished) {
        this.finished = finished;
    }

    public CheckpointDmv getDmv() {
        return dmv;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return player;
    }

    public LtrpVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isPassed() {
        return passed;
    }

    @Override
    public void stop() {
        onFinish();
    }

}
