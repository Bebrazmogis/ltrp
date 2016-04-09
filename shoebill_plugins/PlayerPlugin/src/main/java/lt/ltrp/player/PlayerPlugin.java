package lt.ltrp.player;

import lt.ltrp.DatabasePlugin;
import lt.ltrp.player.dao.PlayerDao;
import lt.ltrp.player.dao.impl.SqlPlayerDaoImpl;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class PlayerPlugin extends Plugin{

    private static Logger logger;
    private static PlayerPlugin instance;

    private PlayerControllerImpl playerController;

    @Override
    public void onEnable() throws Throwable {
        instance = this;
        logger = getLogger();

        EventManager eventManager = getEventManager();
        PlayerDao playerDao = new SqlPlayerDaoImpl(Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource());
        playerController = new PlayerControllerImpl(eventManager, playerDao);



        logger.info("Player plugin loaded");
    }

    @Override
    public void onDisable() throws Throwable {
        instance = null;
        playerController.destroy();

        logger.info("Player plugin shutting down...");
    }
}
