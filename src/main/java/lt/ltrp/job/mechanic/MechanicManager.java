package lt.ltrp.job.mechanic;

import lt.ltrp.job.JobManager;
import lt.ltrp.job.mechanic.event.PlayerLeaveRepairArea;
import lt.ltrp.job.mechanic.event.RepairSessionEndEvent;
import lt.ltrp.job.mechanic.session.AbstractRepairSession;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.event.VehicleEngineStartEvent;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.VehicleComponent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.02.08.
 */
public class MechanicManager {

    private static final int JOB_ID = 1;
    /**
     * The amount it costs to remove hydraulics from a vehicle
     */
    protected static final int REMOVE_HYDRAULICS_PRICE = 200;
    protected static final int INSTALL_HYDRAULICs_PRICE = 1500;
    protected static final int WHEEL_PRICE = 400;

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

    public static final String[] WHEEL_NAMES = new String[]{

    };


    private EventManagerNode eventNode;
    private MechanicJob job;
    private Map<LtrpPlayer, AbstractRepairSession> repairSessionMap;
    private PlayerCommandManager commandManager;
    private Map<LtrpPlayer, LtrpPlayer> playerTargetOffers;
    private Map<LtrpPlayer, Timer> offerTimers;

    public MechanicManager(EventManager eventManager) {
        eventNode = eventManager.createChildNode();
        this.repairSessionMap = new HashMap<>();
        this.playerTargetOffers = new HashMap<>();
        this.offerTimers = new HashMap<>();

        this.job = new MechanicJob(JobManager.getContractJob(JOB_ID));

        CommandGroup group = new CommandGroup();
        group.registerCommands(new MechanicAcceptCommands(eventNode));

        commandManager = new PlayerCommandManager(eventNode);
        commandManager.registerCommands(new MechanicCommands(job, eventNode, this));
        commandManager.installCommandHandler(HandlerPriority.NORMAL);
        commandManager.registerChildGroup(group, "accept");




        eventNode.registerHandler(VehicleEngineStartEvent.class, e -> {
            Optional<AbstractRepairSession> optionlSession = repairSessionMap.values().stream().filter(s -> s.getVehicle().equals(e.getVehicle())).findFirst();
            if(optionlSession.isPresent()) {
                e.getPlayer().sendErrorMessage("Mechanikas dirba prie ðios transporto priemonës, niekur vaþiuoti negalite!");
                e.deny();
            }
        });

        eventNode.registerHandler(RepairSessionEndEvent.class, e -> {
            repairSessionMap.remove(e.getPlayer());
        });

        eventNode.registerHandler(PlayerLeaveRepairArea.class, e -> {
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



}
