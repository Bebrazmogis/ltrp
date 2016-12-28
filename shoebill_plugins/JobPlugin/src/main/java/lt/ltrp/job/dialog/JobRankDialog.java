package lt.ltrp.job.dialog;

import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobRank;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class JobRankDialog extends PageListDialog {

    private Job job;
    private ClickOkHandler handler;

    public JobRankDialog(LtrpPlayer player, EventManager eventManager, Job job) {
        super(player, eventManager);
        super.setCaption(job.getName() + " rangai ( " + job.getRanks().size() + ")");
        super.setButtonOk("Pasirinkti");
        super.setButtonCancel("Atgal");
        this.job = job;
    }

    @Override
    @Deprecated
    public void setClickOkHandler(ListDialog.ClickOkHandler hadndler) {
        throw new NotImplementedException();
    }

    public void setClickOkHandler(ClickOkHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void onClickOk(ListDialogItem item) {
        if(handler != null)
            handler.onClickOk(this, (JobRank)item.getData());
    }

    @Override
    public void show() {
        items.clear();

        for(JobRank rank : job.getRanks()) {
            items.add(new ListDialogItem(rank, String.format("%d. %s", rank.getNumber(), rank.getName()), null));
        }
        if(job.getRanks().size() == 0) {
            items.add(new ListDialogItem("Tuðèia"));
        }

        super.show();
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(JobRankDialog dialog, JobRank rank);
    }
}
