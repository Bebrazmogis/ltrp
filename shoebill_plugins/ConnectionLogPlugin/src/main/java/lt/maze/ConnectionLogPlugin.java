package lt.maze;


import lt.ltrp.DatabasePlugin;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerEntry;
import org.slf4j.Logger;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class ConnectionLogPlugin extends Plugin {


    private EventManagerNode eventManager;
    private Logger logger;
    private DataThread dataThread;
    private HandlerEntry resourceEntry;

    @Override
    protected void onEnable() throws Throwable {
        eventManager = getEventManager().createChildNode();
        logger = getLogger();

        if(ResourceManager.get().getPlugin(DatabasePlugin.class) != null) {
            init();
        } else {
            resourceEntry = eventManager.registerHandler(ResourceEnableEvent.class, e -> {
                if(e.getResource().getClass().equals(DatabasePlugin.class)) {
                    init();
                }
            });
        }

        eventManager.registerHandler(PlayerConnectEvent.class, e -> {
            String ip = e.getPlayer().getIp();
            String username = e.getPlayer().getName();
            DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
            dataThread.addData(username, ip);
        });
    }

    private void init() {
        resourceEntry.cancel();
        dataThread = new DataThread(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource());
        dataThread.start();
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        if(resourceEntry != null)
            resourceEntry.cancel();
        if(dataThread != null)
            dataThread.interrupt();
    }

/*
    CREATE TABLE IF NOT EXISTS player_connections (
	id INT AUTO_INCREMENT NOT NULL,
	username VARCHAR(24) NOT NULL,
	ip VARCHAR(32) NOT NULL,
	isp VARCHAR(64) NOT NULL,
	country VARCHAR(64) NOT NULL,
	created_at TIMESTAMP NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

 */
}
