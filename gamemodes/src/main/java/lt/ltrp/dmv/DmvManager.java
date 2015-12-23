package lt.ltrp.dmv;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.PlayerCommandManager;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvManager {

    private static final Logger logger = LoggerFactory.getLogger(DmvManager.class);
    private static final DmvManager instance = new DmvManager();

    public static DmvManager getInstance() {
        return instance;
    }


    private EventManager eventManager;
    private PlayerCommandManager commandManager;
    private List<Dmv> dmvList;


    private DmvManager() {
        eventManager = LtrpGamemode.get().getEventManager().createChildNode();

        dmvList = LtrpGamemode.getDao().getDmvDao().getDmvs();

        commandManager = new PlayerCommandManager(HandlerPriority.NORMAL, eventManager);
        commandManager.registerCommands(new DmvCommands());


        logger.info("Dmv manager initialized with " + dmvList.size() + " dmvs");
    }

    public List<Dmv> getDmvs() {
        return dmvList;
    }

    protected EventManager getEventManager() {
        return eventManager;
    }
}
