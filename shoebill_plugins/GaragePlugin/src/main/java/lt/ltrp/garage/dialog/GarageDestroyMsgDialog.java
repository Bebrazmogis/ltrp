package lt.ltrp.garage.dialog;

import lt.ltrp.GarageController;
import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageDestroyMsgDialog {
    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, Garage garage) {
        return MsgboxDialog.create(player, eventManager)
                .caption("{FF1100}Gara�o naikinimas")
                .line("D�mesio! �io veiksmo atstatyti ne�manoma.")
                .line("\nGara�as bus pa�alintas negr��tamai.")
                .line("\nAr tikrai norite pa�alinti gara�� \"" + garage.getName() + "\"?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(d -> {
                    GarageController.get().getDao().remove(garage);
                    garage.destroy();
                })
                .build();
    }
}
