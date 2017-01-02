package lt.ltrp.dialog;

import lt.ltrp.player.fine.data.PlayerFine;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class CrimeDialog {


    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, PlayerFine crime, AbstractDialog parent) {
        return MsgboxDialog.create(player, eventManager)
                .caption(() -> String.format(crime.getPlayer().getName() + " nusikaltimas"))
                .parentDialog(parent)
                .message((d) -> String.format("Data: %s\nPaþeidëjas: %s\n\n%s",
                        crime.getCreatedAt(),
                        crime.getPlayer().getName(),
                        crime.getDescription().replaceAll("(.{100})", "$1\n")))
                .buttonOk("Uþdaryti")
                .buttonCancel("Atgal")
                .onClickCancel(d -> d.getParentDialog().show())

                .build();
    }

}
