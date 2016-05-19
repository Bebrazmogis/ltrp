package lt.ltrp.aircraft;

import lt.ltrp.AbstractDmvManager;
import lt.ltrp.DmvController;
import lt.ltrp.InitException;
import lt.ltrp.LoadingException;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerLicense;
import lt.ltrp.dialog.FlyingTestEndMsgDialog;
import lt.ltrp.event.PlayerFlyingTestEnd;
import lt.ltrp.object.*;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

import java.sql.Timestamp;
import java.util.Date;
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
                                    .message("Sveiki atvyk� � " + dmv.getName() +
                                        "\n\nMes si�lome egzaminus norintiems i�mokti pilotuoti oro transporta." +
                                        "\nVieno egzamino bandymo kaina $" + dmv.getCheckpointTestPrice() +
                                        "\n\nAr norite prad�ti egzamin�?")
                                    .onClickOk(d -> {
                                        p.giveMoney(-dmv.getCheckpointTestPrice());
                                        ongoingPlayerFlyingTestMap.put(p, FlyingTest.create(p, vehicle, dmv, getEventManagerNode()));
                                        p.sendMessage(Color.DMV, "Egzaminas prasideda. Neskriskite per �emai ir nenukrypkite nuo kurso, s�km�s!");
                                    })
                                    .onClickCancel(d -> p.removeFromVehicle())
                                    .build()
                                    .show();
                        } else {
                            p.sendErrorMessage("Jums neu�tenka pinig�. Aviacijos testo kaina $" + dmv.getCheckpointTestPrice());
                            p.removeFromVehicle();
                        }
                    } else {
                        p.sendErrorMessage("J�s jau turite aviacijos licenzij�!");
                        p.removeFromVehicle();
                    }
                    return true;
                }
            }
            return false;
        }, null, null, null, null);

        getEventManagerNode().registerHandler(PlayerFlyingTestEnd.class, e -> {
            if(e.getTest().isPassed()) {
                PlayerLicense license = new PlayerLicense();
                license.setType(LicenseType.Aircraft);
                license.setPlayer(e.getPlayer());
                license.setDateAquired(new Timestamp(new Date().getTime()));
                license.setStage(1);
                LtrpPlayer.getPlayerDao().insertLicense(license);
                e.getPlayer().getLicenses().add(license);
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
