package lt.ltrp.dmv.dialog;


import lt.ltrp.dmv.DrivingTest;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.26.
 */
public class DrivingTestEndMsgDialog  {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, DrivingTest drivingTest) {
        String body = String.format("%s\n\n{FFFFFF}Testas %s" +
                "\n\n{FFFFFF}Testo suvestin�:\nLaikantysis: %s" +
                "\n{FFFFFF}Kursas u�baigtas: %s" +
                "\n{FFFFFF}Maksimalus greitis: %s" +
                "\n{FFFFFF}Saugos dir�ai: %s" +
                "\n{FFFFFF}�viesos: %s",
                drivingTest.getDmv().getName(),
                drivingTest.isPassed() ? "{1EE701}i�laikytas" : "{F93960}nei�laikytas",
                player.getCharName(),
                drivingTest.isFinished() ? "{1EE701}Taip" : "{F93960}Ne",
                drivingTest.getMaxSpeed() < drivingTest.getMaxAllowedSpeed() ? "{1EE701}"+drivingTest.getMaxSpeed() : "{F93960}" + drivingTest.getMaxSpeed(),
                drivingTest.isSeatbelt() ? "{1EE701}Taip" : "{F93960}Ne",
                drivingTest.getLights() == VehicleParam.PARAM_ON ? "{1EE701}Taip" : "{F93960}Ne"
                );

        return MsgboxDialog.create(player, eventManager)
                .caption(drivingTest.getDmv().getName())
                .message(body)
                .buttonOk("Gerai")
                .build();
    }


}
