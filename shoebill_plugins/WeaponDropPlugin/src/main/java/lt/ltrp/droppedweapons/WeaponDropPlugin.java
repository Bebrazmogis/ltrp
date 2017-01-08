package lt.ltrp.droppedweapons;

import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.droppedweapons.command.WeaponDropCommands;
import lt.ltrp.droppedweapons.object.DroppedWeaponData;
import net.gtaun.shoebill.ShoebillMain;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.13.
 */
@ShoebillMain(name = "Dropped weapon plugin", author = "Bebras")
public class WeaponDropPlugin extends Plugin {

    private Logger logger;
    private EventManagerNode eventManagerNode;
    private PlayerCommandManager playerCommandManager;


    private int weaponDisappearInterval = 3 * 60 * 1000;

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
        playerCommandManager.registerCommands(new WeaponDropCommands(this));
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

    private void addEventHandlers() {

    }

    public void setWeaponDisappearInterval(int weaponDisappearInterval) {
        this.weaponDisappearInterval = weaponDisappearInterval;
    }

    public void dropWeapon(LtrpWeaponData weaponData, Location location) {
        DroppedWeaponData data = new DroppedWeaponData(
                weaponData.getWeaponData().model,
                weaponData.getWeaponData().ammo,
                weaponData.isJob(),
                location,
                weaponDisappearInterval,
                eventManagerNode
        );
        logger.debug("Dropped weapon " + weaponData.getUUID());
    }

    public Collection<DroppedWeaponData> getDroppedWeapos() {
        return DroppedWeaponData.get();
    }
}
