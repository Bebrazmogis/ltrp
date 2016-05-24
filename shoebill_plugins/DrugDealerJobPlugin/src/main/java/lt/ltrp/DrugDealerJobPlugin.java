package lt.ltrp;

import lt.ltrp.command.DrugDealerCommands;
import lt.ltrp.dao.DrugDealerJobDao;
import lt.ltrp.dao.impl.MySqlDrugDealerJobImpl;
import lt.ltrp.object.DrugDealerJob;
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
public class DrugDealerJobPlugin extends Plugin {

    private EventManagerNode eventManager;
    private Logger logger;
    private DrugDealerJobDao drugDealerDao;
    private DrugDealerJob drugDealerJob;
    private PlayerCommandManager playerCommandManager;


    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        eventManager = getEventManager().createChildNode();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(JobPlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            eventManager.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();
    }

    private void load() {
        eventManager.cancelAll();
        drugDealerDao = new MySqlDrugDealerJobImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), null, eventManager);
        drugDealerJob = drugDealerDao.get(JobPlugin.JobId.DrugDealer.id);

        this.playerCommandManager = new PlayerCommandManager(eventManager);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        playerCommandManager.registerCommands(new DrugDealerCommands(drugDealerJob, eventManager));

        logger.info(getDescription().getName() + " loaded");
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        playerCommandManager.uninstallAllHandlers();
        logger.info(getDescription().getName() + " unloaded");
    }
}
