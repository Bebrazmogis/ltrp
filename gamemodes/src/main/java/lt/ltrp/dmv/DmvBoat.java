package lt.ltrp.dmv;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerLicense;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.checkpoint.CheckpointEnterEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.util.event.EventManager;

import java.util.*;

/**
 * @author Bebras
 *         2015.12.17.
 */
public class DmvBoat implements Dmv {

    private int id;
    private Location location;
    private String name;
    private List<LtrpVehicle> vehicles;
    private EventManager eventManager;
    private List<DmvCheckpoint> checkpoints;
    private Map<LtrpPlayer, DrivingTestSession> playerTestSessions;
    private float minZ;
    private int[] prices;

    public DmvBoat() {
       // this.eventManager = DmvManager.getInstance().getEventManager().createChildNode();
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

    private void onFinishTest(LtrpPlayer player) {
        LtrpVehicle vehicle = player.getVehicle();
        DrivingTestSession session = playerTestSessions.get(player);
        boolean passed = true;
        if(vehicle == null) {
            passed = false;
        } else if(!vehicle.equals(session.vehicle)) {
            passed = false;
        } else if(vehicle.getHealth() < 1000.0f) {
            passed = false;
        } else if(session.cp < checkpoints.size()) {
            passed = false;
        }
        player.removeFromVehicle();
        if(vehicle != null)
            vehicle.respawn();

        MsgboxDialog.create(player, eventManager)
                .caption(getName())
                .message(getName() + "" +
                        "\n\n{FFFFFF}" + (passed ? "Testà iðlaikëte!" : "Testo neiðlaikëte!") +
                        "\n\nTesto suvestinë: " +
                        "\nKursas uþbaigtas: " + (session.cp == checkpoints.size() ? "{1BD61F}Taip" : "{FF464A}Ne") +
                        "\nTransporto priemonës bûklë: " + (vehicle != null && vehicle.getHealth() == 1000f ? "{1BD61F}Tinkama" : "{FF464A}Netinkama"))
                .buttonOk("Gerai")
                .build()
                .show();

        if(passed) {
            PlayerLicense license = new PlayerLicense();
            license.setPlayer(player);
            license.setStage(1);
            license.setDateAquired(new Date());
            license.setType(LicenseType.Ship);
            player.getLicenses().add(license);
            LtrpGamemode.getDao().getPlayerDao().insertLicense(license);
        }
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
        int price = getStagePrice(0);
        LtrpVehicle vehicle = player.getVehicle();
        if(vehicle != null) {
            if(vehicles.contains(vehicle)) {
                MsgboxDialog.create(player, eventManager)
                        .caption(getName())
                        .message(getName() + "" +
                                "\n\n{FFFFFF}Egzamino bandymo kaina $" + price + "" +
                                "\nAr norite tæsti?")
                        .buttonCancel("Ne")
                        .buttonOk("Taip")
                        .onClickOk(dialog -> {
                            if(player.getMoney() >= price) {
                                DrivingTestSession session = new DrivingTestSession();
                                session.vehicle = vehicle;
                                session.cp = 0;
                                player.setCheckpoint(checkpoints.get(0).getCheckpoint());
                                player.sendMessage(Color.DARKBLUE, "Uþveskite variklá ir plaukite á paþymëtà vietà.");
                                playerTestSessions.put(player, session);
                            } else {
                                player.sendErrorMessage("Jums neuþtenka pinigø!");
                                player.removeFromVehicle();
                            }
                        })
                        .build().show();
            } else
                player.sendErrorMessage("Jûs neesate " + getName() + " priklausanèiai transporto priemonëje.");
            player.removeFromVehicle();
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

    @Override
    public void addCheckpoint(DmvCheckpoint checkpoint) {
        checkpoints.add(checkpoint);
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

    private class DrivingTestSession {
        protected int cp;
        protected LtrpVehicle vehicle;

        public DrivingTestSession() {

        }
    }
}
