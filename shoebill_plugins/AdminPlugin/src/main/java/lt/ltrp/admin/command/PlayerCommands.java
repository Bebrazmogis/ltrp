package lt.ltrp.command;

import lt.ltrp.AdminController;
import lt.ltrp.AdminPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.Vote;
import lt.ltrp.event.player.PlayerAskQuestionEvent;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.06.03.
 */
public class PlayerCommands {

    private AdminPlugin adminPlugin;
    private Map<LtrpPlayer, Long> askqUseTimestamps;
    private EventManager eventManager;

    public PlayerCommands(AdminPlugin adminPlugin, EventManager eventManager) {
        this.adminPlugin = adminPlugin;
        this.eventManager = eventManager;
        this.askqUseTimestamps = new HashMap<>();

        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> askqUseTimestamps.remove(LtrpPlayer.get(e.getPlayer())));
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


    @Command
    @CommandHelp("Parodo prisijungusiø moderatoriø sàraðà")
    public boolean moderators(Player pl) {
        LtrpPlayer p = LtrpPlayer.get(pl);
        Collection<LtrpPlayer> onlineMods = LtrpPlayer.get()
                .stream()
                .filter(LtrpPlayer::isModerator)
                .collect(Collectors.toList());
        if(onlineMods.size() > 0) {
            p.sendMessage(Color.MODERATOR, "|_________________PRISIJUNGÆ MODERATORIAI_________________|");
            onlineMods.forEach(m -> {
                if(AdminController.get().getModeratorsOnDuty().contains(m))
                    p.sendMessage(Color.GREEN, String.format("Moderatorius %s (%s) ájungæs budinèio moderatoriaus rëþimà.", p.getName(), p.getForumName()));
                else
                    p.sendMessage(Color.LIGHTRED, String.format("Moderatorius %s (%s) iðjungæs budinèio moderatoriaus rëþimà.", p.getName(), p.getForumName()));
            });
        } else {
            p.sendErrorMessage("Nëra nei vieno prisingusio moderatoriaus!");
        }
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia klausimà administracijai")
    public boolean askq(Player p, @CommandParameter(name = "Klausimas")String message) {
        LtrpPlayer player = LtrpPlayer.get(p);
        long current = Instant.now().getEpochSecond();
        if(askqUseTimestamps.containsKey(player) && current - askqUseTimestamps.get(player) > 60) {
            player.sendErrorMessage("Klausti klausimus galite tik kas minutæ!");
        } else {
            askqUseTimestamps.put(player, current);
            eventManager.dispatchEvent(new PlayerAskQuestionEvent(player, message));
        }
        return true;
    }


    @Command
    @CommandHelp("Leidþia balsuoti teigiamai")
    public boolean taip(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Vote vote = adminPlugin.getCurrentVote();
        if(vote == null || vote.isEnded())
            player.sendErrorMessage("Ðiuo metu balsavimas nevyksta!");
        else if(vote.voted(player))
            player.sendErrorMessage("Jûs jau balsavote!");
        else {
            vote.addVote(player, true);
            player.sendMessage(Color.NEWS, "Jûsø balsas sëkmingai áskaièiuotas!");
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia balsuoti neigiamai")
    public boolean ne(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Vote vote = adminPlugin.getCurrentVote();
        if(vote == null || vote.isEnded())
            player.sendErrorMessage("Ðiuo metu balsavimas nevyksta!");
        else if(vote.voted(player))
            player.sendErrorMessage("Jûs jau balsavote!");
        else {
            vote.addVote(player, false);
            player.sendMessage(Color.NEWS, "Jûsø balsas sëkmingai áskaièiuotas!");
        }
        return true;
    }
}
