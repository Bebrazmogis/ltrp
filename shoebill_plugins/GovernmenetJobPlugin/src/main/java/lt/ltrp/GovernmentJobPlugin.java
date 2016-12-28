package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.command.GovernmentCommands;
import lt.ltrp.dao.GovernmentFactionDao;
import lt.ltrp.business.dao.impl.MySqlGovernmentFactionDaoImpl;
import lt.ltrp.object.GovernmentFaction;
import lt.ltrp.object.impl.GovernmentFactionImpl;
import lt.ltrp.resource.DependentPlugin;
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
public class GovernmentJobPlugin extends DependentPlugin {

    private EventManagerNode eventManager;
    private Logger logger;
    private GovernmentFactionDao governmentDao;
    private GovernmentFaction governmentFaction;
    private PlayerCommandManager commandManager;

    public GovernmentJobPlugin() {
        addDependency(new KClassImpl<>(DatabasePlugin.class));
        addDependency(new KClassImpl<>(JobPlugin.class));
    }


    @Override
    public void onDependenciesLoaded() {
        logger = getLogger();
        eventManager = getEventManager().createChildNode();

        this.governmentDao= new MySqlGovernmentFactionDaoImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), eventManager);
        this.governmentFaction= new GovernmentFactionImpl(JobPlugin.JobId.Government.id, eventManager);
        addCommands();
        logger.info(getDescription().getName() + " loaded");
    }

    private void addCommands() {
        commandManager=  new PlayerCommandManager(eventManager);
        commandManager.registerCommands(new GovernmentCommands());
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        eventManager.cancelAll();
        commandManager.uninstallAllHandlers();
        commandManager.destroy();
    }

    public GovernmentFaction getGovernmentFaction() {
        return governmentFaction;
    }
}
