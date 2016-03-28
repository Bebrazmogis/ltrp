package lt.ltrp.player;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.data.Color;
import lt.ltrp.player.event.PlayerJailEvent;
import lt.ltrp.player.event.PlayerUnJailEvent;
import lt.maze.streamer.event.PlayerLeaveDynamicAreaEvent;
import lt.maze.streamer.object.DynamicArea;
import lt.maze.streamer.object.DynamicSphere;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

/**
 * Created by Bebras on 2016.03.26.
 * This class manages both IC and OOC jails
 */
public class PlayerJailController implements Destroyable {

    public static final Location DISCHARGE_LOCATION = new Location();
    public static final Location IC_JAIL_ENTRY = new Location();
    public static final Location OOC_JAIL_ENTRY = new Location();
    public static final DynamicArea IC_JAIL_BOUNDS = DynamicSphere.create(0f, 0f, 0f, 0f, 0, 0, null);
    public static final DynamicArea OOC_JAIL_BOUNDS = DynamicSphere.create(0f, 0f, 0f, 0f, 0, 0, null);

    private boolean destroyed;
    private EventManagerNode node;
    private Timer jailTimeTimer;

    public PlayerJailController(EventManager m, PlayerDao playerDao) {
        this.node = m.createChildNode();
        this.jailTimeTimer = Timer.create(60000, -1, i -> {
            LtrpPlayer.get()
                    .stream()
                    .filter(p -> p.getJailData() != null)
                    .forEach(p -> {
                        JailData jd = p.getJailData();
                        jd.setTime(jd.getTime() - 1);
                        if(jd.getTime() <= 0) {
                            p.unjail();
                        } else {
                            p.getInfoBox().setJailTime(jd.getTime());
                        }
                    });
        });

        this.node.registerHandler(PlayerLeaveDynamicAreaEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            JailData data = player.getJailData();
            DynamicArea area = e.getArea();
            if(data != null) {
                if(area.equals(IC_JAIL_BOUNDS) && data.getType().equals(JailData.JailType.OutOfCharacter)) {
                    player.setLocation(IC_JAIL_ENTRY);
                } else if(area.equals(OOC_JAIL_BOUNDS) && data.getType().equals(JailData.JailType.InCharacter)) {
                    player.setLocation(OOC_JAIL_ENTRY);
                }
            }
        });


        this.node.registerHandler(PlayerJailEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            JailData data = e.getJailData();
            switch(data.getType()) {
                case OutOfCharacter:
                    player.setLocation(OOC_JAIL_ENTRY);
                    break;
                case InCharacter:
                    player.setLocation(IC_JAIL_ENTRY);
                    break;
            }
            playerDao.insert(data);
        });

        this.node.registerHandler(PlayerUnJailEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            JailData data = e.getJailData();

            player.setLocation(DISCHARGE_LOCATION);
            player.sendMessage(Color.NEWS, "Jûs esate paleidþiamas ið kalëjimo!");
            playerDao.remove(player, data);
        });
    }

    @Override
    public void destroy() {
        destroyed = true;
        node.cancelAll();
        jailTimeTimer.stop();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

}
