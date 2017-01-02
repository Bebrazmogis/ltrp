package lt.ltrp.boat;


import lt.ltrp.AbstractDmvManager;
import lt.ltrp.DmvController;
import lt.ltrp.InitException;
import lt.ltrp.LoadingException;
import lt.ltrp.dialog.BoatingTestEndMsgDialog;
import lt.ltrp.PlayerBoatingTestEnd;
import lt.ltrp.object.BoatDmv;
import lt.ltrp.object.Dmv;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.player.licenses.PlayerLicenseController;
import lt.ltrp.player.licenses.constant.LicenseType;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.02.14.
 */
public class BoatDmvManager extends AbstractDmvManager {

    private BoatDmv dmv;
    private Map<LtrpPlayer, BoatingTest> ongoingBoatingTests;

    public BoatDmvManager(EventManager eventManager) {
        super(eventManager);
        this.ongoingBoatingTests = new HashMap<>();

        try {
            dmv = DmvController.get().getDao().getBoatDmv(3);
        } catch(LoadingException e) {
            throw new InitException(getClass().getSimpleName() + " could not be initialized", e);
        }

        getPlayerCommandManager().registerCommand("takelesson", new Class[0], (player, params) -> {
            LtrpPlayer p = LtrpPlayer.get(player);
            if (p != null && p.isInAnyVehicle()) {
                LtrpVehicle vehicle = LtrpVehicle.getByVehicle(p.getVehicle());
                // If so, then its a dmv vehicle
                if (vehicle != null && dmv.getVehicles().contains(vehicle)) {
                    if (!p.getLicenses().contains(LicenseType.Ship)) {
                        if (dmv.getCheckpointTestPrice() < p.getMoney()) {
                            MsgboxDialog.create(p, getEventManagerNode())
                                    .caption(dmv.getName())
                                    .message("Sveiki atvykæ á " + dmv.getName() +
                                            "\n\nIðlaikæ laivybos egzaminà bûsite kvalifikuotas valdyti ávairaus dydþio ir paskirties vandens transporto priemones." +
                                            "\nEgzamino kaina $" + dmv.getCheckpointTestPrice() +
                                            "\n\nAr norite pradëti laikyti egzaminà?")
                                    .buttonOk("Taip")
                                    .buttonCancel("Ne")
                                    .onClickOk(d -> {
                                        p.giveMoney(-dmv.getCheckpointTestPrice());
                                        ongoingBoatingTests.put(p, BoatingTest.create(p, vehicle, dmv, getEventManagerNode()));
                                    })
                                    .onClickCancel(d -> p.removeFromVehicle())
                                    .build()
                                    .show();
                        } else {
                            p.sendErrorMessage("Jums neuþtenka pinigø, testo kaina $" + dmv.getCheckpointTestPrice());
                            p.removeFromVehicle();
                        }
                    } else {
                        p.sendErrorMessage("Jûs jau turite jûrininkystës licenzijà!");
                        p.removeFromVehicle();
                    }
                    return true;
                }
            }
            return false;
        }, null, null, null, null);

        getEventManagerNode().registerHandler(PlayerBoatingTestEnd.class, e -> {
            if(e.getTest().isPassed()) {
                PlayerLicenseController.instance.insert(e.getPlayer(), LicenseType.Ship, 1);
            }
            BoatingTestEndMsgDialog.create(e.getPlayer(), getEventManagerNode(), e.getTest())
                    .show();
        });
    }

    @Override
    public boolean isInTest(LtrpPlayer player) {
        return ongoingBoatingTests.containsKey(player);
    }

    @Override
    public boolean isInTest(LtrpVehicle vehicle) {
        return ongoingBoatingTests.values().stream().filter(t -> t.getVehicle().equals(vehicle)).findFirst().isPresent();
    }

    @Override
    public Dmv getDmv() {
        return dmv;
    }


}
