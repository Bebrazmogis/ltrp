package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.command.MechanicAcceptCommands;
import lt.ltrp.command.MechanicCommands;
import lt.ltrp.dao.MechanicJobDao;
import lt.ltrp.dao.impl.MySqlMechanicJobDaoImpl;
import lt.ltrp.event.PlayerLeaveRepairArea;
import lt.ltrp.event.RepairSessionEndEvent;
import lt.ltrp.event.vehicle.VehicleEngineStartEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.object.MechanicJob;
import lt.ltrp.object.impl.MechanicJobImpl;
import lt.ltrp.resource.DependentPlugin;
import lt.ltrp.session.AbstractRepairSession;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MechanicJobPlugin extends DependentPlugin {

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

    private EventManagerNode eventManager;
    private Map<LtrpPlayer, AbstractRepairSession> repairSessionMap;
    private PlayerCommandManager playerCommandManager;
    private Logger logger;
    private MechanicJobDao mechanicDao;
    private MechanicJob mechanicJob;

    public MechanicJobPlugin() {
        addDependency(new KClassImpl<>(DatabasePlugin.class));
        addDependency(new KClassImpl<>(JobPlugin.class));
    }

    @Override
    protected void onEnable()  {
        this.repairSessionMap = new HashMap<>();
        logger = getLogger();
        eventManager = getEventManager().createChildNode();
    }


    private void addCommands() {
        playerCommandManager = new PlayerCommandManager(eventManager);
        CommandGroup group = new CommandGroup();
        group.registerCommands(new MechanicAcceptCommands(eventManager));
        playerCommandManager.registerCommands(new MechanicCommands(eventManager));
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        playerCommandManager.registerChildGroup(group, "accept");
    }

    private void addEventHandlers() {
        eventManager.registerHandler(VehicleEngineStartEvent.class, e -> {
            Optional<AbstractRepairSession> optionlSession = repairSessionMap.values().stream().filter(s -> s.getVehicle().equals(e.getVehicle())).findFirst();
            if(optionlSession.isPresent()) {
                e.getPlayer().sendErrorMessage("Mechanikas dirba prie ðios transporto priemonës, niekur vaþiuoti negalite!");
                e.deny();
            }
        });

        eventManager.registerHandler(RepairSessionEndEvent.class, e -> {
            repairSessionMap.remove(e.getPlayer());
        });

        eventManager.registerHandler(PlayerLeaveRepairArea.class, e -> {
            e.getPlayer().sendErrorMessage("Pasitraukëte per toli nuo taisomo automobilio! Laikas sustabdytas, gráþkite per 15 sekundþiø arba taisymas bus nutrauktas.");
        });
    }

    public void addRepairSession(AbstractRepairSession session) {
        repairSessionMap.put(session.getPlayer(), session);
    }

    public boolean isVehicleInSession(LtrpVehicle vehicle) {
        return repairSessionMap.values().stream().filter(s -> s.getVehicle().equals(vehicle)).findFirst().isPresent();
    }

    public boolean isPlayerInSession(LtrpPlayer player) {
        return repairSessionMap.containsKey(player);
    }


    public MechanicJob getJob() {
        return mechanicJob;
    }


    @Override
    protected void onDisable() {
        super.onDisable();
        eventManager.cancelAll();
        playerCommandManager.uninstallAllHandlers();
        repairSessionMap.values().forEach(AbstractRepairSession::destroy);
        repairSessionMap.clear();
        repairSessionMap = null;
        logger.info(getDescription().getName() + " unloaded");
    }

    @Override
    public void onDependenciesLoaded() {
        mechanicDao = new MySqlMechanicJobDaoImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), null, eventManager);
        mechanicJob = new MechanicJobImpl(JobPlugin.JobId.Mechanic.id, eventManager);

        addEventHandlers();
        addCommands();

        logger.info(getDescription().getName() + " loaded");
    }
}
