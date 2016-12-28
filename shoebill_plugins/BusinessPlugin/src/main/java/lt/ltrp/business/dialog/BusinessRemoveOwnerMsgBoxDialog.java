package lt.ltrp.business.dialog;

import lt.ltrp.player.PlayerController;
import lt.ltrp.event.property.BusinessEditEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.27.
 */
public class BusinessRemoveOwnerMsgBoxDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, Business business) {
        return MsgboxDialog.create(player, eventManager)
                .parentDialog(parent)
                .caption("Verslo savininko ðalinimas")
                .line("Dabartinis verslo savininkas yra " + PlayerController.instance.getUsernameByUUID(business.getOwner()))
                .line("\nAr norite paðalinti verslo savininkà, verslas vël taps parduodamas?")
                .buttonOk("Taip")
                .buttonCancel("Atgal")
                .onClickCancel((d) -> {
                    if (parent != null)
                        parent.show();
                })
                .onClickOk(d -> {
                    business.setOwner(LtrpPlayer.INVALID_USER_ID);
                    eventManager.dispatchEvent(new BusinessEditEvent(business, player));
                })
                .build();
    }

}
