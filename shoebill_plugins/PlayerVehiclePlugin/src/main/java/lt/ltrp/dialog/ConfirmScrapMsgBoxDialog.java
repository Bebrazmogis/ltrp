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
                .caption("{FF0000}\t\tD�mesio!")
                .message("{FFFFFF}�is veiksmas sunaikins j�s� automobil� " + vehicle.getName() +
                        "\nU� tai gausite " + Currency.SYMBOL + price +
                        "\n\n{AA1111}�io veiksmo atstatyti ne�manoma. " +
                        "\n{FFFFFF}Ar tikrai norite t�sti?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(d -> {
                    player.giveMoney(price);
                    player.sendMessage("J�s� pasirinkta tr. priemon� buvo sunaikinta negr��inamai.");
                    plugin.scrapVehicle(vehicle);
                    eventManager.dispatchEvent(new PlayerVehicleScrapEvent(player, vehicle));
                })
                .build();
    }

}
