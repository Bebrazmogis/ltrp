package lt.ltrp;

import lt.ltrp.command.WeaponShopCommands;
import lt.ltrp.dao.WeaponShopDao;
import lt.ltrp.dao.WeaponShopWeaponDao;
import lt.ltrp.dao.impl.MySqlWeaponShopDaoImpl;
import lt.ltrp.dao.impl.MySqlWeaponShopWeaponDaoImpl;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopPlugin extends Plugin {

    private EventManagerNode eventManagerNode;
    private Logger logger;
    private WeaponShopWeaponDao weaponShopWeaponDao;
    private WeaponShopDao weaponShopDao;
    private Collection<WeaponShop> weaponShops;
    private PlayerCommandManager playerCommandManager;

    @Override
    protected void onEnable() throws Throwable {
        this.eventManagerNode = getEventManager().createChildNode();
        logger = getLogger();
        this.weaponShops = new ArrayList<>();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            eventManagerNode.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                logger.debug("R:" + r);
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    logger.debug("Removing r");
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();
    }

    private void load() {
        eventManagerNode.cancelAll();
        DatabasePlugin db = ResourceManager.get().getPlugin(DatabasePlugin.class);
        this.weaponShopWeaponDao = new MySqlWeaponShopWeaponDaoImpl(db.getDataSource());
        this.weaponShopDao = new MySqlWeaponShopDaoImpl(db.getDataSource(), eventManagerNode);

        this.weaponShops.addAll(weaponShopDao.getWithWeapons());
        logger.info("Loaded " + weaponShops.size() + " weapon shops.");

        playerCommandManager = new PlayerCommandManager(eventManagerNode);
        playerCommandManager.registerCommands(new WeaponShopCommands());
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
    }


    @Override
    protected void onDisable() throws Throwable {
        weaponShops.forEach(WeaponShop::destroy);
        weaponShops.clear();
        eventManagerNode.cancelAll();
        playerCommandManager.uninstallAllHandlers();
        playerCommandManager.destroy();
    }

    public WeaponShopDao getShopDao() {
        return weaponShopDao;
    }

    public WeaponShopWeaponDao getShopWeaponDao() {
        return weaponShopWeaponDao;
    }

    public Collection<WeaponShop> getWeaponShops() {
        return weaponShops;
    }
}
