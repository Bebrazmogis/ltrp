package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.command.TrashmanCommands;
import lt.ltrp.constant.Currency;
import lt.ltrp.dao.TrashManJobDao;
import lt.ltrp.dao.impl.MySqlTrashManJobImpl;
import lt.ltrp.data.Animation;
import lt.ltrp.data.Color;
import lt.ltrp.data.TrashMission;
import lt.ltrp.data.TrashMissions;
import lt.ltrp.event.PlayerTrashMissionEndEvent;
import lt.ltrp.event.PlayerTrashMissionStartEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.TrashManJob;
import lt.ltrp.object.impl.PlayerTrashMission;
import lt.ltrp.object.impl.TrashManJobImpl;
import lt.ltrp.player.util.PlayerUtils;
import lt.ltrp.resource.DependentPlugin;
import lt.ltrp.util.VehicleUtils;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.common.timers.TemporaryTimer;
import net.gtaun.shoebill.constant.*;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerDeathEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerSpawnEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerAttach;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class TrashmanJobPlugin extends DependentPlugin {

    private static final Animation ANIMATION_THROW_GARBAGE = new Animation("GRENADE", "WEAPON_THROWU", false, 400);
    private static final Animation ANIMATION_PICKUP_GARBAGE = new Animation("CARRY", "liftup05", false, 500);
    private static final int DISCONNECT_MISSION_END_DELAY = 10 * 60 * 1000;
    private EventManagerNode eventManager;
    private PlayerCommandManager playerCommandManager;
    private Logger logger;
    private TrashManJob trashManJob;
    private TrashManJobDao trashManJobDao;
    private Checkpoint dropOffCheckpoint;
    private Map<LtrpPlayer, PlayerTrashMission> playerTrashMissions;

    private Consumer<Player> vehicleCheckpointEnterConsumer = (p) -> {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerTrashMission mission = getPlayerTrashMission(player);
        if(mission == null)
            return;

        player.getCheckpoint().disable(player);
        PlayerAttach.PlayerAttachSlot slot = PlayerUtils.getSlotByBone(player, PlayerAttachBone.HAND_LEFT);
        if(slot.isUsed()) {
            player.setSpecialAction(SpecialAction.NONE);
            player.applyAnimation(ANIMATION_THROW_GARBAGE);
            TemporaryTimer t = TemporaryTimer.create(ANIMATION_THROW_GARBAGE.getTime() - 40, 1, ticks -> {
                slot.remove();
            });
            t.start();
            player.sendGameText(7, 2000, trashManJob.getTrashPickupBonus() + Currency.SYMBOL + " prideta prie algos");
            player.addCurrentPaycheck(trashManJob.getTrashPickupBonus());
            mission.setCheckpoint(null);
        }
    };

    private Consumer<Player> garbageCheckpointEnterConsumer = (p) -> {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerTrashMission mission = getPlayerTrashMission(player);
        if(mission == null)
            return;
        PlayerAttach.PlayerAttachSlot slot = PlayerUtils.getSlotByBone(player, PlayerAttachBone.HAND_LEFT);
        if(!slot.isUsed()) {
            player.applyAnimation(ANIMATION_PICKUP_GARBAGE);
            Checkpoint cp = Checkpoint.create(new Radius(VehicleUtils.getBehind(mission.getVehicle()), 8f), vehicleCheckpointEnterConsumer, null);
            player.setCheckpoint(cp);
            mission.setCheckpoint(cp);
            TemporaryTimer.create(500, 1, (i)-> {
                player.setSpecialAction(SpecialAction.CARRY);
                slot.set(PlayerAttachBone.HAND_LEFT, mission.getMission().getGarbage(mission.getProgress()).getModelId(), new Vector3D(0.100000f, 0.553958f, -0.024002f), new Vector3D(356.860290f, 269.945068f, 0.000000f), new Vector3D(0.834606f, 1.000000f, 0.889027f), 0, 0);
            }).start();
            mission.incrementProgress();
        }
    };

    public TrashmanJobPlugin() {
        addDependency(new KClassImpl<>(DatabasePlugin.class));
        addDependency(new KClassImpl<>(JobPlugin.class));
    }


    @Override
    public void onDependenciesLoaded() {
        this.playerTrashMissions = new HashMap<>();
        eventManager = getEventManager().createChildNode();
        logger = getLogger();
        trashManJobDao = new MySqlTrashManJobImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), eventManager);
        this.trashManJob = new TrashManJobImpl(JobPlugin.JobId.TrashMan.id, eventManager);

        addCommands();
        addEventHandlers();
        createCheckpoints();
    }

    private void createCheckpoints() {
        this.dropOffCheckpoint =  Checkpoint.create(new Radius(trashManJob.getDropOffLocation(), 9f), p -> {
            LtrpPlayer player = LtrpPlayer.get(p);
            if(player != null && player.getVehicle() != null && player.getVehicle().getModelId() == VehicleModel.TRASHMASTER) {
                PlayerTrashMission mission = getPlayerTrashMission(player);
                mission.setCheckpoint(null);
                player.addCurrentPaycheck(trashManJob.getTrashRouteBonus());
                player.sendMessage(Color.NEWS, "Baigëte misijà. Jums prie algos buvo pridëti "
                        + trashManJob.getTrashRouteBonus() + Currency.SYMBOL + " Norëdami pradëti dar vienà misijà: /startmission");
                endPlayerMission(mission);
            }
        }, null);
    }

    private void addCommands() {
        playerCommandManager = new PlayerCommandManager(eventManager);
        playerCommandManager.registerCommands(new TrashmanCommands());
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

    private void addEventHandlers() {
        this.eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(e.getReason() == DisconnectReason.TIME_OUT && player != null) {
                if(playerTrashMissions.containsKey(player)) {
                    // If a player is on a trash route and crashes, he has DISCONNECT_MISSION_END_DELAY minutes to come back to work
                    TemporaryTimer.create(DISCONNECT_MISSION_END_DELAY, 1, (i) -> {
                        if(!player.isOnline())
                            endPlayerMission(getPlayerTrashMission(player));
                    }).start();
                }
            }
        });

        this.eventManager.registerHandler(PlayerSpawnEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            PlayerTrashMission mission = getPlayerTrashMission(player);
            if(mission != null) {
                player.sendMessage(Color.BLANCHEDALMOND, "Norëdami tæsti savo ðiukðliø reisà, susiraskite senàjà transporto priemonæ ir paraðykite /startmission");
            }
        });

        this.eventManager.registerHandler(PlayerDeathEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            PlayerTrashMission mission = getPlayerTrashMission(player);
            if(mission != null) {
                player.sendMessage(Color.NEWS, "Jûsø ðiukðliø veþimo reisas baigtas.");
            }
        });

        this.eventManager.registerHandler(PlayerStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            // Basically if he entered a {@link net.gtaun.shoebill.constant.VehicleModel#TRASHMASTER} as a driver
            if(player != null && player.getState().equals(PlayerState.DRIVER) &&
                    player.getVehicle() != null && player.getVehicle().getModelId() == VehicleModel.TRASHMASTER) {
                PlayerTrashMission mission = getPlayerTrashMission(player);
                if(mission != null) {
                    // If he picked up the last trash bag in current route
                    if(mission.getProgress() == mission.getMission().getGarbageCount()) {
                        player.sendMessage(Color.NEWS, "Surinkote visas ðiukðles ðiame rajone, veþkite jas á ðiukðlynà. Jis buvo paþymëtas jûsø þemëlapyje.");
                        player.setCheckpoint(dropOffCheckpoint);
                    } else {
                        player.sendMessage(Color.NEWS, "Rinkite ðiukðles ið paþymëtø taðkø, uþ kiekvienà paimta maiðà jums bus pridëta " + trashManJob.getTrashPickupBonus() + Currency.SYMBOL + " prie algos.");
                    }
                }
            }
        });

    }

    @Override
    protected void onDisable() {
        super.onDisable();
        playerCommandManager.uninstallAllHandlers();
        playerTrashMissions.clear();
        playerTrashMissions = null;
        eventManager.cancelAll();
    }

    public PlayerTrashMission getPlayerTrashMission(LtrpPlayer player) {
        return playerTrashMissions.get(player);
    }

    public void startMission(LtrpPlayer player, PlayerTrashMission playerTrashMission) {
        playerTrashMissions.put(player, playerTrashMission);
        TrashMission mission = playerTrashMission.getMission();
        Checkpoint checkpoint = Checkpoint.create(new Radius(mission.getGarbage(0).getPosition(), 8f), garbageCheckpointEnterConsumer, null);
        player.setCheckpoint(checkpoint);
        playerTrashMission.setCheckpoint(checkpoint);
        eventManager.dispatchEvent(new PlayerTrashMissionStartEvent(player, playerTrashMission));
    }

    public TrashMissions getMissions() {
        return getJob().getMissions();
    }

    public void endPlayerMission(PlayerTrashMission mission) {
        playerTrashMissions.remove(mission.getPlayer());
        mission.getPlayer().disableCheckpoint();
        eventManager.dispatchEvent(new PlayerTrashMissionEndEvent(mission.getPlayer(), mission));
    }

    public Checkpoint getDropOffCheckpoint() {
        return dropOffCheckpoint;
    }

    public TrashManJob getJob() {
        return trashManJob;
    }
}
