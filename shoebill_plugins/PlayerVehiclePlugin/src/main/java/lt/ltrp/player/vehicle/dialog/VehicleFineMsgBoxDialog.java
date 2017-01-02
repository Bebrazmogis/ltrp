package lt.ltrp.player.vehicle.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.player.vehicle.data.VehicleFine;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class VehicleFineMsgBoxDialog {

    static final DateFormat dateFormat = new SimpleDateFormat();

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, VehicleFine fine) {
        return MsgboxDialog.create(player, eventManager)
                .caption("Baudos iðraðos #" + fine.getUUID())
                .buttonOk("Gerai")
                .line("Automobilio numeriai baudos iðraðymo metu: " + fine.getLicense())
                .line("Pareigûnas iðraðæs baudà: " + fine.getReportedBy())
                .line("Baudos data: " + dateFormat.format(fine.getCreatedAt()))
                .line("Piniginë bauda: " + fine.getFine() + Currency.SYMBOL)
                .line("Sumokëta: " + (fine.isPaid() ? "Taip" : "Ne"))
                .line((fine.isPaid() ? dateFormat.format(fine.getPaidAt()) : ""))
                .line("\n\n")
                .line(StringUtils.addLineBreaks(fine.getCrime(), 50))
                .buttonCancel(() -> parent == null ? "Uþdaryti" : "Atgal")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }

}
