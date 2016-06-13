package lt.maze;

import lt.maze.event.PlayerAttemptDriveByEvent;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerWeaponShotEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class AntiDriveByPlugin extends Plugin {

    private Logger logger;
    private EventManagerNode eventManagerNode;
    private PlayerCommandManager playerCommandManager;

    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        eventManagerNode = getEventManager().createChildNode();

        eventManagerNode.registerHandler(PlayerStateChangeEvent.class, HandlerPriority.HIGH, e -> {
            Player p = e.getPlayer();
            PlayerState newState = p.getState();
            if(newState == PlayerState.DRIVER || newState == PlayerState.PASSENGER) {
                p.setArmedWeapon(WeaponModel.NONE);
            }
        });

        eventManagerNode.registerHandler(PlayerWeaponShotEvent.class, HandlerPriority.HIGH, e -> {
            Player p = e.getPlayer();
            if(p.isInAnyVehicle() && p.getCameraMode() == 55) {
                p.setArmedWeapon(WeaponModel.NONE);
                eventManagerNode.dispatchEvent(new PlayerAttemptDriveByEvent(p));
                logger.warn("Player shot his weapon " + e.getWeapon().getModelId() + " from a vehicle");
                e.disallow();
            }
        });

        playerCommandManager = new PlayerCommandManager(eventManagerNode);
        playerCommandManager.registerCommand("drivebyoff", null, (player, queue) -> {
            player.setArmedWeapon(WeaponModel.NONE);
            return true;
        }, null, null, null, null);
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManagerNode.cancelAll();
        playerCommandManager.uninstallAllHandlers();
        playerCommandManager.destroy();
    }


}
