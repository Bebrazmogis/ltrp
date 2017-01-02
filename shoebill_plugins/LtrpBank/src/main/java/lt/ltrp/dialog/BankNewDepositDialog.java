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
                .caption("Indëlis")
                .message("Pasidëjus indëlá negalësite atlikti kitø operacijø kol jo neiðimsite."
                        + "\nDabartinës palûkanos: " + bankController.getInterest()
                        + "\nÐis palûkanø dydis iðliks visu laikotarpiu."
                        + "\n\nÁveskite sumà kurià norite padëti")
                .buttonOk("Tæsti")
                .buttonCancel("Atgal")
                .onClickOk((d, amount) -> {
                    if (amount > account.getMoney()) {
                        player.sendErrorMessage("Jûsø sàskaitoje tiek pinigø nëra!");
                    } else {
                        account.placeDeposit(amount, bankController.getInterest());
                        eventManager.dispatchEvent(new BankPlaceDepositEvent(player, account, amount, bankController.getInterest()));
                    }
                })
                .build();
    }

}
