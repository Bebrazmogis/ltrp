package lt.ltrp.dialog;

import lt.ltrp.BusinessController;
import lt.ltrp.GarageController;
import lt.ltrp.HouseController;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.05.
 *         The one and only...
 *         The most powerful dialog...
 *         .. The /amenu!
 */
public class AdminServerManagementDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        return ListDialog.create(player, eventManager)
                .caption(LtrpGamemode.Name + " " + LtrpGamemode.Version + " admin meniu")
                .buttonOk("Pasirinkti")
                .buttonCancel("Uþdaryti")
                .item("Namai", i -> HouseController.get().showManagementDialog(player))
                .item("Verslai", i -> BusinessController.get().showManagementDialog(player))
                .item("Verslø prekës", i -> BusinessController.get().showAvailableCommodityDialog(player))
                .item("Garaþai", i -> GarageController.get().showManagementDialog(player))
                .build();
    }
}

