package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.command.WeaponShopCommands;
import lt.ltrp.dao.WeaponShopDao;
import lt.ltrp.dao.WeaponShopWeaponDao;
import lt.ltrp.dao.impl.MySqlWeaponShopDaoImpl;
import lt.ltrp.dao.impl.MySqlWeaponShopWeaponDaoImpl;
import lt.ltrp.object.WeaponShop;
import lt.ltrp.resource.DependentPlugin;
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
public class WeaponShopPlugin extends DependentPlugin {

    private EventManagerNode eventManagerNode;
    private Logger logger;
    private WeaponShopWeaponDao weaponShopWeaponDao;
    private WeaponShopDao weaponShopDao;
    private Collection<WeaponShop> weaponShops;
    private PlayerCommandManager playerCommandManager;

    public WeaponShopPlugin() {
        super();
        addDependency(new KClassImpl<>(DatabasePlugin.class));
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        this.eventManagerNode = getEventManager().createChildNode();
        logger = getLogger();
        this.weaponShops = new ArrayList<>();
    }

    @Override
    public void onDependenciesLoaded() {
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
    protected void onDisable() {
        super.onDisable();
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
