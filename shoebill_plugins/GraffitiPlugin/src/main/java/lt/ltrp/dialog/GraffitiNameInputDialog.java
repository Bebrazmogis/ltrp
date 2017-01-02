package lt.ltrp.dialog;

import lt.ltrp.event.GraffitiEditEvent;
import lt.ltrp.object.Graffiti;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiNameInputDialog {


    public static InputDialog create(LtrpPlayer player, EventManager eventManager, Graffiti graffiti) {
        return InputDialog.create(player, eventManager)
                .caption("Grafiti teksto keitimas")
                .line("Dabartinis grafiti tekstas:" + graffiti.getText())
                .line("�veskite nauj� grafiti tekst�, kur� sudaryt� nema�iau nei 1 simbolis ir nedaugiau nei " + Graffiti.MAX_NAME_LENGTH)
                .buttonOk("Keisti")
                .buttonCancel("U�daryti")
                .onClickOk((d, n) -> {
                    String name = StringUtils.stripColors(n);
                    graffiti.setText(name);
                    player.sendMessage(graffiti.getColor().getColor(), "Tekstas pakeistas � " + name);
                    eventManager.dispatchEvent(new GraffitiEditEvent(graffiti, player));

                })
                .build();
    }
}
