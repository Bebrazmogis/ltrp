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
                .line("Pickup skai�ius: " + StreamerPlugin.getInstance().countItems(StreamerType.Pickup))
                .line("Objekt� skai�ius:" + StreamerPlugin.getInstance().countItems(StreamerType.Object))
                .line("Area skai�ius: "+ StreamerPlugin.getInstance().countItems(StreamerType.Area))
                .line("Dinamini� CP skai�ius: " + StreamerPlugin.getInstance().countItems(StreamerType.Checkpoint))
                .line("3D Teksto etike�i� skai�ius: "+ StreamerPlugin.getInstance().countItems(StreamerType.Label))
                .line("Serverio codepage: " +Server.get().getServerCodepage())
                .line("Max. �aid�j�: " + Server.get().getMaxPlayers())
                .line("Slapta�odis: "+ Server.get().getPassword())
                .line("Gravitacija: " + World.get().getGravity())
                .line("Oras: " + World.get().getWeather())
                .line("Shoebill versija: " + Shoebill.get().getVersion().getVersion())
                .line("U�kraut� AMX kiekis: " + Shoebill.get().getAmxInstanceManager().getAmxInstances().size())
                .build();
    }
}
