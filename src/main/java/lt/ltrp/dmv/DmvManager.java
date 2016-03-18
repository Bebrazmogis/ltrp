package lt.ltrp.dmv;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.dao.DmvDao;
import lt.ltrp.data.Color;
import lt.ltrp.dmv.aircraft.AircraftDmvManager;
import lt.ltrp.dmv.boat.BoatDmvManager;
import lt.ltrp.dmv.car.CarDmv;
import lt.ltrp.dmv.car.CarDmvManager;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.event.VehicleEngineKillEvent;
import lt.ltrp.vehicle.event.VehicleEngineStartEvent;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvManager implements Destroyable {

    private static final Logger logger = LoggerFactory.getLogger(DmvManager.class);

    private EventManagerNode eventManager;
    private PlayerCommandManager commandManager;
    private boolean destroyed;

    private Map<LtrpPlayer, DmvTest> playerTests;


    public DmvManager(EventManager eventManager1) {
        this.playerTests = new HashMap<>();
        eventManager = eventManager1.createChildNode();

        DmvDao dmvDao = LtrpGamemode.getDao().getDmvDao();
        CarDmvManager carDmvManager = new CarDmvManager(eventManager);
        BoatDmvManager boatDmvManager = new BoatDmvManager(eventManager);
        AircraftDmvManager aircraftDmvManager = new AircraftDmvManager(eventManager);

        AbstractDmvManager[] dmvManagers = new AbstractDmvManager[]{ carDmvManager, boatDmvManager, aircraftDmvManager };


        eventManager.registerHandler(VehicleEngineStartEvent.class, e -> {
            for(AbstractDmvManager manager : dmvManagers) {
                // If the vehicle belongs to a DMV but there isn't a started test
                if(manager.getDmv().getVehicles().contains(e.getVehicle()) && !manager.isInTest(e.getVehicle())) {
                    e.deny();
                }
            }
        });

        eventManager.registerHandler(VehicleEngineKillEvent.class, e -> {
           for(AbstractDmvManager manager : dmvManagers) {
               // Vehicle engine cannot be turned off during a test
               if(manager.getDmv().getVehicles().contains(e.getVehicle()) && manager.isInTest(e.getVehicle())) {
                   e.getPlayer().sendMessage(Color.DMV, "Testo metu negalima gesinti variklio!");
                   e.deny();
               }
           }
        });

        commandManager = new PlayerCommandManager(eventManager);
        commandManager.installCommandHandler(HandlerPriority.BOTTOM);
        commandManager.registerCommand("takelesson", new Class[0], (player, params) -> {
            LtrpPlayer p = LtrpPlayer.get(player);
            if(p != null) {
                p.sendErrorMessage("Ðià komandà galite naudoti tik bûdami vairavimo mokyklos transporto priemonëje arba mokyklos patalpose.");
            }
            return true;
        }, null, null, null);

        eventManager.registerHandler(PlayerCommandEvent.class, e -> {
            String command = e.getCommand().toLowerCase();
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            System.out.println("PlayerCommadnEvent in DmvManager. Cmd: " + command + " Player: " +player);
            if(player == null) {
                return;
            }
            if(command.startsWith("/testdmv")) {

            }
        });


        eventManager.registerHandler(AmxLoadEvent.class, e -> {
            e.getAmxInstance().registerFunction("isDmvVehicle", params -> {
                LtrpVehicle vehicle = LtrpVehicle.getById((Integer)params[0]);
                if(vehicle != null) {
                    return isDmvVehicle(vehicle) ? 1 : 0;
                }
                return 0;
            }, Integer.class);
        });

        logger.info("Dmv manager initialized with " + dmvManagers.length + " dmvs");
    }


    protected EventManager getEventManager() {
        return eventManager;
    }

    public boolean isDmvVehicle(LtrpVehicle vehicle) {
        return vehicle instanceof DmvVehicle;
    }

    @Override
    public void destroy() {
        destroyed = true;
        eventManager.cancelAll();
        commandManager.destroy();
        playerTests.forEach((p, v) -> v.stop());
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
