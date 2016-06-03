package lt.ltrp;

import lt.ltrp.command.*;
import lt.ltrp.dao.PoliceFactionDao;
import lt.ltrp.dao.impl.MySqlPoliceFactionImpl;
import lt.ltrp.data.Animation;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.event.PlayerJailEvent;
import lt.ltrp.object.JobVehicle;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PoliceFaction;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.common.timers.TemporaryTimer;
import net.gtaun.shoebill.constant.BulletHitType;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.event.player.*;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class PoliceJobPlugin extends Plugin {

    private EventManagerNode eventManager;
    private Logger logger;
    protected Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    protected Map<JobVehicle, DynamicObject> policeSirens = new HashMap<>();
    protected Map<LtrpPlayer, DragTimer> dragTimers;
    private PlayerCommandManager commandManager;
    private Collection<LtrpPlayer> playersOnDuty;
    private PoliceFaction policeFaction;
    private PoliceFactionDao policeFactionDao;
    private Map<LtrpPlayer, LtrpWeaponData> taserSlotWeaponCache;
    private Collection<LtrpPlayer> playersUsingTaser;

    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        eventManager = getEventManager().createChildNode();
        this.unitLabels = new HashMap<>();
        this.policeSirens = new HashMap<>();
        this.dragTimers = new HashMap<>();
        this.playersOnDuty = new ArrayList<>();
        this.taserSlotWeaponCache = new HashMap<>();
        this.playersUsingTaser = new ArrayList<>();


        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(JobPlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            eventManager.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();

    }

    private  void load() {
        eventManager.cancelAll();
        policeFactionDao = new MySqlPoliceFactionImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), null, eventManager);
        policeFaction = policeFactionDao.get(JobPlugin.JobId.Officer.id);

        registerCommands();
        registerEventHandlers();

        logger.info(getDescription().getName() + " loaded");
    }

    private void registerCommands() {
        commandManager = new PlayerCommandManager(eventManager);
        CommandGroup acceptGroup = new CommandGroup();
        acceptGroup.registerCommands(new CivilianAcceptCommands());

        commandManager.registerChildGroup(acceptGroup, "accept");
        commandManager.replaceTypeParser(LtrpPlayer.class, s -> {
            try {
                return LtrpPlayer.get(Integer.parseInt(s));
            } catch(NumberFormatException e) {
                return LtrpPlayer.getByPartName(s);
            }
        });

        commandManager.registerCommands(new PoliceCommands(commandManager, policeFaction, eventManager, unitLabels, policeSirens, dragTimers));
        commandManager.registerCommands(new RoadblockCommands(policeFaction, eventManager));
        commandManager.registerCommands(new CivilianCommands());
        commandManager.registerCommands(new PoliceLeaderCommands(this));
        commandManager.registerCommands(new DepartmentChatCommand(getPoliceFaction()));
        commandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

    private void registerEventHandlers() {
        eventManager.registerHandler(VehicleDeathEvent.class, e -> {
            JobVehicle jobVehicle = JobVehicle.getById(e.getVehicle().getId());
            if (jobVehicle != null) {
                if (unitLabels.containsKey(jobVehicle)) {
                    unitLabels.get(jobVehicle).destroy();
                    unitLabels.remove(jobVehicle);
                }
                if (policeSirens.containsKey(jobVehicle)) {
                    policeSirens.get(jobVehicle).destroy();
                    policeSirens.remove(jobVehicle);
                }
            }
        });

        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if (player != null) {
                Optional<DragTimer> timer = dragTimers.values().stream().filter(dt -> dt.getTarget().equals(player) || dt.getPlayer().equals(player)).findFirst();
                if (timer.isPresent()) {
                    DragTimer t = timer.get();
                    dragTimers.remove(t.getPlayer());
                    t.destroy();
                    t.getTarget().toggleControllable(true);
                }
                if(playersUsingTaser.contains(player)) setTaser(player, false);
            }
        });

        eventManager.registerHandler(PlayerDeathEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if (player != null) {
                Optional<DragTimer> timer = dragTimers.values().stream().filter(dt -> dt.getTarget().equals(player) || dt.getPlayer().equals(player)).findFirst();
                if (timer.isPresent()) {
                    DragTimer t = timer.get();
                    dragTimers.remove(t.getPlayer());
                    t.destroy();
                    t.getTarget().toggleControllable(true);
                }
            }
        });

        eventManager.registerHandler(PlayerWeaponShotEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            // DENY the taser damage
            if(isUsingTaser(player) && e.getHitType() == BulletHitType.PLAYER) {
                e.disallow();
            }
        });

        eventManager.registerHandler(PlayerTakeDamageEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            LtrpPlayer issuer = LtrpPlayer.get(e.getIssuer());
            if(issuer != null && isUsingTaser(issuer) && issuer.getDistanceToPlayer(player) <= 7) {
                issuer.sendActionMessage("iððauna elektros ðokà nutaikæs á " + player.getCharName() + " ir nukreèia su didele átampa.");
                player.sendActionMessage("nuo ðûvio nugriûva ant þemës ir negali pajudëti...");
                player.toggleControllable(false);
                player.applyAnimation(new Animation("CRACK", "crckdeth2", true, 0));
                TemporaryTimer.create(30000, 1, (i) -> {
                    player.toggleControllable(true);
                    player.clearAnimations();
                    // TODO stand up anim
                });
                setTaser(issuer, false);
            }
        });

        eventManager.registerHandler(PlayerStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            // When entering a vehicle we should hide users taser
            if(isUsingTaser(player) && (player.getState() == PlayerState.DRIVER || player.getState() == PlayerState.PASSENGER)) {
                player.sendActionMessage("prieð ásësdamas á transporto priemonæ, ásideda tazerá á dëklà");
                setTaser(player, false);
            }
        });


        eventManager.registerHandler(PlayerJailEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            Optional<DragTimer> optionalDrag = dragTimers.values().stream().filter(d -> d.getTarget().equals(p)).findFirst();
            if(optionalDrag.isPresent()) {
                dragTimers.remove(p);
                optionalDrag.get().destroy();
                p.toggleControllable(true);
            }
        });

    }

    public boolean isOnDuty(LtrpPlayer player) {
        return playersOnDuty.contains(player);
    }

    public void setOnDuty(LtrpPlayer player, boolean set) {
        if(set)
            playersOnDuty.add(player);
        else
            playersOnDuty.remove(player);
    }

    public PoliceFaction getPoliceFaction() {
        return policeFaction;
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        commandManager.uninstallAllHandlers();
        commandManager.destroy();
        playersUsingTaser.clear();
        taserSlotWeaponCache.forEach((p, w) -> setTaser(p, false));
        taserSlotWeaponCache.clear();
        logger.info(getDescription().getName() + " unloaded");
    }


    public void setTaser(LtrpPlayer player, boolean enabled) {
        if(enabled) {
            // If we turn it on, we add the user to a list
            playersUsingTaser.add(player);
            // If he has any weapon in tasers slot, we cache it
            LtrpWeaponData weapon = player.getWeaponData(WeaponModel.SILENCED_COLT45);
            if(weapon != null) {
                taserSlotWeaponCache.put(player, weapon);
                player.removeWeapon(weapon);
            }
            player.giveWeapon(new LtrpWeaponData(WeaponModel.SILENCED_COLT45, 10, true));
        } else {
            playersUsingTaser.remove(player);
            // Remove the instance of silencer
            player.removeWeapon(player.getWeaponData(WeaponModel.SILENCED_COLT45));
            // If he had cached weapons, give them back
            LtrpWeaponData weapon = taserSlotWeaponCache.get(player);
            if(weapon != null) {
                player.giveWeapon(weapon);
                taserSlotWeaponCache.remove(player);
            }
        }
    }

    public boolean isUsingTaser(LtrpPlayer player) {
        return playersUsingTaser.contains(player);
    }
}
