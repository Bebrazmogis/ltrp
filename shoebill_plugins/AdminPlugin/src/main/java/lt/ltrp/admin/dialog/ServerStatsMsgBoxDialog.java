package lt.ltrp.dialog;

import lt.ltrp.LtrpGamemodeConstants;
import lt.ltrp.player.object.LtrpPlayer;
import lt.maze.streamer.StreamerPlugin;
import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.object.Server;
import net.gtaun.shoebill.object.World;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.25.
 */
public class ServerStatsMsgBoxDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager) {
        return MsgboxDialog.create(player, eventManager)
                .caption(LtrpGamemodeConstants.Name + " statistika")
                .buttonOk("Gerai")
                .line("Serverio versija: " + LtrpGamemodeConstants.Version)
                .line("Pickup skaièius: " + StreamerPlugin.getInstance().countItems(StreamerType.Pickup))
                .line("Objektø skaièius:" + StreamerPlugin.getInstance().countItems(StreamerType.Object))
                .line("Area skaièius: "+ StreamerPlugin.getInstance().countItems(StreamerType.Area))
                .line("Dinaminiø CP skaièius: " + StreamerPlugin.getInstance().countItems(StreamerType.Checkpoint))
                .line("3D Teksto etikeèiø skaièius: "+ StreamerPlugin.getInstance().countItems(StreamerType.Label))
                .line("Serverio codepage: " +Server.get().getServerCodepage())
                .line("Max. þaidëjø: " + Server.get().getMaxPlayers())
                .line("Slaptaþodis: "+ Server.get().getPassword())
                .line("Gravitacija: " + World.get().getGravity())
                .line("Oras: " + World.get().getWeather())
                .line("Shoebill versija: " + Shoebill.get().getVersion().getVersion())
                .line("Uþkrautø AMX kiekis: " + Shoebill.get().getAmxInstanceManager().getAmxInstances().size())
                .build();
    }
}
