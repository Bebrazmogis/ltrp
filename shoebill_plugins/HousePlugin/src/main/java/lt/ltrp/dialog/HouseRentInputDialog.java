package lt.ltrp.dialog;

import lt.ltrp.dialog.IntegerInputDialog;
import lt.ltrp.event.property.house.HouseEditEvent;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.17.
 */
public class HouseRentInputDialog {
    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parenetDialog, House house) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Namo nuomos kainos keitimas")
                .message("Dabartinë nuomos kaina " + house.getRentPrice())
                .line("Nustaèius nuomos kaina á ar þemesnæ, namas taps nenuomuojamas")
                .line("Áveskite naujà nuomos kainà")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .parentDialog(parenetDialog)
                .onClickOk((d, i) -> {
                    if (i < 0)
                        i = 0;
                    house.setRentPrice(i);
                    eventManager.dispatchEvent(new HouseEditEvent(house, player));
                    parenetDialog.show();
                })
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }
}
