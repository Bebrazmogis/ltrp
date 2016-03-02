package lt.ltrp;

import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerSpawnEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.02.07.
 */
public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private EventManagerNode eventManager;
    private List<Npc> npcList;


    public EventHandler(EventManager manager, List<Npc> npcs) {
        this.eventManager = manager.createChildNode();
        this.npcList = npcs;

        eventManager.registerHandler(PlayerConnectEvent.class, e -> {
            Player p = e.getPlayer();
            if(p.isNpc()) {
                if(p.getIp().equals("localhost") || p.getIp().equals("127.0.0.1")) {
                    String name = e.getPlayer().getName();
                    Optional<Npc> npc = npcList.stream().filter(n -> n.getName().equals(name)).findFirst();
                    if(npc.isPresent()) {
                        npc.get().setPlayer(p);
                        npc.get().getPlayer().setSpawnInfo(new AngledLocation(), 0, 0, null, null, null);
                        npc.get().getPlayer().spawn();
                    }
                } else {
                    logger.warn("A NPC named " + p.getName() + " tried to connect from remote host IP " + p.getIp());
                    p.kick();
                }
            }
        });

        eventManager.registerHandler(PlayerSpawnEvent.class, e -> {
            Player p = e.getPlayer();
            if(p.isNpc()) {
                Optional<Npc> optionalNpc = npcList.stream().filter(n -> n.getPlayer().equals(p)).findFirst();
                if(optionalNpc.isPresent()) {
                    optionalNpc.get().getVehicle().putPlayer(optionalNpc.get().getPlayer(), 1);
                }
            }
        });

        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            Player p = e.getPlayer();
            if(p.isNpc()) {
                Optional<Npc> optionalNpc = npcList.stream().filter(n -> n.getPlayer().equals(p)).findFirst();
                if(optionalNpc.isPresent()) {
                    npcList.remove(optionalNpc.get());
                }
            }
        });
    }

    public void destroy() {
        eventManager.cancelAll();
    }

}
