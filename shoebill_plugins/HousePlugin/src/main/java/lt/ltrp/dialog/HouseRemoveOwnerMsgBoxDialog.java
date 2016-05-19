package lt.ltrp.dialog;

import lt.ltrp.PlayerController;
import lt.ltrp.event.property.house.HouseEditEvent;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseRemoveOwnerMsgBoxDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, House house) {
        return MsgboxDialog.create(player, eventManager)
                .parentDialog(parent)
                .caption("Namo savininko �alinimas")
                .line("Dabartinis namo savininkas yra " + PlayerController.get().getUsernameByUUID(house.getOwner()))
                .line("\nAr norite pa�alinti namo savinink�, namas v�l taps parduodamas?")
                .buttonOk("Taip")
                .buttonCancel("Atgal")
                .onClickCancel((d) -> {
                    if (parent != null)
                        parent.show();
                })
                .onClickOk(d -> {
                    house.setOwner(LtrpPlayer.INVALID_USER_ID);
                    eventManager.dispatchEvent(new HouseEditEvent(house, player));
                    parent.show();
                })
                .build();
    }


}
