package lt.ltrp.dmv;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.dao.DmvDao;
import lt.ltrp.dmv.dialog.DrivingTestEndMsgDialog;
import lt.ltrp.dmv.event.PlayerDrivingTestEndEvent;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerLicense;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerRequestSpawnEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.object.Server;
import net.gtaun.shoebill.object.World;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

        carDmv = new CarDmv(1);
        dmvDao.getQuestionCheckpointDmv(carDmv);

        aircraftDmv = new AicraftDmv(2);
        dmvDao.getCheckpointDmv(aircraftDmv);

        boatDmv = new BoatDmv(3);
        dmvDao.getCheckpointDmv(boatDmv);

        dmvList = new Dmv[]{carDmv, aircraftDmv, boatDmv};


        eventManager.registerHandler(PlayerDrivingTestEndEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            DrivingTestEndMsgDialog.create(player, eventManager, e.getTest());

            if(e.getTest().isPassed()) {
                if(player.getLicenses().contains(LicenseType.Car) || player.getLicenses().contains(LicenseType.Motorcycle)) {
                    PlayerLicense license = null;
                    if(player.getLicenses().get(LicenseType.Car).getStage() == 1) {
                        license = player.getLicenses().get(LicenseType.Car);
                    } else {
                        license = player.getLicenses().get(LicenseType.Motorcycle);
                    }
                    license.setStage(2);
                    LtrpGamemode.getDao().getPlayerDao().updateLicense(license);
                } else {
                    PlayerLicense license = new PlayerLicense();
                    license.setType(LicenseType.Car);
                    license.setDateAquired(new Date());
                    license.setStage(1);
                    license.setPlayer(player);
                    LtrpGamemode.getDao().getPlayerDao().insertLicense(license);

                    license = new PlayerLicense();
                    license.setType(LicenseType.Motorcycle);
                    license.setDateAquired(new Date());
                    license.setStage(1);
                    license.setPlayer(player);
                    LtrpGamemode.getDao().getPlayerDao().insertLicense(license);
                }
            }
        });

        eventManager.registerHandler(PlayerStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());

            if(player != null && player.getState() == PlayerState.DRIVER && player.getVehicle() != null) {
                LtrpVehicle vehicle = player.getVehicle();
                for(Dmv dmv : dmvList) {
                    if(dmv instanceof CheckpointDmv) {
                        if(dmv.getVehicles().contains(vehicle)) {
                            CheckpointDmv checkpointDmv = (CheckpointDmv)dmv;
                            int price = checkpointDmv.getCheckpointTestPrice();
                            if(player.getMoney() >= price) {
                                // Maybe a dialog of some sort?
                                playerTests.put(player, checkpointDmv.startCheckpointTest(player, vehicle, eventManager));
                            } else {
                                player.sendErrorMessage("Jums neuþtenka pinigø. Testo kaina $" + price);
                                player.removeFromVehicle();
                            }
                        }
                    }
                }
            }
        });

        eventManager.registerHandler(PlayerCommandEvent.class, e -> {
            String command = e.getCommand().toLowerCase();
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player == null) {
                return;
            }

            if(command.startsWith("/takelesson")) {
                for(Dmv d : DmvManager.getInstance().getDmvs()) {
                    if(d instanceof QuestionDmv && d.getLocation().distance(player.getLocation()) < 7.0) {
                        QuestionDmv checkpointDmv = (QuestionDmv)d;
                        int price = checkpointDmv.getQuestionTestPrice();
                        if(player.getMoney() >= price) {
                            // Maybe a dialog of some sort?
                            playerTests.put(player, checkpointDmv.startQuestionTest(player, eventManager));
                        } else {
                            player.sendErrorMessage("Jums neuþtenka pinigø. Testo kaina $" + price);
                            player.removeFromVehicle();
                        }
                    }
                }
            }
        });



        logger.info("Dmv manager initialized with " + dmvList.length + " dmvs");
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

    public QuestionCheckpointDmv getCarDmv() {
        return carDmv;
    }

    protected EventManager getEventManager() {
        return eventManager;
    }
}
