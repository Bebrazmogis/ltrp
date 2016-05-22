package lt.ltrp;

import lt.ltrp.dao.PlayerDao;
import lt.ltrp.dao.impl.SqlPlayerDaoImpl;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class PlayerPlugin extends Plugin{

    private static Logger logger;
    private static PlayerPlugin instance;

    private PlayerControllerImpl playerController;
    private PlayerCommandManager playerCommandManager;
    private GameTextStyleManager gameTextStyleManager;
    private EventManagerNode eventManagerNode;


    @Override
    public void onEnable() throws Throwable {
        instance = this;
        logger = getLogger();
        replaceTypeParsers();

        eventManagerNode = getEventManager().createChildNode();
        if(Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class) != null) {
            load();
        } else {
            eventManagerNode.registerHandler(ResourceEnableEvent.class, e -> {
                Resource resource = e.getResource();
                if(resource.getClass().equals(DatabasePlugin.class)) {
                    load();
                }
            });
        }
    }

    private void load() {
        DataSource dataSource = Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource();
        playerCommandManager = new PlayerCommandManager(eventManagerNode);
        PlayerDao playerDao = new SqlPlayerDaoImpl(dataSource);
        playerController = new PlayerControllerImpl(eventManagerNode, playerDao, playerCommandManager);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        this.gameTextStyleManager = new GameTextStyleManager(eventManagerNode);

        logger.info("Player plugin loaded");
    }

    @Override
    public void onDisable() throws Throwable {
        instance = null;
        playerController.destroy();
        playerCommandManager.destroy();
        gameTextStyleManager.destroy();
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
