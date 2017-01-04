package lt.ltrp.dialog;


import lt.ltrp.player.PlayerController;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.data.PlayerVehicleArrest;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class ConfirmDelArrestMsgboxDialog {


    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, PlayerVehicleArrest arrest) {
        return MsgboxDialog.create(player, eventManager)
                .caption("Transporto priemonës areðtas")
                .message("Areðtavo " + PlayerController.instance.getUsernameByUUID(arrest.getArrestedBy()) +
                    "\nData: " + arrest.getDate() +
                    "\nPrieþastis:" + arrest.getReason() +
                    "\n\nAr norite gràþinti ðià transporto priemonæ savininkui?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .build();
    }

}
