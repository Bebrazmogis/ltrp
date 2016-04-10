package lt.ltrp.dialog;


import lt.ltrp.BankController;
import lt.ltrp.constant.Currency;
import lt.ltrp.event.BankTakeDepositEvent;
import lt.ltrp.player.BankAccount;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankAccountDialog {

    public static AbstractDialog create(LtrpPlayer player, EventManager eventManager, BankController bankManager) {
        BankAccount playerBankAccount = bankManager.getAccount(player);
        // If the user does NOT have a bank account
        if(playerBankAccount == null) {
            return CreateBankAccountMsgDialog.create(player, eventManager, bankManager);
        } else {
            return ListDialog.create(player, eventManager)
                    .caption("Los Santos Bankas")
                    .item("Dabartinis balansas: " + Currency.SYMBOL + playerBankAccount.getMoney(), i -> i.getCurrentDialog().show())
                    .item("Sàskaitos nr. " + playerBankAccount.getNumber(), i -> i.getCurrentDialog().show())
                    .item("Nuimti pinigø", () -> playerBankAccount.getDeposit() == 0, i -> {
                        WithdrawMoneyInputDialog.create(player, eventManager, playerBankAccount)
                                .show();
                    })

                    .item("Padëti pinigø", () -> playerBankAccount.getDeposit() == 0, i -> {
                        DepositMoneyInputDialog.create(player, eventManager, playerBankAccount)
                                .show();
                    })
                    .item("Pervesti pinigø á kità sàskaità", () -> playerBankAccount.getDeposit() == 0 && playerBankAccount.getMoney() > 0, i -> {
                        BankWireInputDialog.create(player, eventManager, playerBankAccount)
                                .show();
                    })
                    .item(String.format("Paimti indëlá(%d)", playerBankAccount.getDeposit()), () -> playerBankAccount.getDeposit() != 0, i -> {
                        int deposit = playerBankAccount.getDeposit();
                        playerBankAccount.takeDeposit();
                        eventManager.dispatchEvent(new BankTakeDepositEvent(player, playerBankAccount, deposit));
                    })
                    .item(String.format("Padëti indëlá(%d%%", bankManager.getInterest()),
                            () -> playerBankAccount.getDeposit() == 0,
                            i -> {
                                BankNewDepositDialog.create(player, eventManager, bankManager, playerBankAccount)
                                        .show();
                            })
                    .build();
        }
    }

}
