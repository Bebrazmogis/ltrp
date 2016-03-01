package lt.ltrp.dialog;

import lt.ltrp.job.Job;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
*/
public class JobListDialog extends PageListDialog {

    public JobListDialog(LtrpPlayer player, EventManager eventManager) {
        super(player, eventManager);
        super.setCaption("Darbø sàraðas(" + Job.get().size() + ")");
        super.setButtonOk("Pasirinkti");
        super.setButtonCancel("Atgal");
    }



    @Override
    public void show() {
        items.clear();

        for(final Job job : Job.get()) {
            ListDialogItem item = new ListDialogItem();
            item.setItemText(job.getName());
            item.setData(job);
            items.add(item);
        }
        super.show();
    }

}
