package lt.ltrp.dialog;

import lt.ltrp.GarageController;
import lt.ltrp.player.PlayerController;
import lt.ltrp.event.property.garage.GarageEditEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageRemoveOwnerMsgBoxDialog {
    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, Garage garage) {
        return MsgboxDialog.create(player, eventManager)
                .parentDialog(parent)
                .caption("Garaþo savininko ðalinimas")
                .line("Dabartinis namo savininkas yra " + PlayerController.get().getUsernameByUUID(garage.getOwner()))
                .line("\nAr norite paðalinti garaþo savininkà, garaþas vël taps parduodamas?")
                .buttonOk("Taip")
                .buttonCancel("Atgal")
                .onClickCancel((d) -> {
                    if (parent != null)
                        parent.show();
                })
                .onClickOk(d -> {
                    garage.setOwner(LtrpPlayer.INVALID_USER_ID);
                    eventManager.dispatchEvent(new GarageEditEvent(garage, player));
                    GarageController.get().getDao().update(garage);
                    parent.show();
                })
                .build();
    }
}
