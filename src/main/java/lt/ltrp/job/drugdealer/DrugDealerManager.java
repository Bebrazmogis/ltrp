package lt.ltrp.job.drugdealer;

import lt.ltrp.LoadingException;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.job.AbstractJobManager;
import lt.ltrp.job.Job;
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

        this.job = LtrpGamemode.getDao().getJobDao().getDrugDealerJob(id);

        this.playerCommandManager = new PlayerCommandManager(eventManagerNode);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        playerCommandManager.registerCommands(new DrugDealerCommands(job));
    }

    @Override
    public Job getJob() {
        return job;
    }

    @Override
    public void destroy() {
        playerCommandManager.uninstallAllHandlers();
    }

}
