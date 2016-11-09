package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.command.DrugDealerCommands;
import lt.ltrp.dao.DrugDealerJobDao;
import lt.ltrp.dao.impl.MySqlDrugDealerJobImpl;
import lt.ltrp.object.DrugDealerJob;
import lt.ltrp.object.impl.DrugDealerJobImpl;
import lt.ltrp.resource.DependentPlugin;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class DrugDealerJobPlugin extends DependentPlugin {

    private EventManagerNode eventManager;
    private Logger logger;
    private DrugDealerJobDao drugDealerDao;
    private DrugDealerJob drugDealerJob;
    private PlayerCommandManager playerCommandManager;

    public DrugDealerJobPlugin() {
        addDependency(new KClassImpl<>(DatabasePlugin.class));
        addDependency(new KClassImpl<>(JobPlugin.class));
    }


    @Override
    public void onDependenciesLoaded() {
        logger = getLogger();
        eventManager = getEventManager().createChildNode();

        drugDealerDao = new MySqlDrugDealerJobImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), eventManager);
        drugDealerJob = new DrugDealerJobImpl(JobPlugin.JobId.DrugDealer.id, eventManager);

        this.playerCommandManager = new PlayerCommandManager(eventManager);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        playerCommandManager.registerCommands(new DrugDealerCommands(drugDealerJob, eventManager));

        logger.info(getDescription().getName() + " loaded");
    }


    @Override
    protected void onDisable() {
        super.onDisable();
        eventManager.cancelAll();
        playerCommandManager.uninstallAllHandlers();
        logger.info(getDescription().getName() + " unloaded");
    }
}
