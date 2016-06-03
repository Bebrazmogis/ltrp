package lt.ltrp.command;

import lt.ltrp.AdminPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.06.03.
 */
public class PlayerCommands {

    private AdminPlugin adminPlugin;

    public PlayerCommands(AdminPlugin adminPlugin) {
        this.adminPlugin = adminPlugin;
    }

    @Command
    @CommandHelp("Parodo prisijungusiø administratoriø sàraðà")
    public boolean admins(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Collection<LtrpPlayer> admins = LtrpPlayer.get().stream().filter(LtrpPlayer::isAdmin).collect(Collectors.toList());
        if(admins.size() == 0)
            player.sendErrorMessage("Nëra nei vieno prisijungusio administratoriaus!");
        else {
            player.sendMessage(Color.TEAL, "---------------------------PRISIJUNGÆ ADMINISTRATORIAI----------------------------------");
            admins.forEach(a -> {
                if(adminPlugin.getAdminsOnDuty().contains(a))
                    player.sendMessage(Color.GREEN, String.format("[AdmLVL: %d] %s [%s] budintis statusas (/re).", player.getAdminLevel(), player.getName(), player.getForumName()));
                else
                    player.sendMessage(Color.LIGHTGREY, String.format("[AdmLVL: %d] %s [%s] nebudintis statusas (/re).", player.getAdminLevel(), player.getName(), player.getForumName()));
            });
            player.sendMessage(Color.TEAL, "----------------------------------------------------------------------------------------------");
        }
        return true;
    }

}
