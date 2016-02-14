package lt.ltrp.dmv;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.PlayerCommandManager;
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
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvManager {

    private static final Logger logger = LoggerFactory.getLogger(DmvManager.class);
    private static final DmvManager instance = new DmvManager();

    public static DmvManager getInstance() {
        return instance;
    }


    private EventManager eventManager;
    private PlayerCommandManager commandManager;
    private Dmv[] dmvList;

    private CarDmv carDmv;
    private CheckpointDmv aircraftDmv;
    private CheckpointDmv boatDmv;

    private Map<LtrpPlayer, DmvTest> playerTests;


    private DmvManager() {
        this.playerTests = new HashMap<>();
        eventManager = LtrpGamemode.get().getEventManager().createChildNode();

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

        commandManager = new PlayerCommandManager(HandlerPriority.BOTTOM, eventManager);
        commandManager.registerCommand("takelesson", new Class[0], new String[0], (p, params) -> {
            p.sendErrorMessage("Ðià komandà galite naudoti tik bûdami vairavimo mokyklos transporto priemonëje arba mokyklos patalpose.");
            return true;
        });

        eventManager.registerHandler(PlayerCommandEvent.class, e -> {
            String command = e.getCommand().toLowerCase();
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            System.out.println("PlayerCommadnEvent in DmvManager. Cmd: " + command + " Player: " +player);
            if(player == null) {
                return;
            }
            if(command.startsWith("/testdmv")) {
            player.sendMessage(Color.DARKOLIVEGREEN, "Dmv count: " + dmvList.length);
            for(Dmv dmv : dmvList) {
                player.sendMessage(Color.DARKOLIVEGREEN, String.format("Id:%d Name: %s location:%s",
                        dmv.getId(), dmv.getName(), dmv.getLocation()));
                if(dmv.getVehicles() != null) {
                    player.sendMessage(Color.DARKOLIVEGREEN, "Vehicle count: "+ dmv.getVehicles().size());
                } else {
                    player.sendMessage(Color.DARKOLIVEGREEN, "Dmv doesn't have a vehicle list.");
                }/*
                if(dmv instanceof CheckpointDmv) {
                    CheckpointDmv d = (CheckpointDmv)dmv;
                    if(d.getCheckpoints() != null) {
                        player.sendMessage(Color.DARKOLIVEGREEN, "Dmv has " + d.getCheckpoints().size() + " checkpoints. First: " + d.getCheckpoints().get(0) + " Last:" + d.getCheckpoints().get(d.getCheckpoints().size() -1));
                    } else {
                        player.sendMessage(Color.DARKOLIVEGREEN, "Dmv doesn't have any checkpoints.");
                    }
                }*/
                if(dmv instanceof QuestionDmv) {
                    QuestionDmv d = (QuestionDmv)dmv;
                    if(d.getQuestions() != null) {
                        player.sendMessage(Color.DARKOLIVEGREEN, "Dmv has " + d.getQuestions().size() + " questions.");
                    } else {
                        player.sendMessage(Color.DARKOLIVEGREEN, "Dmv doesn't have any questions.");
                    }
                }
                player.sendMessage(Color.WHITE,"-------------------------------------------------------------------------------------------------------------------");
            }
            }

        });



        logger.info("Dmv manager initialized with " + dmvList.length + " dmvs");
    }

    private boolean isDmvVehicle(LtrpVehicle vehicle) {
        for(Dmv dmv : dmvList) {
            if(dmv.getVehicles().contains(vehicle)) {
                return true;
            }
        }
        return false;
    }

    private Dmv getDmvByVehicle(LtrpVehicle vehicle) {
        for(Dmv dmv : dmvList) {
            if(dmv.getVehicles().contains(vehicle)) {
                return dmv;
            }
        }
        return null;
    }


    public Dmv[] getDmvs() {
        return dmvList;
    }

    public CheckpointDmv getBoatDmv() {
        return boatDmv;
    }

    public CheckpointDmv getAircraftDmv() {
        return aircraftDmv;
    }

    public CarDmv getCarDmv() {
        return carDmv;
    }

    protected EventManager getEventManager() {
        return eventManager;
    }
}
