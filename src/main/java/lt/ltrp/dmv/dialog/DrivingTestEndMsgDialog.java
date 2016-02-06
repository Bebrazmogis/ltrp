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
        String body = String.format("%s\n\nTestas %s" +
                "\n\nTesto suvestin�:\nLaikantysis: %s" +
                "\nKursas u�baigtas: %s" +
                "\nMaksimalus greitis: %s" +
                "\nSaugos dir�ai: %s" +
                "\n�viesos: %s",
                drivingTest.getDmv().getName(),
                drivingTest.isPassed() ? "{1EE701}i�laiktas" : "{F93960}nei�laikytas",
                player.getCharName(),
                drivingTest.isPassed() ? "{1EE701}Taip" : "{F93960}Ne",
                drivingTest.getMaxSpeed(),
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
