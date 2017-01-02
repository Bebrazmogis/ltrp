package lt.ltrp;

import lt.ltrp.data.Color;
import lt.ltrp.dialog.PasswordInputDialog;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.PlayerData;
import lt.ltrp.object.impl.LtrpPlayerImpl;
import lt.ltrp.player.PlayerController;
import lt.ltrp.util.Whirlpool;
import net.gtaun.shoebill.common.timers.TemporaryTimer;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.19.
 */


public class AuthPlugin extends Plugin {

    private Logger logger;
    private EventManagerNode eventManagerNode;
    private Map<Player, Integer> playerLoginAttempts;

    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        eventManagerNode = getEventManager().createChildNode();
        playerLoginAttempts = new HashMap<>();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(PlayerPlugin.class);
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
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();
    }

    private void load() {
        eventManagerNode.cancelAll();
        addEventHandlers();
    }

    private void addEventHandlers() {
        eventManagerNode.registerHandler(PlayerConnectEvent.class, HandlerPriority.HIGHEST, e -> {
            if(e.getPlayer().isNpc()) {
                return;
            }
            Player p = e.getPlayer();
            PlayerPlugin plugin = ResourceManager.get().getPlugin(PlayerPlugin.class);
            if(plugin == null) {
                p.sendMessage(Color.RED, "Prisijungimas ðiuo metu negalimas.");
                p.kick();
                e.interrupt();
                return;
            }
            PlayerData playerData = PlayerController.instance.getData(p.getName());
            logger.debug("Player data: "+ playerData + " is valid?" + (playerData != null ? playerData.isValid() : "false"));
            if(playerData != null && playerData.isValid()) {
                //LtrpPlayerImpl player = new LtrpPlayerImpl(e.getPlayer(), uuid, eventManagerNode);
                //player.setPassword(playerDao.getPassword(player));

                playerLoginAttempts.put(p, 0);
                PasswordInputDialog.create(p, eventManagerNode)
                        .onClickCancel(d -> p.kick())
                        .onClickOk((d, password) -> {
                            if(password == null || password.isEmpty())
                                d.show();
                            else {
                                logger.debug("Unhashed password:" + password);
                                password = Whirlpool.hash(password);
                                logger.debug("Hashed password:" + password);
                                logger.debug("Stored password:" + playerData.getPassword());
                                if(password.equals(playerData.getPassword())) {
                                    playerData.setLoggedIn(true);
                                    LtrpPlayerImpl impl = new LtrpPlayerImpl(playerData, p);
                                    eventManagerNode.dispatchEvent(new PlayerLogInEvent(impl, playerLoginAttempts.get(p)));
                                    playerLoginAttempts.remove(p);
                                } else {
                                    playerLoginAttempts.put(p, playerLoginAttempts.get(p) + 1);
                                    d.addLine("\n\n{AA1100}Slaptaþodis neteisingas. Tai " + playerLoginAttempts.get(p) + " bandymas ið " + LtrpPlayer.MAX_LOGIN_TRIES);
                                    d.show();
                                }
                            }

                        })
                        .build()
                        .show();
            } else {
                e.getPlayer().sendMessage(Color.LIGHTRED, "Jûs neesate uþsreigstravæs. Tai padaryti galite tinklalapyje www.ltrp.lt");
                TemporaryTimer.create(100, i -> e.getPlayer().kick()).start();
                e.interrupt(); // Nobody else should do anything
            }

        });

        eventManagerNode.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.HIGHEST, e -> {
            Player p = e.getPlayer();
            playerLoginAttempts.remove(p);
            LtrpPlayer player = LtrpPlayer.get(p);
            if(player != null) {
                eventManagerNode.dispatchEvent(new lt.ltrp.player.event.PlayerDisconnectEvent(player, e.getReason()));
            }
        });

        eventManagerNode.registerHandler(PlayerCommandEvent.class, HandlerPriority.HIGH, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player == null || !player.isLoggedIn()) {
                logger.info("Invalid or not logged in player tried using command " + e.getCommand() + " Player=" + player);
                e.interrupt();
            }
        });

        eventManagerNode.registerHandler(PlayerTextEvent.class, HandlerPriority.HIGH, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player == null || !player.isLoggedIn()) {
                logger.info("Invalid or not logged in player tried texting " + e.getText() + " Player=" + player);
                e.interrupt();
            }
        });
    }


    @Override
    protected void onDisable() throws Throwable {
        playerLoginAttempts.clear();
        eventManagerNode.cancelAll();
    }
}
