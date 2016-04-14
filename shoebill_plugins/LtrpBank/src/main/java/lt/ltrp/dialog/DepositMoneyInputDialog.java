package lt.ltrp.dialog;

import lt.ltrp.common.constant.Currency;
import lt.ltrp.common.dialog.IntegerInputDialog;
import lt.ltrp.event.BankAccountDepositMoney;
import lt.ltrp.player.BankAccount;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class DepositMoneyInputDialog {

    public static InputDialog create(LtrpPlayer player, EventManager eventManager, BankAccount account) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Pinigø áneðimo operacija")
                .message("Sàskaita: " + account.getNumber()
                        + "\nSàskaitos likutis: " + Currency.SYMBOL + account.getMoney()
                        + "\n\nÁveskite sumà kurià norite iðimti")
                .buttonOk("Padëti")
                .buttonCancel("Atgal")
                .onClickOk((d, amount) -> {
                    if (amount > player.getMoney()) {
                        d.show();
                        player.sendErrorMessage("Jûs neturite tiek pinigø.");
                    } else {
                        player.giveMoney(-amount);
                        account.addMoney(amount);
                        eventManager.dispatchEvent(new BankAccountDepositMoney(player, account, amount));
                    }
                })
                .build();
    }

}
