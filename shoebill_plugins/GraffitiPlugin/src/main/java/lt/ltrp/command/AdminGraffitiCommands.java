package lt.ltrp.command;

import lt.ltrp.GraffitiPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.GraffitiListDialog;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class AdminGraffitiCommands {

    private EventManager eventManager;
    private GraffitiPlugin plugin;

    public AdminGraffitiCommands(EventManager eventManager) {
        this.eventManager = eventManager;
        this.plugin = GraffitiPlugin.get(GraffitiPlugin.class);
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.isAdmin()) {
            return true;
        }
        return false;
    }

    public boolean allow(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        else if(plugin.isAllowedToPaint(target))
            player.sendErrorMessage(target.getName() + " jau turi leidimà pieðti grafiti, atimti já galite su /spray dissalow");
        else {
            plugin.allowToPaint(target, player);
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " suteikë jums galimybæ pieðti grafiti ribotam laikui. Galite pradëti su /spray create");
            LtrpPlayer.sendAdminMessage(player.getName() + " suteikë þaidëjui " + target.getName() + " leidimà pieðti grafiti.");
        }
        return true;
    }

    public boolean disallow(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        else if(!plugin.isAllowedToPaint(target))
            player.sendErrorMessage(target.getName() + " neturi leidimo pieðti grafiti, suteikti já galite su /spray allow");
        else {
            plugin.endPaintSession(target);
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " atëmë leidimà jums pieðti grafiti. Dabartinis grafiti buvo sunaikintas.");
            LtrpPlayer.sendAdminMessage(player.getName() + " atëmë leidimà pieðti grafiti ið  " + target.getName() + ".");
        }
        return true;
    }

    public boolean list(Player p) {
        LtrpPlayer player =  LtrpPlayer.get(p);
        GraffitiListDialog.create(player, eventManager)
            .show();
        return true;
    }

}
