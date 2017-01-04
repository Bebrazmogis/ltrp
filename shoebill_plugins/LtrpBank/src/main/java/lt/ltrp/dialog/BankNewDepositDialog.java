package lt.ltrp.dialog;

import lt.ltrp.BankController;
import lt.ltrp.event.BankPlaceDepositEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.BankAccount;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankNewDepositDialog {

    protected static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, BankController bankController, BankAccount account) {
        return IntegerInputDialog.create(player, eventManager)
                .caption("Ind�lis")
                .message("Pasid�jus ind�l� negal�site atlikti kit� operacij� kol jo nei�imsite."
                        + "\nDabartin�s pal�kanos: " + bankController.getInterest()
                        + "\n�is pal�kan� dydis i�liks visu laikotarpiu."
                        + "\n\n�veskite sum� kuri� norite pad�ti")
                .buttonOk("T�sti")
                .buttonCancel("Atgal")
                .onClickOk((d, amount) -> {
                    if (amount > account.getMoney()) {
                        player.sendErrorMessage("J�s� s�skaitoje tiek pinig� n�ra!");
                    } else {
                        account.placeDeposit(amount, bankController.getInterest());
                        eventManager.dispatchEvent(new BankPlaceDepositEvent(player, account, amount, bankController.getInterest()));
                    }
                })
                .build();
    }

}
