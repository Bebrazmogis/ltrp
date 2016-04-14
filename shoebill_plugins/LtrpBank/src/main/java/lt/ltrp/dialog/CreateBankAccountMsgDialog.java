package lt.ltrp.dialog;

import lt.ltrp.BankController;
import lt.ltrp.common.constant.Currency;
import lt.ltrp.event.BankAccountCreateEvent;
import lt.ltrp.player.BankAccount;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class CreateBankAccountMsgDialog  {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, BankController bankController) {
        return MsgboxDialog.create(player, eventManager)
                .caption("${name}")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .message("Sveiki atvyk� � ${name}. "
                        + "\n\nM�s� duomenimis J�s s�skaitos neturite."
                        + "\nS�skaitos atidarymo kaina: " + Currency.SYMBOL + bankController.getAccountPrice()
                        + "\nAtsidar� s�skait� gal�site naudotis m�s� banko paslaugomis:"
                        + "\n\t� Gryn�j� pinig� �ne�imas/pa�mimas"
                        + "\n\t� Pinig� pervedimai"
                        + "\n\t� Ind�lis"
                        + "\n\nAr norite dabar atsidaryti s�skait�?")
                .onClickOk(d -> {
                    String number = bankController.generateAccountNumber(player);
                    BankAccount account = new BankAccount(number, player.getUUID());
                    eventManager.dispatchEvent(new BankAccountCreateEvent(player, account));
                })
                .build();
    }

}
