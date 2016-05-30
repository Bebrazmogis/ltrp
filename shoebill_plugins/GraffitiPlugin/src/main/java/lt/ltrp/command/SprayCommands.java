package lt.ltrp.command;

import lt.ltrp.GraffitiPlugin;
import lt.ltrp.dialog.GraffitiColorListDialog;
import lt.ltrp.dialog.GraffitiFontListDialog;
import lt.ltrp.dialog.GraffitiNameInputDialog;
import lt.ltrp.object.Graffiti;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class SprayCommands {

    private GraffitiPlugin plugin;
    private EventManager eventManager;

    public SprayCommands(EventManager eventManager) {
        this.eventManager = eventManager;
        plugin = GraffitiPlugin.get(GraffitiPlugin.class);
    }

    public boolean create(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!plugin.isAllowedToPaint(player))
            player.sendErrorMessage("Jûs neturite leidimo pieðti grafiti, já gauti galite ið administratoriaus.");
        else if(plugin.isPaintingGraffiti(player))
            player.sendErrorMessage("Jûs jau pieðiate grafiti, pirmiausia pabaigite dabartiná.");
        else if(player.getLocation().getInteriorId() != 0 || player.getLocation().getWorldId() != 0)
            player.sendErrorMessage("Grafiti pieðti galima tik lauke.");
        else {
            Graffiti graffiti = plugin.startPainting(player);
            if(graffiti == null)
                player.sendErrorMessage("Jûsø leidimas pieðti grafiti nebegalioja.");
            else {
                graffiti.edit(player);
                player.sendMessage(graffiti.getColor().getColor(), "Pradëjote grafiti pieðimà. Naudokite /spray komandomis, " +
                        "pabaigai /spray save. Turite " + GraffitiPlugin.GRAFFITI_PAINT_PERMISSION_TIME / 1000  + " sekundes.");
            }
        }
        return true;
    }

    public boolean delete(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Graffiti graffiti = plugin.getPlayerGraffiti(player);
        if(graffiti == null)
            player.sendErrorMessage("Jûs neesate pradëjæs pieðti grafiti.");
        else {
            plugin.endPaintSession(player);
            graffiti.destroy();
            player.sendMessage(graffiti.getColor().getColor(), "Grafiti pieðimas baigtas, norëdami pieðti ið naujo turësite gauti naujà leidimà.");
        }
        return true;
    }

    public boolean pos(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Graffiti graffiti = plugin.getPlayerGraffiti(player);
        if(graffiti == null)
            player.sendErrorMessage("Jûs neesate pradëjæs pieðti grafiti.");
        else {
            graffiti.edit(player);
        }
        return true;
    }

    public boolean text(Player p){
        LtrpPlayer player = LtrpPlayer.get(p);
        Graffiti graffiti = plugin.getPlayerGraffiti(player);
        if(graffiti == null)
            player.sendErrorMessage("Jûs neesate pradëjæs pieðti grafiti.");
        else {
            GraffitiNameInputDialog.create(player, eventManager, graffiti).show();
        }
        return true;
    }

    public boolean font(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Graffiti graffiti = plugin.getPlayerGraffiti(player);
        if(graffiti == null)
            player.sendErrorMessage("Jûs neesate pradëjæs pieðti grafiti.");
        else {
            GraffitiFontListDialog.create(player, eventManager, graffiti)
                    .show();
        }
        return true;
    }

    public boolean color(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Graffiti graffiti = plugin.getPlayerGraffiti(player);
        if(graffiti == null)
            player.sendErrorMessage("Jûs neesate pradëjæs pieðti grafiti.");
        else {
            GraffitiColorListDialog.create(player, eventManager, graffiti)
                    .show();
        }
        return true;
    }

    public boolean save(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Graffiti graffiti = plugin.getPlayerGraffiti(player);
        if(graffiti == null)
            player.sendErrorMessage("Jûs neesate pradëjæs pieðti grafiti.");
        else {
            plugin.endPaintSession(player);
            int uuid = plugin.getGraffitiDao().insert(graffiti);
            graffiti.setUUID(uuid);
            player.sendMessage(graffiti.getColor().getColor(), "Grafiti sëkmingai iðsaugotas");
            LtrpPlayer.sendAdminMessage("Þaidëjas " + player.getName() + " iðsaugojo grafiti.");
        }
        return true;
    }
}
