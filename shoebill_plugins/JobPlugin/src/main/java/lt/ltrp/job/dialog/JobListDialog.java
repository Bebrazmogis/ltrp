package lt.ltrp.job.dialog;

import lt.ltrp.job.JobController;
import lt.ltrp.job.object.Job;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
 *
*/
public class JobListDialog extends PageListDialog {

    public static AbstractJobListDialogBuilder create(LtrpPlayer player, EventManager eventManager) {
        return new JobListDialogBuilder(new JobListDialog(player, eventManager));
    }

    private SelectJobHandler handler;

    public JobListDialog(LtrpPlayer player, EventManager eventManager) {
        super(player, eventManager);
        super.setCaption("Darbø sàraðas(" + JobController.instance.get().size() + ")");
        super.setButtonOk("Pasirinkti");
        super.setButtonCancel("Atgal");
    }

    public void setSelectHandler(SelectJobHandler handler) {
        this.handler = handler;
    }

    @Override
    public void show() {
        items.clear();

        for(final Job job : JobController.instance.get()) {
            ListDialogItem item = new ListDialogItem();
            item.setItemText(job.getName());
            item.setData(job);
            items.add(item);
        }
        super.show();
    }

    @Override
    protected void onClickOk(ListDialogItem item) {
        if(handler != null)
            handler.onSelectJob(this, (Job)item.getData());
        else
            super.onClickOk(item);
    }

    @FunctionalInterface
    public interface SelectJobHandler {
        void onSelectJob(JobListDialog dialog, Job job);
    }

    @SuppressWarnings("unchecked")
    public static class AbstractJobListDialogBuilder<DialogType extends JobListDialog, DialogBuilderType extends AbstractJobListDialogBuilder>
        extends AbstractPageListDialogBuilder<DialogType, AbstractJobListDialogBuilder<DialogType, DialogBuilderType>> {

        public DialogBuilderType selectJobHandler(SelectJobHandler handler) {
            dialog.setSelectHandler(handler);
            return (DialogBuilderType)this;
        }

        protected AbstractJobListDialogBuilder(DialogType dialog) {
            super(dialog);
        }
    }

    private static class JobListDialogBuilder extends AbstractJobListDialogBuilder<JobListDialog, AbstractJobListDialogBuilder> {

        protected JobListDialogBuilder(JobListDialog dialog) {
            super(dialog);
        }
    }

}
