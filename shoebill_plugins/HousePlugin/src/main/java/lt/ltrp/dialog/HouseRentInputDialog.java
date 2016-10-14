package lt.ltrp.dialog;

import lt.ltrp.house.event.HouseEditEvent;
import lt.ltrp.house.object.House;
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
                .message("Dabartin� nuomos kaina " + house.getRentPrice())
                .line("Nusta�ius nuomos kaina � ar �emesn�, namas taps nenuomuojamas")
                .line("�veskite nauj� nuomos kain�")
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
