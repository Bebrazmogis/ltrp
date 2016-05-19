package lt.ltrp.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.event.property.garage.GarageEditEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GaragePriceInputDialog {
    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, Garage garage) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Garaþo kainos keitimas")
                .message("Áveskite naujà gara=o kainà " +
                        "\n\nMinimali kaina: 1" +
                        "\nDabartinë kaina " + garage.getPrice() + Currency.SYMBOL)
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onInputError((d, s) -> d.show())
                .onClickOk((d, i) -> {
                    if (i < 0)
                        d.show();
                    else {
                        garage.setPrice(i);
                        eventManager.dispatchEvent(new GarageEditEvent(garage, player));
                    }
                })
                .build();
    }
}
