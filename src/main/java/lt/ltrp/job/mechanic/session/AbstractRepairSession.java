package lt.ltrp.job.mechanic.session;

import lt.ltrp.data.Animation;
import lt.ltrp.job.mechanic.event.PlayerLeaveRepairArea;
import lt.ltrp.job.mechanic.event.RepairSessionEndEvent;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerCountdown;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerDeathEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.02.08.
 */
public abstract class AbstractRepairSession {

    private static final float MAX_DISTANCE = 8f;

    private int duration;
    private LtrpPlayer player;
    private LtrpVehicle vehicle;
    private PlayerCountdown countdown;
    private RepairSessionEndHandler handler;
    private Map<PlayerKey, Animation> keyAnimations;
    private EventManagerNode eventManagerNode;
    private boolean leaveEventDispatched;

    public AbstractRepairSession(EventManager eventManager, int duration, LtrpPlayer player, LtrpVehicle vehicle, RepairSessionEndHandler handler) {
        this.eventManagerNode = eventManager.createChildNode();
        this.keyAnimations = new HashMap<>();
        this.duration = duration;
        this.player = player;
        this.vehicle = vehicle;
        this.countdown = new PlayerCountdown(player, duration, false, (p, finished) -> {
            if(finished) {
                eventManagerNode.cancelAll();
                if(handler != null) {
                    handler.onSessionEnd(player, vehicle, countdown.getTimeleft() == 0);
                }
                eventManagerNode.dispatchEvent(new RepairSessionEndEvent(player, this, true));
                destroy();
            }
        });

        this.eventManagerNode.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(p != null && p.equals(player)) {
                eventManagerNode.dispatchEvent(new RepairSessionEndEvent(player,  this, false));
                destroy();
            }
        });


        this.eventManagerNode.registerHandler(PlayerDeathEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(p != null && p.equals(player)) {
                eventManagerNode.dispatchEvent(new RepairSessionEndEvent(player,  this, false));
                destroy();
            }
        });

        this.eventManagerNode.registerHandler(VehicleDeathEvent.class, e -> {
            LtrpVehicle v = LtrpVehicle.getByVehicle(e.getVehicle());
            if(v != null && v.equals(vehicle)) {
                eventManagerNode.dispatchEvent(new RepairSessionEndEvent(player,  this, false));
                destroy();
            }
        });

        this.eventManagerNode.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(p != null && p.equals(this.player)) {
                keyAnimations.keySet().
                        stream().
                        filter(k -> !e.getOldState().isKeyPressed(k) && p.getKeyState().isKeyPressed(k))
                        .forEach(k -> p.applyAnimation(keyAnimations.get(k)));
            }
        });

        this.eventManagerNode.registerHandler(PlayerUpdateEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(p != null && p.equals(player)) {
                float distance = p.getLocation().distance(vehicle.getLocation());
                if(countdown.isPaused() && distance <= MAX_DISTANCE) {
                    countdown.resume();
                    leaveEventDispatched = false;
                } else if(distance > MAX_DISTANCE && !leaveEventDispatched) {
                    eventManagerNode.dispatchEvent(new PlayerLeaveRepairArea(player, vehicle, distance)); // This has to come before pausing or else the eventNode will be destroyed
                    countdown.pause();
                    leaveEventDispatched = true;
                }
            }
        });
    }

    protected void addAnimation(PlayerKey key, Animation anim) {
        this.keyAnimations.put(key, anim);
    }

    public int getDuration() {
        return duration;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public LtrpVehicle getVehicle() {
        return vehicle;
    }

    public Map<PlayerKey, Animation> getKeyAnimations() {
        return keyAnimations;
    }

    public void destroy() {
        this.countdown.destroy();
        if(!this.eventManagerNode.isDestroy()) {
            eventManagerNode.cancelAll();
            eventManagerNode.destroy();
        }
        leaveEventDispatched = false;
    }

    public int getTimeLeft() {
        return countdown.getTimeleft();
    }

    @FunctionalInterface
    public interface RepairSessionEndHandler {
        void onSessionEnd(LtrpPlayer player, LtrpVehicle vehicle, boolean success);
    }
}
