package lt.ltrp;

import lt.ltrp.aircraft.AircraftDmvManager;
import lt.ltrp.boat.BoatDmvManager;
import lt.ltrp.car.CarDmvManager;
import lt.ltrp.dao.DmvDao;
import lt.ltrp.business.dao.impl.FileDmvDaoImpl;
import lt.ltrp.data.Color;
import lt.ltrp.event.DmvVehicleDestroyEvent;
import lt.ltrp.event.vehicle.VehicleEngineKillEvent;
import lt.ltrp.event.vehicle.VehicleEngineStartEvent;
import lt.ltrp.object.*;
import lt.ltrp.player.licenses.constant.LicenseType;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.vehicle.VehicleEnterEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvManager extends Plugin implements DmvController {

    private static final Logger logger = LoggerFactory.getLogger(DmvManager.class);

    private EventManagerNode eventManager;
    private PlayerCommandManager commandManager;
    private Collection<DmvVehicle> dmvVehicles;
    private DmvDao dmvDao;
    private Map<LtrpPlayer, DmvTest> playerTests;

    @Override
    protected void onEnable() throws Throwable {
        Instance.instance = this;
        this.playerTests = new HashMap<>();
        this.dmvVehicles = new ArrayList<>();
        eventManager = getEventManager().createChildNode();

        dmvDao = new FileDmvDaoImpl(getDataDir(), eventManager);
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

        eventManager.registerHandler(VehicleEnterEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            LtrpVehicle vehicle = LtrpVehicle.getByVehicle(e.getVehicle());
            if(player != null && vehicle != null) {
                VehicleModel.VehicleType type = VehicleModel.getType(vehicle.getModelId());
                // If it's an aicraft but the player isn't licensed to use one
                if (type == VehicleModel.VehicleType.AIRCRAFT && !player.getLicenses().contains(LicenseType.Aircraft)) {
                    Location location = player.getLocation();
                    location.setZ(location.getZ() + 1f);
                    player.setLocation(location);
                    player.sendErrorMessage("Jûs neturite teisës valdyti arba nemokate valdyti lëktuvo.");
                } else if(type == VehicleModel.VehicleType.BOAT && !player.getLicenses().contains(LicenseType.Ship)) {
                    Location location = player.getLocation();
                    location.setZ(location.getZ() + 1f);
                    player.setLocation(location);
                    player.sendErrorMessage("Jûs neturite teisës valdyti arba nemokate valdyti laivo.");
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
        }, null, null, null, null);

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

        eventManager.registerHandler(DmvVehicleDestroyEvent.class, e -> {
            dmvVehicles.remove(e.getVehicle());
        });

        logger.info("Dmv manager initialized with " + dmvManagers.length + " dmvs");
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        commandManager.destroy();
        playerTests.forEach((p, v) -> v.stop());
    }


    public boolean isDmvVehicle(LtrpVehicle vehicle) {
        return vehicle instanceof DmvVehicle;
    }


    @Override
    public DmvVehicle createVehicle(Dmv dmv, int i, int i1, AngledLocation angledLocation, int i2, int i3, float v, EventManager eventManager) {
        DmvVehicleImpl impl = new DmvVehicleImpl(dmv, i, i1, angledLocation, i2, i3, v, eventManager);
        dmvVehicles.add(impl);
        return impl;
    }

    @Override
    public DmvDao getDao() {
        return dmvDao;
    }
}
