package lt.ltrp.garage.dialog;

import lt.ltrp.player.PlayerController;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageInfoMessageDialog {
    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, Garage garage) {
        return MsgboxDialog.create(player, eventManager)
                .caption("Garaþo " + garage.getName() + " informacija")
                .line("ID:" + garage.getUUID())
                .line("Pavadinimas: " + garage.getName())
                .line("Kaina:" + garage.getPrice())
                .line("Savininkas:" + (garage.isOwned() ? PlayerController.get().getUsernameByUUID(garage.getOwner()) : "nëra"))
                .buttonOk("Atgal")
                .buttonCancel("")
                .onClickOk(d -> parentDialog.show())
                .build();
    }
}
