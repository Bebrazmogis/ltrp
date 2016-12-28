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
                .caption("Verslo ��jimo kainos keitimas")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::show)
                .message("Dabartin� ��jimo kaina: " + business.getEntrancePrice() + Currency.SYMBOL + "\n" +
                        "Kaina turi b�ti didesn� u� 0, �vedus 0 ��jimas bus nemokamas.\n " +
                        "�veskite nauj� ��jimo kain�")
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
