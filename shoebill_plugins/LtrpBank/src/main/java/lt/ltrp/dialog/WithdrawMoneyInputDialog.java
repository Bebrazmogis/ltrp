package lt.ltrp.dialog;

import lt.ltrp.common.constant.Currency;
import lt.ltrp.common.dialog.IntegerInputDialog;
import lt.ltrp.event.BankAccountWithdrawMoney;
import lt.ltrp.player.BankAccount;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class WithdrawMoneyInputDialog {

    public static InputDialog create(LtrpPlayer player, EventManager eventManager, BankAccount account) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Pinigø iðëmimo operacija")
                .message("Sàskaita: " + account.getNumber()
                        + "\nSàskaitos likutis: " + Currency.SYMBOL + account.getMoney()
                        + "\n\nÁveskite sumà kurià norite paimti")
                .buttonOk("Paimti")
                .buttonCancel("Atgal")
                .onClickOk((d, amount) -> {
                    if (amount > account.getMoney()) {
                        d.show();
                        player.sendErrorMessage("Jûsø sàskaitoje tiek pinigø nëra.");
                    } else {
                        player.giveMoney(amount);
                        account.addMoney(-amount);
                        eventManager.dispatchEvent(new BankAccountWithdrawMoney(player, account, amount));
                    }
                })
                .build();
    }
}
