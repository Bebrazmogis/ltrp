package lt.ltrp.dialog;

import lt.ltrp.LtrpWorld;
import lt.ltrp.PlayerVehiclePlugin;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.Color;
import lt.ltrp.data.VehicleFine;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.31.
 *
 *         This dialog is a confirmation whether to pay a fine, or not
 */
public class ConfirmPayFineMsgBoxDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog dialog, VehicleFine fine) {
        return MsgboxDialog.create(player, eventManager)
                .parentDialog(dialog)
                .onClickCancel(AbstractDialog::showParentDialog)
                .caption("Baudos #" + fine.getUUID() + " apok�jimas")
                .buttonOk("Taip")
                .buttonCancel("Atgal")
                .line("Baudos i�ra�ymo data: " + VehicleFineMsgBoxDialog.dateFormat.format(fine.getCreatedAt()))
                .line("Baud� i�ra��s pareig�nas: " + fine.getReportedBy())
                .line("Baudos dydis: " + fine.getFine() + Currency.SYMBOL)
                .line("\n")
                .line(StringUtils.addLineBreaks(fine.getCrime(), 60))
                .line("\n\nAr norite apmok�ti �i� baud�?")
                .onClickOk(d -> {
                    if (player.getMoney() < fine.getFine()) {
                        player.sendErrorMessage("Jums neu�tenka pinig� apmok�ti �iai baudai.");
                        dialog.show();
                    } else {
                        player.giveMoney(-fine.getFine());
                        LtrpWorld.get().addMoney(LtrpWorld.get().getTaxes().getVAT(fine.getFine()));
                        PlayerVehiclePlugin.get(PlayerVehiclePlugin.class).getFineDao().setPaid(fine);
                        player.sendMessage(Color.NEWS, "Bauda s�kmingai apmok�ta!");
                    }

                })
                .build();
    }

}
