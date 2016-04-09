package lt.ltrp.dialog;

import lt.ltrp.event.BankWireTransferEvent;
import lt.ltrp.player.BankAccount;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankWireInputDialog {

    protected static InputDialog create(LtrpPlayer player, EventManager manager, BankAccount account) {
        return InputDialog.create(player, manager)
                .caption("Pinig� pervedimas 1/2")
                .message("�veskite banko s�skaitos numer� � kur� norite pervesti pinig�.")
                .buttonOk("T�sti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, number) -> {
                    IntegerInputDialog.create(player, manager)
                            .caption("Pinig� pervedimas 2/2")
                            .message("Pervedimas � s�skait�: " + number
                                    + "\nJ�s� s�skaitos likutis: " + account.getMoney()
                                    + "\n\n�veskite sum� kuri� norite pervesti")
                            .buttonOk("Pervesti")
                            .buttonCancel("Atgal")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((dd, amount) -> {
                                if (amount > account.getMoney()) {
                                    player.sendErrorMessage("J�s� s�skaitoje tiek n�ra!");
                                    dd.show();
                                } else {
                                    manager.dispatchEvent(new BankWireTransferEvent(player, account, number, amount));
                                }
                            })
                            .build()
                            .show();
                })
                .build();
    }

}
