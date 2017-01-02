package lt.ltrp.aircraft;

import lt.ltrp.AbstractDmvManager;
import lt.ltrp.DmvController;
import lt.ltrp.InitException;
import lt.ltrp.LoadingException;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.FlyingTestEndMsgDialog;
import lt.ltrp.PlayerFlyingTestEnd;
import lt.ltrp.object.*;
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
public class AircraftDmvManager extends AbstractDmvManager {

    private Map<LtrpPlayer, FlyingTest> ongoingPlayerFlyingTestMap;
    private AircraftDmv dmv;

    public AircraftDmvManager(EventManager eventManager) {
        super(eventManager);
        this.ongoingPlayerFlyingTestMap = new HashMap<>();

        try {
            this.dmv = DmvController.get().getDao().getAircraftDmv(2);
        } catch(LoadingException e) {
            throw new InitException(getClass().getSimpleName() + " could not be initialized", e);
        }

        getPlayerCommandManager().registerCommand("takelesson", new Class[0], (player, params) -> {
            LtrpPlayer p = LtrpPlayer.get(player);
            if(p != null) {
                LtrpVehicle vehicle = LtrpVehicle.getByVehicle(p.getVehicle());
                if(vehicle != null && dmv.getVehicles().contains(vehicle)) {
                    if(!p.getLicenses().contains(LicenseType.Aircraft)) {
                        if(p.getMoney() >= dmv.getCheckpointTestPrice()) {
                            MsgboxDialog.create(p, getEventManagerNode())
                                    .caption(dmv.getName())
                                    .buttonOk("Taip")
                                    .buttonCancel("Ne")
                                    .message("Sveiki atvykæ á " + dmv.getName() +
                                        "\n\nMes siûlome egzaminus norintiems iðmokti pilotuoti oro transporta." +
                                        "\nVieno egzamino bandymo kaina $" + dmv.getCheckpointTestPrice() +
                                        "\n\nAr norite pradëti egzaminà?")
                                    .onClickOk(d -> {
                                        p.giveMoney(-dmv.getCheckpointTestPrice());
                                        ongoingPlayerFlyingTestMap.put(p, FlyingTest.create(p, vehicle, dmv, getEventManagerNode()));
                                        p.sendMessage(Color.DMV, "Egzaminas prasideda. Neskriskite per þemai ir nenukrypkite nuo kurso, sëkmës!");
                                    })
                                    .onClickCancel(d -> p.removeFromVehicle())
                                    .build()
                                    .show();
                        } else {
                            p.sendErrorMessage("Jums neuþtenka pinigø. Aviacijos testo kaina $" + dmv.getCheckpointTestPrice());
                            p.removeFromVehicle();
                        }
                    } else {
                        p.sendErrorMessage("Jûs jau turite aviacijos licenzijà!");
                        p.removeFromVehicle();
                    }
                    return true;
                }
            }
            return false;
        }, null, null, null, null);

        getEventManagerNode().registerHandler(PlayerFlyingTestEnd.class, e -> {
            if(e.getTest().isPassed()) {
                PlayerLicenseController.instance.insert(e.getPlayer(), LicenseType.Aircraft, 1);
            }
            FlyingTestEndMsgDialog.create(e.getPlayer(), getEventManagerNode(), e.getTest())
                    .show();
        });

    }

    @Override
    public boolean isInTest(LtrpPlayer player) {
        return ongoingPlayerFlyingTestMap.containsKey(player);
    }

    @Override
    public boolean isInTest(LtrpVehicle vehicle) {
        return ongoingPlayerFlyingTestMap.values().stream().filter(t -> t.getVehicle().equals(vehicle)).findFirst().isPresent();
    }

    @Override
    public Dmv getDmv() {
        return dmv;
    }

    @Override
    public void destroy() {
        super.destroy();
        ongoingPlayerFlyingTestMap.values().forEach(DmvTest::stop);
    }
}
