package lt.ltrp.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.28.
 */
public class RemoveHydraulicsMsgDialog {

    public static MsgboxDialog create(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager, int price) {
        return MsgboxDialog.create(player, eventManager)
                .caption("Hidraulin� pakaba")
                .message("Taisomas automobilis: " + vehicle.getModelName() +
                        "\nHidraulin�s pakabos i��mimo kaina: " + Currency.SYMBOL + price +
                        "\nAr norite prad�ti darbus?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .build();
    }

}
