package lt.ltrp.business.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.dialog.IntegerInputDialog;
import lt.ltrp.event.property.BusinessEditEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessEntrancePriceInputDialog {

    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, Business business) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Verslo áëjimo kainos keitimas")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::show)
                .message("Dabartinë áëjimo kaina: " + business.getEntrancePrice() + Currency.SYMBOL + "\n" +
                        "Kaina turi bûti didesnë uþ 0, ávedus 0 áëjimas bus nemokamas.\n " +
                        "Áveskite naujà áëjimo kainà")
                .onClickOk((d, i) -> {
                    if (i < 0)
                        d.show();
                    else {
                        business.setEntrancePrice(i);
                        eventManager.dispatchEvent(new BusinessEditEvent(business, player));
                    }
                })
                .build();
    }

}
