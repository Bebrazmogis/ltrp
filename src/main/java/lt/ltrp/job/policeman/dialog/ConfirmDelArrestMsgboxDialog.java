package lt.ltrp.job.policeman.dialog;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.PlayerVehicleArrest;
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
                .message("Areðtavo " + LtrpGamemode.getDao().getPlayerDao().getUsername(arrest.getArrestedBy()) +
                    "\nData: " + arrest.getDate() +
                    "\nPrieþastis:" + arrest.getReason() +
                    "\n\nAr norite gràþinti ðià transporto priemonæ savininkui?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .build();
    }

}
