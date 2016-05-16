package lt.ltrp.dialog.property.business;

import lt.ltrp.constant.BusinessType;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class BusinessTypeListDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, BusinessTypeSelectHandler handler) {
        ListDialog dialog =  ListDialog.create(player, eventManager)
                .caption("Verslo tipo pasirinkimas" + BusinessType.values().length)
                .buttonOk("Pasirinkti")
                .buttonCancel("Iðeiti")
                .build();
        for(BusinessType type : BusinessType.values()) {
            dialog.addItem(type.getId() + ". " + type.getName(), i -> {
                if(handler != null) handler.onSelectType(dialog, type);
            });
        }
        return dialog;
    }


    @FunctionalInterface
    public interface BusinessTypeSelectHandler {
        void onSelectType(ListDialog dialog, BusinessType type);
    }

}
