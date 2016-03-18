package lt.ltrp.dmv.dialog;


import lt.ltrp.dmv.aircraft.FlyingTest;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class FlyingTestEndMsgDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, FlyingTest flyingTest) {
        String body = String.format("%s\n\nTestas %s" +
                        "\n\nTesto suvestinë:\nLaikantysis: %s" +
                        "\nKursas uþbaigtas: %s" +
                        "\nMinimalus aukðtis: %.2f",
                flyingTest.getDmv().getName(),
                flyingTest.isPassed() ? "{1EE701}iðlaiktas" : "{F93960}neiðlaikytas",
                player.getCharName(),
                flyingTest.isPassed() ? "{1EE701}Taip" : "{F93960}Ne",
                flyingTest.getMinZ()

        );

        return MsgboxDialog.create(player, eventManager)
                .caption(flyingTest.getDmv().getName())
                .message(body)
                .buttonOk("Gerai")
                .build();
    }

}
