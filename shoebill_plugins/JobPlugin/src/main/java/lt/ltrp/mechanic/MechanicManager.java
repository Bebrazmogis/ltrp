package lt.ltrp.mechanic;

import lt.ltrp.AbstractJobManager;
import lt.ltrp.LoadingException;
import lt.ltrp.event.vehicle.VehicleEngineStartEvent;
import lt.ltrp.mechanic.event.PlayerLeaveRepairArea;
import lt.ltrp.mechanic.event.RepairSessionEndEvent;
import lt.ltrp.mechanic.session.AbstractRepairSession;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.object.MechanicJob;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.02.08.
 */
public class MechanicManager extends AbstractJobManager {


    public static final Map<String, Integer> WHEEL_COMPONENTS = new HashMap<>();

    static {
        WHEEL_COMPONENTS.put("Offroad", 1025);
        WHEEL_COMPONENTS.put("Mega", 1074);
        WHEEL_COMPONENTS.put("Wires", 1076);
        WHEEL_COMPONENTS.put("Twist", 1078);
        WHEEL_COMPONENTS.put("Grove", 1081);
        WHEEL_COMPONENTS.put("Import", 1082);
        WHEEL_COMPONENTS.put("Atomic", 1085);
        WHEEL_COMPONENTS.put("Ahab", 1096);
        WHEEL_COMPONENTS.put("Virtual", 1097);
        WHEEL_COMPONENTS.put("Access", 1098);
        WHEEL_COMPONENTS.put("Trance", 1084);
        WHEEL_COMPONENTS.put("Shadow", 1073);
        WHEEL_COMPONENTS.put("Rimshine", 1075);
        WHEEL_COMPONENTS.put("Classic", 1077);
        WHEEL_COMPONENTS.put("Cutter", 1079);
        WHEEL_COMPONENTS.put("Switch", 1080);
        WHEEL_COMPONENTS.put("Dollar", 1083);
    }



    private MechanicJob job;
    private Map<LtrpPlayer, AbstractRepairSession> repairSessionMap;
    private PlayerCommandManager commandManager;
    private Map<LtrpPlayer, LtrpPlayer> playerTargetOffers;
    private Map<LtrpPlayer, Timer> offerTimers;

    public MechanicManager(EventManager eventManager, int id) throws LoadingException {
        super(eventManager);
        this.repairSessionMap = new HashMap<>();
        this.playerTargetOffers = new HashMap<>();
        this.offerTimers = new HashMap<>();

        //this.job = JobController.get().getDao().getMechanicJob(id);

        CommandGroup group = new CommandGroup();
        group.registerCommands(new MechanicAcceptCommands(eventManagerNode));

        commandManager = new PlayerCommandManager(eventManagerNode);
        commandManager.registerCommands(new MechanicCommands(job, eventManagerNode, this));
        commandManager.installCommandHandler(HandlerPriority.NORMAL);
        commandManager.registerChildGroup(group, "accept");




        eventManagerNode.registerHandler(VehicleEngineStartEvent.class, e -> {
            Optional<AbstractRepairSession> optionlSession = repairSessionMap.values().stream().filter(s -> s.getVehicle().equals(e.getVehicle())).findFirst();
            if(optionlSession.isPresent()) {
                e.getPlayer().sendErrorMessage("Mechanikas dirba prie ðios transporto priemonës, niekur vaþiuoti negalite!");
                e.deny();
            }
        });

        eventManagerNode.registerHandler(RepairSessionEndEvent.class, e -> {
            repairSessionMap.remove(e.getPlayer());
        });

        eventManagerNode.registerHandler(PlayerLeaveRepairArea.class, e -> {
           e.getPlayer().sendErrorMessage("Pasitraukëte per toli nuo taisomo automobilio! Laikas sustabdytas, gráþkite per 15 sekundþiø arba taisymas bus nutrauktas.");
        });
    }

    public PlayerCommandManager getPlayerCommandManager() {
        return commandManager;
    }

    protected void addRepairSession(AbstractRepairSession session) {
        repairSessionMap.put(session.getPlayer(), session);
    }

    protected boolean isVehicleInSession(LtrpVehicle vehicle) {
        return repairSessionMap.values().stream().filter(s -> s.getVehicle().equals(vehicle)).findFirst().isPresent();
    }

    protected boolean isPlayerInSession(LtrpPlayer player) {
        return repairSessionMap.containsKey(player);
    }

    protected void addOffer(LtrpPlayer player, LtrpPlayer target) {
        playerTargetOffers.put(player, target);
        offerTimers.put(target, Timer.create(30000, 1, (i) -> {
            offerTimers.remove(target);
            playerTargetOffers.remove(player);
        }));
    }

    protected boolean isOffered(LtrpPlayer player) {
        return playerTargetOffers.values().stream().filter(p -> p.equals(player)).findFirst().isPresent();
    }


    @Override
    public MechanicJob getJob() {
        return job;
    }

    @Override
    public void destroy() {
        offerTimers.values().forEach(Timer::destroy);
        commandManager.destroy();
        super.destroy();
    }
}
