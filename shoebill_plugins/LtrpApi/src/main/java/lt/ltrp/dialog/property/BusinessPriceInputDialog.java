package lt.ltrp.dialog.property;

import lt.ltrp.constant.Currency;
import lt.ltrp.dialog.IntegerInputDialog;
import lt.ltrp.event.property.BusinessEditEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.27.
 */
public class BusinessPriceInputDialog {

    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parentDialog, Business b) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Verslo kainos keitimas")
                .message("Áveskite naujà verslo kainà " +
                        "\n\nMinimali kaina: 1" +
                        "\nDabartinë kaina " + b.getPrice() + Currency.SYMBOL)
                .parentDialog(parentDialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onInputError((d, s) -> d.show())
                .onClickOk((d, i) -> {
                    if (i < 0)
                        d.show();
                    else {
                        b.setPrice(i);
                        eventManager.dispatchEvent(new BusinessEditEvent(b, player));
                    }
                })
                .build();
    }

}
