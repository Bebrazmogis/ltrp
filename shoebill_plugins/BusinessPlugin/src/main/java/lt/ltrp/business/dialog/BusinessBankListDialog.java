package lt.ltrp.business.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessBankListDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, Business business) {
        return ListDialog.create(player, eventManager)
                .caption("Verslo pinigø valdymas")
                .onClickCancel(AbstractDialog::showParentDialog)
                .item("Dabartinis balansas " + business.getMoney() + Currency.SYMBOL, i -> i.getCurrentDialog().show())
                .item("Paimti pinigø", i -> BusinessWithdrawInputDialog.create(player, eventManager, business).show())
                .item("Padëti pinigø", i -> BusinessDepositInputDialog.create(player, eventManager, business).show())
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .build();
    }

}
