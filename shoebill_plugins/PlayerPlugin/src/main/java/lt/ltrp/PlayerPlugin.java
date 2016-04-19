package lt.ltrp;

import lt.ltrp.dao.PlayerDao;
import lt.ltrp.dao.impl.SqlPlayerDaoImpl;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class PlayerPlugin extends Plugin{

    private static Logger logger;
    private static PlayerPlugin instance;

    private PlayerControllerImpl playerController;
    private AdminController adminController;
    private PlayerCommandManager playerCommandManager;


    @Override
    public void onEnable() throws Throwable {
        instance = this;
        logger = getLogger();
        replaceTypeParsers();

        EventManager eventManager = getEventManager();
        playerCommandManager = new PlayerCommandManager(eventManager);
        PlayerDao playerDao = new SqlPlayerDaoImpl(Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource());
        playerController = new PlayerControllerImpl(eventManager, playerDao, playerCommandManager);
        adminController = new AdminControllerImpl(eventManager, playerCommandManager);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);


        logger.info("Player plugin loaded");
    }

    @Override
    public void onDisable() throws Throwable {
        instance = null;
        playerController.destroy();
        adminController.destroy();
        playerCommandManager.destroy();

        logger.info("Player plugin shutting down...");
    }

    private static void replaceTypeParsers() {
        PlayerCommandManager.replaceTypeParser(LtrpPlayer.class, s -> {
            int id = Player.INVALID_ID;
            try {
                id = Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return null;
            }
            return LtrpPlayer.get(id);
        });
    }

}
