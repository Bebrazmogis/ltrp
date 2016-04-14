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
                .caption("Pinig� �ne�imo operacija")
                .message("S�skaita: " + account.getNumber()
                        + "\nS�skaitos likutis: " + Currency.SYMBOL + account.getMoney()
                        + "\n\n�veskite sum� kuri� norite i�imti")
                .buttonOk("Pad�ti")
                .buttonCancel("Atgal")
                .onClickOk((d, amount) -> {
                    if (amount > player.getMoney()) {
                        d.show();
                        player.sendErrorMessage("J�s neturite tiek pinig�.");
                    } else {
                        player.giveMoney(-amount);
                        account.addMoney(amount);
                        eventManager.dispatchEvent(new BankAccountDepositMoney(player, account, amount));
                    }
                })
                .build();
    }

}
