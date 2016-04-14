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
                .message("Sveiki atvykæ á ${name}. "
                        + "\n\nMûsø duomenimis Jûs sàskaitos neturite."
                        + "\nSàskaitos atidarymo kaina: " + Currency.SYMBOL + bankController.getAccountPrice()
                        + "\nAtsidaræ sàskaità galësite naudotis mûsø banko paslaugomis:"
                        + "\n\t• Grynøjø pinigø áneðimas/paëmimas"
                        + "\n\t• Pinigø pervedimai"
                        + "\n\t• Indëlis"
                        + "\n\nAr norite dabar atsidaryti sàskaità?")
                .onClickOk(d -> {
                    String number = bankController.generateAccountNumber(player);
                    BankAccount account = new BankAccount(number, player.getUUID());
                    eventManager.dispatchEvent(new BankAccountCreateEvent(player, account));
                })
                .build();
    }

}
