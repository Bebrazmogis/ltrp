package lt.ltrp.player;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.data.Color;
import lt.ltrp.player.event.PlayerJailEvent;
import lt.ltrp.player.event.PlayerUnJailEvent;
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
    public static final DynamicPolygon IC_JAIL_BOUNDS;
    public static final DynamicPolygon OOC_JAIL_BOUNDS;

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

        this.node.registerHandler(PlayerLeaveDynamicArea.class, e -> {
             
        });


        this.node.registerHandler(PlayerJailEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            JailData data = e.getJailData();
            AmxCallable getPos = PawnFunc.getPublicMethod("Data_GetCoordinates");
            AmxCallable getWorld = PawnFunc.getPublicMethod("Data_GetVirtualWorld");
            AmxCallable getInterior = PawnFunc.getPublicMethod("Data_GetInterior");
            String key = "";
            switch(data.getType()) {
                case OutOfCharacter:
                    key = "ooc_jail";
                    break;
                case InCharacter:
                    key = "ic_prison";
                    break;
            }
            if(getPos != null && getWorld != null && getInterior != null) {
                float x = 0.0f, y = 0.0f, z = 0.0f;
                int world, interior;
                getPos.call(key, x, y, z);
                world = (Integer)getWorld.call(key);
                interior = (Integer)getInterior.call(key);
                player.setLocation(new Location(x, y, z, world, interior));

            }
            playerDao.insert(data);
        });

        this.node.registerHandler(PlayerUnJailEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            JailData data = e.getJailData();

            AmxCallable getPos = PawnFunc.getPublicMethod("Data_GetCoordinates");
            AmxCallable getWorld = PawnFunc.getPublicMethod("Data_GetVirtualWorld");
            AmxCallable getInterior = PawnFunc.getPublicMethod("Data_GetInterior");
            if(getPos != null && getWorld != null && getInterior != null) {
                ReferenceFloat refX = new ReferenceFloat(0f);
                ReferenceFloat refY = new ReferenceFloat(0f);
                ReferenceFloat refZ = new ReferenceFloat(0f);
                getPos.call("jail_discharge", refX, refY, refZ);
                int virWod = (Integer)getWorld.call("jail_discharge");
                int interior = (Integer)getInterior.call("jail_discharge");
                player.setLocation(new Location(refX.getValue(), refY.getValue(), refZ.getValue(), interior, virWod));
            }
            player.sendMessage(Color.NEWS, "Jûs esate paleidþiamas ið kalëjimo!");
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
