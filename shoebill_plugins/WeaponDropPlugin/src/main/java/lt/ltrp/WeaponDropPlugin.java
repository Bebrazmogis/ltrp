package lt.ltrp;

import lt.ltrp.command.WeaponDropCommands;
import lt.ltrp.data.DroppedWeaponData;
import lt.ltrp.data.LtrpWeaponData;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class WeaponDropPlugin extends Plugin {

    private Logger logger;
    private EventManagerNode eventManagerNode;
    private PlayerCommandManager playerCommandManager;

    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        eventManagerNode = getEventManager().createChildNode();

        eventManagerNode.cancelAll();
        addEventHandlers();
        addCommands();
        logger.info(getDescription().getName()  + " loaded");
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManagerNode.cancelAll();
        playerCommandManager.destroy();
    }


    private void addCommands() {
        playerCommandManager = new PlayerCommandManager(eventManagerNode);
        playerCommandManager.registerCommands(new WeaponDropCommands());
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

    private void addEventHandlers() {

    }

    public void dropWeapon(LtrpWeaponData weaponData, Location location) {
        new DroppedWeaponData(weaponData.getModel(), weaponData.getAmmo(), weaponData.isJob(), location);
        // Event perhaps?
        logger.debug("Dropped weapon " + weaponData.getUUID());
    }

}
