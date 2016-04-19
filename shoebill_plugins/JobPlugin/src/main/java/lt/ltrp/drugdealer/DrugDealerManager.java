package lt.ltrp.drugdealer;

import lt.ltrp.AbstractJobManager;
import lt.ltrp.LoadingException;
import lt.ltrp.JobController;
import lt.ltrp.object.DrugDealerJob;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class DrugDealerManager extends AbstractJobManager {

    private int id;
    private DrugDealerJob job;
    private PlayerCommandManager playerCommandManager;

    public DrugDealerManager(EventManager eventManager, int id) throws LoadingException {
        super(eventManager);
        this.id = id;

        this.job = JobController.get().getDao().getDrugDealerJob(id);

        this.playerCommandManager = new PlayerCommandManager(eventManagerNode);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        playerCommandManager.registerCommands(new DrugDealerCommands(job, eventManager));
    }

    @Override
    public DrugDealerJob getJob() {
        return job;
    }

    @Override
    public void destroy() {
        playerCommandManager.uninstallAllHandlers();
    }

}
