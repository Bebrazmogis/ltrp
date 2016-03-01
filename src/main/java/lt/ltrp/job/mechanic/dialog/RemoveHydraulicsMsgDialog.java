package lt.ltrp.job.mechanic.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.28.
 */
public class RemoveHydraulicsMsgDialog {

    public static MsgboxDialog create(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager, int price) {
        return MsgboxDialog.create(player, eventManager)
                .caption("Hidraulinë pakaba")
                .message("Taisomas automobilis: " + vehicle.getModelName() +
                        "\nHidraulinës pakabos iðëmimo kaina: " + Currency.SYMBOL + price +
                        "\nAr norite pradëti darbus?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .build();
    }

}
