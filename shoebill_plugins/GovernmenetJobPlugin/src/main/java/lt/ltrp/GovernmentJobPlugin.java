package lt.ltrp;

import lt.ltrp.command.GovernmentCommands;
import lt.ltrp.dao.GovernmentFactionDao;
import lt.ltrp.dao.impl.MySqlGovernmentFactionDaoImpl;
import lt.ltrp.object.GovernmentFaction;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class GovernmentJobPlugin extends Plugin {

    private EventManagerNode eventManager;
    private Logger logger;
    private GovernmentFactionDao governmentDao;
    private GovernmentFaction governmentFaction;
    private PlayerCommandManager commandManager;

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

    private  void load() {
        eventManager.cancelAll();
        this.governmentDao= new MySqlGovernmentFactionDaoImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), null, null, eventManager);
        this.governmentFaction= governmentDao.get(JobPlugin.JobId.Government.id);
        addCommands();
        logger.info(getDescription().getName() + " loaded");
    }

    private void addCommands() {
        commandManager=  new PlayerCommandManager(eventManager);
        commandManager.registerCommands(new GovernmentCommands());
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        commandManager.uninstallAllHandlers();
        commandManager.destroy();
    }

    public GovernmentFaction getGovernmentFaction() {
        return governmentFaction;
    }
}
