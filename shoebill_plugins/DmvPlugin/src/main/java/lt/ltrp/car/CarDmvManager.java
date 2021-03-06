package lt.ltrp.car;

import lt.ltrp.*;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.DrivingTestEndMsgDialog;
import lt.ltrp.dialog.QuestionTestEndMsgDialog;
import lt.ltrp.PlayerDrivingTestEndEvent;
import lt.ltrp.PlayerQuestionTestEndEvent;
import lt.ltrp.object.*;
import lt.ltrp.player.licenses.PlayerLicenseController;
import lt.ltrp.player.licenses.constant.LicenseType;
import lt.ltrp.player.licenses.data.PlayerLicense;
import lt.ltrp.player.licenses.data.PlayerLicenses;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Bebras
 *         2016.02.10.
 */
public class CarDmvManager extends AbstractDmvManager {

    private CarDmv dmv;
    private Map<LtrpPlayer, DmvTest> ongoingTests;

    public CarDmvManager(EventManager eventManager) {
        super(eventManager);
        this.ongoingTests = new HashMap<>();

        try {
            dmv = DmvController.get().getDao().getCarDmv(1);
        } catch(LoadingException e) {
            throw new InitException("CarDmvManager could not be initialized", e);
        }


        getEventManagerNode().registerHandler(PlayerDrivingTestEndEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            if (e.getTest().isPassed()) {
                if (player.getLicenses().contains(LicenseType.Car) || player.getLicenses().contains(LicenseType.Motorcycle)) {
                    PlayerLicense license = null;
                    if (player.getLicenses().get(LicenseType.Car).getStage() == 1) {
                        license = player.getLicenses().get(LicenseType.Car);
                    } else {
                        license = player.getLicenses().get(LicenseType.Motorcycle);
                    }
                    license.setStage(2);
                    license.setDateAquired(LocalDateTime.now());
                    PlayerLicenseController.instance.update(license);
                }
            }
            DrivingTestEndMsgDialog.create(player, eventManager, e.getTest()).show();
            ongoingTests.remove(player);
        });

        getEventManagerNode().registerHandler(PlayerQuestionTestEndEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            if (e.getDmv().equals(dmv)) {
                if (e.getTest().isPassed()) {
                    PlayerLicenseController.instance.insert(p, LicenseType.Car, 1);
                    PlayerLicenseController.instance.insert(p, LicenseType.Motorcycle, 1);
                    p.sendMessage(Color.NEWS, "Dabar galite laikyti praktikos egzamin� su lengvuoju automobiliu/motociklu.");
                }
            }
            ongoingTests.remove(p);
            QuestionTestEndMsgDialog.create(p, getEventManagerNode(), e.getTest()).show();
        });

        getPlayerCommandManager().registerCommand("takelesson", new Class[0], (player, params) -> {
            LtrpPlayer p = LtrpPlayer.get(player);
            LtrpVehicle vehicle = LtrpVehicle.getByVehicle(p.getVehicle());
            if (p.getLocation().distance(dmv.getLocation()) < 10f) {
                if (!p.getLicenses().contains(LicenseType.Car)) {
                    if (p.getMoney() >= dmv.getQuestionTestPrice()) {
                        MsgboxDialog.create(p, getEventManagerNode())
                                .caption(dmv.getName() + " : teorijos testas")
                                .buttonOk("Prad�ti")
                                .buttonCancel("I�eiti")
                                .message(dmv.getName() +
                                        "\n\nTesto kaina: $" + dmv.getQuestionTestPrice() +
                                        "\nKlausim� skai�ius: " + dmv.getQuestions().size())
                                .onClickOk(d -> {
                                    // Start the theory theory test
                                    p.giveMoney(-dmv.getQuestionTestPrice());
                                    QuestionTest test = dmv.startQuestionTest(p, getEventManagerNode());
                                    ongoingTests.put(p, test);
                                })
                                .build()
                                .show();
                    } else {
                        p.sendErrorMessage("Jums neu�tenka pinig� testui, testas kainuoja " + dmv.getQuestionTestPrice() + "!");
                    }
                } else {
                    p.sendErrorMessage("J�s jau esat� i�laik�s �� test�, dabar galite laikyti praktikos egzamin�.");
                }
                return true;
            } else if (vehicle != null && dmv.getVehicles().contains(vehicle)) {
                PlayerLicenses licenses = p.getLicenses();
                System.out.println(String.format("IsBike:%b contais Motorcycle:%b moto stage:%d contains car:%b car stage:%d",
                        LtrpVehicleModel.isBike(vehicle.getModelId()),
                        licenses.contains(LicenseType.Motorcycle),
                        (licenses.contains(LicenseType.Motorcycle) ? licenses.get(LicenseType.Motorcycle).getStage() : -1),
                        licenses.contains(LicenseType.Car),
                        (licenses.contains(LicenseType.Car) ? licenses.get(LicenseType.Car).getStage() : -1)
                ));
                // To be able to take the drivin test a player must have vehicle/motorcycle license stage 1.
                if ((LtrpVehicleModel.isBike(vehicle.getModelId()) && licenses.contains(LicenseType.Motorcycle) && licenses.get(LicenseType.Motorcycle).getStage() == 1)
                        || (licenses.contains(LicenseType.Car) && licenses.get(LicenseType.Car).getStage() == 1)) {
                    if (p.getMoney() >= dmv.getDrivingTestPrice()) {
                        MsgboxDialog.create(p, getEventManagerNode())
                                .caption("Vairavimo testas")
                                .buttonOk("Taip")
                                .buttonCancel("Ne")
                                .message(dmv.getName() +
                                        "\n\nTesto kaina: $" + dmv.getDrivingTestPrice() +
                                        "\n\nAr norite prad�ti testo laikym�?")
                                .onClickOk(d -> {
                                    // Start the driving test
                                    p.giveMoney(-dmv.getCheckpointTestPrice());
                                    AbstractCheckpointTest test = dmv.startCheckpointTest(p, vehicle, getEventManagerNode());
                                    ongoingTests.put(p, test);
                                })
                                .onClickCancel(d -> {
                                    p.removeFromVehicle();
                                })
                                .build()
                                .show();
                    } else {
                        p.sendErrorMessage("Jums neu�tenka pinig� testui, testas kainuoja " + dmv.getDrivingTestPrice() + "!");
                        p.removeFromVehicle();
                    }
                } else {
                    p.sendErrorMessage("J�s jau turite �i� licenzij� arba neesate i�laik�s teorijos testo!");
                    p.removeFromVehicle();
                }
                return true;
            }
            return false;
        }, null, null, null, null);


    }

    @Override
    public boolean isInTest(LtrpPlayer player) {
        return ongoingTests.containsKey(player);
    }

    @Override
    public boolean isInTest(LtrpVehicle vehicle) {
        return ongoingTests.values().stream().filter(t -> t instanceof DrivingTest && ((DrivingTest) t).getVehicle().equals(vehicle)).findFirst().isPresent();
    }

    @Override
    public Dmv getDmv() {
        return dmv;
    }


    @Override
    public void destroy() {
        super.destroy();
        // commandManager destroy
        for(DmvTest test : ongoingTests.values()) {
            test.stop();
        }
        ongoingTests.clear();
    }

}
