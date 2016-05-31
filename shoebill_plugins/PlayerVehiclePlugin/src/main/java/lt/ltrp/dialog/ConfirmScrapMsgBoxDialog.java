package lt.ltrp.dialog;

import lt.ltrp.PlayerVehiclePlugin;
import lt.ltrp.constant.Currency;
import lt.ltrp.event.PlayerVehicleScrapEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class ConfirmScrapMsgBoxDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, PlayerVehicle vehicle) {
        PlayerVehiclePlugin plugin = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class);
        int price = plugin.getScrapPrice(vehicle);
        return  MsgboxDialog.create(player, eventManager)
                .caption("{FF0000}\t\tDëmesio!")
                .message("{FFFFFF}Ðis veiksmas sunaikins jûsø automobilá " + vehicle.getName() +
                        "\nUþ tai gausite " + Currency.SYMBOL + price +
                        "\n\n{AA1111}Ðio veiksmo atstatyti neámanoma. " +
                        "\n{FFFFFF}Ar tikrai norite tæsti?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(d -> {
                    player.giveMoney(price);
                    player.sendMessage("Jûsø pasirinkta tr. priemonë buvo sunaikinta negràþinamai.");
                    plugin.scrapVehicle(vehicle);
                    eventManager.dispatchEvent(new PlayerVehicleScrapEvent(player, vehicle));
                })
                .build();
    }

}
