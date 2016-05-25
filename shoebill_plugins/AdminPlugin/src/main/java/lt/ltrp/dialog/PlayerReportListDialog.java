package lt.ltrp.dialog;

import lt.ltrp.data.PlayerReport;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.25.
 */
public class PlayerReportListDialog extends PageListDialog {

    private Collection<PlayerReport> reports;


    public PlayerReportListDialog(Player player, EventManager eventManager, Collection<PlayerReport> reports) {
        super(player, eventManager);
        this.reports = reports;
        this.setCaption("Raportai");
        this.setButtonOk("Pasirinkti");
        this.setButtonCancel("Uþdaryti");
    }


    @Override
    public void show() {
        items.clear();

        for(PlayerReport report : reports) {
            items.add(ListDialogItem.create()
                .itemText(String.format("{00FF11}%s->{FF0011}%s: {FFFFFF}%s", report.getPlayer().getName(), report.getTarget().getName(), StringUtils.limit(report.getReason(), 40)))
                .data(report)
                .build());
        }
        super.show();
    }

    @Override
    protected void onClickOk(ListDialogItem item) {
        PlayerReport report = (PlayerReport)item.getData();
        PlayerReportMsgBoxDialog.create(player, eventManagerNode, this, report)
            .show();
    }
}
