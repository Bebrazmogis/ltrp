package lt.ltrp.dialog;

import lt.ltrp.boat.BoatingTest;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class BoatingTestEndMsgDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, BoatingTest drivingTest) {
        String body = String.format("%s\n\nTestas %s" +
                        "\n\nTesto suvestin�:\nLaikantysis: %s" +
                        "\nKursas u�baigtas: %s",
                drivingTest.getDmv().getName(),
                drivingTest.isPassed() ? "{1EE701}i�laiktas" : "{F93960}nei�laikytas",
                player.getCharName(),
                drivingTest.isPassed() ? "{1EE701}Taip" : "{F93960}Ne"
        );

        return MsgboxDialog.create(player, eventManager)
                .caption(drivingTest.getDmv().getName())
                .message(body)
                .buttonOk("Gerai")
                .build();
    }

}
