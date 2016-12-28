package lt.ltrp.house.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.house.event.HouseEditEvent;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HousePriceInputDialog {

    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, House house) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Namo kainos keitimas")
                .message("Áveskite naujà namo kainà " +
                        "\n\nMinimali kaina: 1" +
                        "\nDabartinë kaina " + house.getPrice() + Currency.SYMBOL)
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onInputError((d, s) -> d.show())
                .onClickOk((d, i) -> {
                    if (i < 0)
                        d.show();
                    else {
                        house.setPrice(i);
                        eventManager.dispatchEvent(new HouseEditEvent(house, player));
                    }
                })
                .build();
    }
}
