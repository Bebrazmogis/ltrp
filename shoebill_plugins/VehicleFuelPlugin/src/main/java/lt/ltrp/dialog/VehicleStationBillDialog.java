package lt.ltrp.dialog;

import lt.ltrp.VehicleFuelPlugin;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.FillData;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.06.
 *
 */
public class VehicleStationBillDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager parentEventManager, FillData fillData) {
        int price = Math.round(fillData.getFuel() * VehicleFuelPlugin.FUEL_PRICE);
        return MsgboxDialog.create(player, parentEventManager)
                .caption("Degalinë")
                .line("Kuro kaina: " + VehicleFuelPlugin.FUEL_PRICE + Currency.SYMBOL + "/L")
                .line("Kiekis: " + fillData.getFuel())
                .line("Mokestis uþ degalus: " + price)
                .line("Kuo mokësite, grynais ar banku?")
                .buttonOk("Grynais")
                .buttonCancel("Banku")
                .build();
    }

}
