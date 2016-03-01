package lt.ltrp.job.trashman;

import lt.ltrp.LoadingException;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.PawnFunc;
import lt.ltrp.dao.JobDao;
import lt.ltrp.data.Color;
import lt.ltrp.job.AbstractJobManager;
import lt.ltrp.job.Job;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.JobVehicle;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.PlayerAttach;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.17.
 */
public class TrashmanManager extends AbstractJobManager {

    protected static final int TRASH_SLOT = 4;

    private TrashMissions trashMissions;
    private Map<LtrpPlayer, PlayerTrashMission> playerTrashMissions;
    private Map<JobVehicle, Integer> vehicleTrashCounts;
    private PlayerCommandManager commandManager;
    private Checkpoint dumpCheckpoint;
    private TrashManJob job;

    public TrashmanManager(EventManager eventManager, int id) throws LoadingException {
        super(eventManager);
        JobDao jobDao = LtrpGamemode.getDao().getJobDao();
        job = jobDao.getTrashmanJob(id);
        this.trashMissions = jobDao.getTrashMissions();
        this.playerTrashMissions = new HashMap<>();
        this.vehicleTrashCounts = new HashMap<>();
        this.commandManager = new PlayerCommandManager(eventManagerNode);
        this.commandManager.installCommandHandler(HandlerPriority.NORMAL);
        this.commandManager.registerCommands(new TrashmanCommands(job,  this, playerTrashMissions, vehicleTrashCounts));

        this.dumpCheckpoint = Checkpoint.create(new Radius(PawnFunc.Data_GetLocation("job_trash_dropoff"), 20.0f), (e) -> {
            LtrpPlayer player = LtrpPlayer.get(e.getId());
            if(player != null) {
                if(player.getVehicle() != null) {
                    JobVehicle vehicle = JobVehicle.getById(player.getVehicle().getId());
                    if(vehicle != null) {
                        dumpCheckpoint.disable(player);
                        playerTrashMissions.remove(player);
                        vehicleTrashCounts.put(vehicle, 0);
                        player.sendMessage(Color.NEWS, "Baigëte misijà. Jums prie algos buvo pridëti " + job.getTrashRouteBonus() + "$ Norëdami pradëti dar vienà misijà: /startmission");
                        AmxCallable addPaycheck = PawnFunc.getPublicMethod("AddPlayerPaycheck");
                        if(addPaycheck != null) {
                            addPaycheck.call(player.getId(), job.getTrashRouteBonus());
                        }
                    }
                }
            }
        }, null);


        this.eventManagerNode.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                if(playerTrashMissions.containsKey(player)) {
                    PlayerTrashMission playerTrashMission = playerTrashMissions.get(player);
                    playerTrashMission.end();
                }
            }
        });

        this.eventManagerNode.registerHandler(PlayerStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                if(player.getState().equals(PlayerState.DRIVER)) {
                    JobVehicle vehicle = JobVehicle.getById(player.getVehicle().getId());
                    if (vehicle != null && vehicle.getJob().equals(job)) {
                        if(playerTrashMissions.containsKey(player)) {
                            PlayerTrashMission playerTrashMission = playerTrashMissions.get(player);
                            if (vehicleTrashCounts.get(vehicle) == job.getTrashMasterCapacity()) {
                                player.setCheckpoint(dumpCheckpoint);
                            } else {
                                playerTrashMission.progress++;
                                playerTrashMission.showCheckpoint();
                            }
                        } else {
                            player.sendGameText(7, 3000, "~n~~n~~n~Rinkite siuksles ið pazymetø tasku~n~Naudokite /takegarbage ju paemimui~n~Siame sunkvezimyje yra " + vehicleTrashCounts.get(vehicle) + " maisai");
                            player.sendMessage(Color.NEWS, "Rinkite ðiukðles ið paþymëtø taðkø. Naudokite /takegarbage jø paëmimui. Ðiame sunkveþimyje yra " + vehicleTrashCounts.get(vehicle) + " maiðai");
                        }
                    }

                }
            }
        });

        this.eventManagerNode.registerHandler(VehicleDeathEvent.class, e -> {
            JobVehicle vehicle = JobVehicle.getById(e.getVehicle().getId());
            if(vehicle != null) {
                if(vehicleTrashCounts.containsKey(vehicle)) {
                    vehicleTrashCounts.put(vehicle, 0);
                }
            }
        });
    }

    public TrashMissions getTrashMissions() {
        return trashMissions;
    }

    @Override
    public Job getJob() {
        return job;
    }

    @Override
    public void destroy() {
        commandManager.uninstallAllHandlers();
        super.destroy();
    }



    protected class PlayerTrashMission {
        private TrashMission mission;
        private int progress;
        private Checkpoint checkpoint;
        private LtrpPlayer player;
        private boolean holdingTrash, ended;
        private Timer timer;

        public PlayerTrashMission(TrashMission mission, LtrpPlayer player) {
            this.mission = mission;
            this.progress = 0;
            this.player = player;
        }

        public void end() {
            if(getCheckpoint() != null) {
                getCheckpoint().disable(player);
            }
            if(isHoldingTrash()) {
                PlayerAttach.PlayerAttachSlot slot = player.getAttach().getSlot(TrashmanManager.TRASH_SLOT);
                if (slot.isUsed()) {
                    slot.remove();
                }
                player.setSpecialAction(SpecialAction.NONE);
                holdingTrash = false;
            }
            if(timer != null && timer.isRunning()) {
                timer.stop();
            }
            timer = null;
            ended = true;
        }

        public void showCheckpoint() {
            setCheckpoint(Checkpoint.create(new Radius(getMission().getGarbage(getProgress()).getLocation(), 5.0f), null, null));
            player.setCheckpoint(getCheckpoint());
        }

        public void pickupTrash() {
            if(!isHoldingTrash()) {
                PlayerAttach.PlayerAttachSlot slot = player.getAttach().getSlot(TrashmanManager.TRASH_SLOT);
                if(!slot.isUsed()) {
                    player.setSpecialAction(SpecialAction.CARRY);
                    slot.set(PlayerAttachBone.HAND_LEFT, 1265, new Vector3D(0.100000f, 0.553958f, -0.024002f), new Vector3D(356.860290f, 269.945068f, 0.000000f), new Vector3D(0.834606f, 1.000000f, 0.889027f), 0, 0);
                    holdingTrash = true;
                }
            }
        }

        public void throwGarbage(int delay) {
            if(isHoldingTrash()) {
                PlayerAttach.PlayerAttachSlot slot = player.getAttach().getSlot(TrashmanManager.TRASH_SLOT);
                if(slot.isUsed()) {
                    player.setSpecialAction(SpecialAction.NONE);
                    if(timer != null) {
                        timer.destroy();
                    }
                    timer = Timer.create(delay, 1, ticks -> {
                        slot.remove();
                    });
                }
            }
        }

        public void disableCheckpoint() {
            if(getCheckpoint() != null) {
                checkpoint.disable(player);
            }
        }

        public boolean isEnded() {
            return ended;
        }

        public TrashMission getMission() {
            return mission;
        }

        public void setMission(TrashMission mission) {
            this.mission = mission;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public Checkpoint getCheckpoint() {
            return checkpoint;
        }

        public void setCheckpoint(Checkpoint checkpoint) {
            this.checkpoint = checkpoint;
        }

        public boolean isHoldingTrash() {
            return holdingTrash;
        }

        public void setHoldingTrash(boolean holdingTrash) {
            this.holdingTrash = holdingTrash;
        }
    }
}
