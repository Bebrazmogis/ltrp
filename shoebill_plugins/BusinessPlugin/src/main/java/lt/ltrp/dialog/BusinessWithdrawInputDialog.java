package lt.ltrp.dialog;

import lt.ltrp.dialog.IntegerInputDialog;
import lt.ltrp.event.property.BusinessBankChangeEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessWithdrawInputDialog {

    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, Business business) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Pinigø paëmimas ið verslo banko")
                .buttonOk("Paimti")
                .buttonCancel("Atgal")
                .message("Áveskite sumà, kurià norite paimti")
                .onClickOk((d, i) -> {
                    if (i < 0 || i > business.getMoney())
                        player.sendErrorMessage("Tiek pinigø nëra!");
                    else {
                        player.giveMoney(i);
                        int oldMoney = business.getMoney();
                        business.addMoney(-i);
                        eventManager.dispatchEvent(new BusinessBankChangeEvent(business, player, business.getMoney(), oldMoney));
                    }
                })
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }

}
