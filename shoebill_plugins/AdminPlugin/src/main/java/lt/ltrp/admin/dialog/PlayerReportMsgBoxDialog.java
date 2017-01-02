package lt.ltrp.dialog;

import lt.ltrp.data.PlayerReport;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManagerNode;

/**
 * @author Bebras
 *         2016.05.25.
 */
public class PlayerReportMsgBoxDialog {


    public static MsgboxDialog create(Player player, EventManagerNode eventManagerNode, AbstractDialog parent, PlayerReport report) {
        return MsgboxDialog.create(player, eventManagerNode)
                .caption("Raporto perþiûra")
                .buttonOk("Atgal")
                .buttonCancel("")
                .line("Data: " + report.getInstant().toString())
                .line("Praneeðëjas: " + report.getPlayer().getName())
                .line("Paþeidëjas: " + report.getTarget().getName())
                .line("Atsakytas: " + (report.isAnswered() ? "Taip" : "Ne"))
                .line("Þinutë: ")
                .line(StringUtils.addLineBreaks(report.getReason(), 40))
                .parentDialog(parent)
                .onClickOk(AbstractDialog::showParentDialog)
                .build();
    }
}
