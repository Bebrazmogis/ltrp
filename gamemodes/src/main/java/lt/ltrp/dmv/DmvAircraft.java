package lt.ltrp.dmv;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerLicense;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.event.SpeedometerTickEvent;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.checkpoint.CheckpointEnterEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.util.event.EventManager;

import java.util.*;

/**
 * @author Bebras
 *         2015.12.15.
 */
public class DmvAircraft implements Dmv {

    private int id;
    private Location location;
    private String name;
    private List<LtrpVehicle> vehicles;
    private EventManager eventManager;
    private List<DmvCheckpoint> checkpoints;
    private Map<LtrpPlayer, DrivingTestSession> playerTestSessions;
    private float minZ;
    private int[] prices;


    public DmvAircraft() {
        //this.eventManager = DmvManager.getInstance().getEventManager().createChildNode();
        this.eventManager = LtrpGamemode.get().getEventManager().createChildNode();
        this.vehicles = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
        this.playerTestSessions = new HashMap<>();


        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                if(playerTestSessions.containsKey(player)) {
                    LtrpVehicle vehicle = playerTestSessions.get(player).vehicle;
                    vehicle.respawn();
                    playerTestSessions.remove(player);
                }
            }
        });

        eventManager.registerHandler(VehicleDeathEvent.class, e -> {
            LtrpVehicle vehicle = LtrpVehicle.getById(e.getVehicle().getId());
            if(vehicle != null && vehicles.contains(vehicle)) {
                // find the player that is testing with that vehicle
                for(LtrpPlayer applicant : playerTestSessions.keySet()) {
                    DrivingTestSession session = playerTestSessions.get(applicant);
                    if(session.vehicle.equals(vehicle)) {
                        onFinishTest(applicant);
                        break;
                    }
                }
            }
        });

        eventManager.registerHandler(SpeedometerTickEvent.class, e -> {
            LtrpVehicle vehicle = e.getVehicle();
            LtrpPlayer player = e.getPlayer();
            // Check for z too low.

        });

        eventManager.registerHandler(CheckpointEnterEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null && playerTestSessions.containsKey(player)) {
                for(DmvCheckpoint dmvCheckpoint : checkpoints) {
                    if(dmvCheckpoint.equals(e.getCheckpoint())) {
                        DrivingTestSession session = playerTestSessions.get(player);
                        if(++session.cp == checkpoints.size()) {
                            onFinishTest(player);
                        } else {
                            player.setCheckpoint(checkpoints.get(session.cp).getCheckpoint());
                        }
                        break;
                    }
                }
            }
        });
    }

    public void addCheckpoint(DmvCheckpoint cp) {
        this.checkpoints.add(cp);
        minZ = findMinZ();
    }

    @Override
    public List<LtrpVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void setVehicles(List<LtrpVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public boolean isUserInTest(LtrpPlayer player) {
        return playerTestSessions.containsKey(player);
    }

    @Override
    public void setLocation(Location loc) {
        this.location = loc;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void startTest(LtrpPlayer player) {
        PlayerLicense playerLicense = player.getLicenses().get(LicenseType.Aircraft);
        if (playerLicense == null) {
            LtrpVehicle vehicle = player.getVehicle();
            if(vehicle != null && vehicles.contains(vehicle)) {
                MsgboxDialog.create(player, eventManager)
                        .caption(getName())
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .message(getName() + " {FFFFFF}" +
                                "\n\nAr norite bandyti laikyti oro transporto licenzijà?" +
                                "\n\nVieno bandymo kaina $" + getStagePrice(0))
                        .onClickCancel(dialog -> player.removeFromVehicle())
                        .onClickOk(dialog -> {
                            if(player.getMoney() >= getStagePrice(0)) {
                                DrivingTestSession session = new DrivingTestSession();
                                session.vehicle = vehicle;
                                session.cp = 0;
                                playerTestSessions.put(player, session);
                                player.sendMessage(Color.DARKVIOLET, "Testas prasidëjo. Skriskite á þymeklá.");
                                player.setCheckpoint(checkpoints.get(0).getCheckpoint());
                            } else {
                                player.sendErrorMessage("Jums neuþtenka pinigø!");
                                player.removeFromVehicle();
                            }
                        });
            }
        } else {
            player.sendErrorMessage("Jûs jau turite ðià licenzijà!");
            player.removeFromVehicle();
        }
    }

    private void onFinishTest(LtrpPlayer player) {
        DrivingTestSession session = playerTestSessions.get(player);
        boolean passed = true;
        if(session.vehicle.isDestroyed() || session.vehicle.getHealth() < 1000.0f) {
            passed  = false;
        } else if(session.minZ < getMinZ() - 30.0f) {
            passed = false;
        } // If for any reason the test was cancelled early
        else if(session.cp != checkpoints.size()) {
            passed = false;
        }

        MsgboxDialog.create(player, eventManager)
                .caption(getName())
                .buttonOk("Gerai")
                .message(getName() + "{FFFFFF}" +
                        "\n\n" + (passed ? "Egzaminà iðlaikëte!" : "Egzamino neiðlaikëte!") +
                        "\n\nSuvestinë: " +
                        "\nKursas uþbaigtas: " + (session.cp == checkpoints.size() ? "{1BD61F}Taip" : "{FF464A}Ne") +
                        "\nApgadinta tr. priemonë: " + (session.vehicle.getHealth() == 1000f ? "{1BD61F}Ne" : "{FF464A}Taip") +
                        "\nPavojingai þemas aukðtis:" + (session.minZ < getMinZ() - 30.0f ? "{FF464A}Taip" : "{1BD61F}Ne"))
                .build().show();

        if(passed) {
            PlayerLicense license = new PlayerLicense();
            license.setPlayer(player);
            license.setType(LicenseType.Aircraft);
            license.setDateAquired(new Date());
            license.setStage(1);
            player.getLicenses().add(license);
            LtrpGamemode.getDao().getPlayerDao().insertLicense(license);
        }

    }

    @Override
    public int getStagePrice(int stage) {
        if(prices == null || stage < 0 || stage > prices.length) {
            return 0;
        }
        return prices[stage];
    }

    @Override
    public void setStagePrice(int stage, int price) {
        if(stage >= prices.length) {
            this.prices = new int[stage+1];
        }
        prices[ stage ] = price;
    }

    @Override
    public String getName() {
        return name;
    }

    private float findMinZ() {
        float min = Float.MAX_VALUE;
        for(DmvCheckpoint cp : checkpoints) {
            float z = cp.getCheckpoint().getLocation().getZ();
            if(z < min) {
                min = z;
            }
        }
        return min;
    }

    private float getMinZ() {
        return minZ;
    }

    private class DrivingTestSession {
        protected int cp;
        protected LtrpVehicle vehicle;
        private  float minZ;

        public DrivingTestSession() {

        }
    }
}
