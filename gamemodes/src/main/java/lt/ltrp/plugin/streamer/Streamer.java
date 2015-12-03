package lt.ltrp.plugin.streamer;


import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.plugin.streamer.event.player.PlayerEditDynamicObjectEvent;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;

import java.awt.*;

/**
 * Created by Justas on 2015.06.07.
 *
 */
public class Streamer {

    private EventManager eventManager;

    public Streamer(EventManager manager) {
        eventManager = manager;
    }

    // forward OnPlayerEditDynamicObject(playerid, objectid, response, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz);
    public void init() throws Throwable {
        Shoebill.get().getAmxInstanceManager().hookCallback("OnPlayerEditDynamicObject", amxCallEvent -> {
            Object[] parameters = amxCallEvent.getParameters();
            LtrpPlayer player = LtrpPlayer.get((Integer) parameters[0]);
            DynamicSampObject object = DynamicSampObject.findById((Integer)parameters[1]);
            PlayerEditDynamicObjectResponse response = null;
            int value = (int)parameters[2];
            switch(value) {
                case 0: response = PlayerEditDynamicObjectResponse.ResponseCancel;
                    break;
                case 1: response = PlayerEditDynamicObjectResponse.ResponseFinal;
                    break;
                case 2: response = PlayerEditDynamicObjectResponse.ResponseUpdate;
                    break;
            }
            Vector3D location = new Vector3D((float)parameters[3], (float)parameters[4], (float)parameters[5]);
            Vector3D rotation = new Vector3D((float)parameters[6], (float)parameters[7], (float)parameters[8]);

            if(player != null && object != null && location != null && rotation != null) {
                eventManager.dispatchEvent(new PlayerEditDynamicObjectEvent(player, object, response, location, rotation));
            }
        }, "iiiffffff");


    }

    public void uninit() throws Throwable {
        Shoebill.get().getAmxInstanceManager().unhookCallback("OnPlayerEditDynamicObject");
    }
}
