package lt.ltrp.player;

import lt.ltrp.Util.AdminLog;
import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.player.event.PlayerAcceptPlayerQuestion;
import lt.ltrp.player.event.PlayerRejectPlayerQuestion;
import lt.ltrp.player.event.PlayerToggleModDutyEvent;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class ModeratorCommands extends Commands {

    private HashMap<String, Integer> commandLevels;

    private EventManager eventManager;
    private AdminController controller;

    public ModeratorCommands(AdminController controller, EventManager eventManager) {
        this.controller = controller;
        this.eventManager = eventManager;
        this.commandLevels = new HashMap<>();
        this.commandLevels.put("modhelp", 1);
        this.commandLevels.put("mduty", 1);
        this.commandLevels.put("dcpq", 1);
        this.commandLevels.put("acpq", 1);
        this.commandLevels.put("mkick", 1);
        this.commandLevels.put("mc", 1);
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        cmd = cmd.toLowerCase();
        if(commandLevels.containsKey(cmd)) {
            if(commandLevels.get(cmd) >= player.getModLevel() || player.isAdmin()) {
                return true;
            }
        }
        return false;
    }

    @Command
    public boolean modHelp(Player p) {
        p.sendMessage(Color.MODERATOR, "|____________________MODERATORIAUS SKYRIUS____________________|" );
        p.sendMessage(Color.MODERATOR, " /togq /mc /dcpq /acpq /mduty /mkick " );
        p.sendMessage(Color.MODERATOR, "|________________________________________________________________|" );
        return true;
    }

    @Command
    @CommandHelp("Pradeda/u�baigia moderatoriaus bud�jim�")
    public boolean mDuty(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(controller.getModeratorsOnDuty().contains(player)) {
            eventManager.dispatchEvent(new PlayerToggleModDutyEvent(player, false));
            LtrpPlayer.sendModMessage("Moderatorius " + player.getName() + " i�jung� aktyvaus b�d�jimo r��im�.");

        } else {
            eventManager.dispatchEvent(new PlayerToggleModDutyEvent(player, true));
            LtrpPlayer.sendModMessage("Moderatorius " + player.getName() + " �jung� aktyvaus b�d�jimo r��im�");
        }
        return true;
    }

    @Command
    @CommandHelp("Nusiun�ia �inut� visiems prisijungusiems administratoriams ir moderatoriams")
    public boolean mc(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(text == null) {
            return false;
        } else {
            LtrpPlayer.sendModMessage(String.format("[Mod] %s: %s", player.getName(), text));
        }
        return true;
    }

    @Command
    @CommandHelp("Atmeta �aid�jo pateikt� klausim�")
    public boolean dcpg(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Map<LtrpPlayer, String> questions = controller.getPendingPlayerQuestions();
        if(!questions.containsKey(target)) {
            player.sendErrorMessage("�is �aid�jas n�ra u�dav�s klausimo.");
        } else if(target.equals(player)) {
            player.sendErrorMessage("� savo klausimus atsakin�ti negalima.");
        } else {
            String question = questions.get(target);
            target.sendMessage(Color.MODERATOR, "D�mesio, J�s� pateiktas klausimas buvo atmestas moderatoriaus: " + player.getName() + ".");
            LtrpPlayer.sendModMessage("Moderatorius " + player.getName() + " atmet� �aid�jo " + target.getName() + " klausim�: " + question);
            AdminLog.log(player, String.format("Rejected users %s(uuid:%d) question:%s", target.getName(), target.getUUID(), question));
            eventManager.dispatchEvent(new PlayerRejectPlayerQuestion(player, target, question));
        }
        return true;
    }

    @Command
    @CommandHelp("Priema �aid�jo pateikt� klausim�")
    public boolean acpg(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Map<LtrpPlayer, String> questions = controller.getPendingPlayerQuestions();
        if(!questions.containsKey(target)) {
            player.sendErrorMessage("�is �aid�jas n�ra u�dav�s klausimo.");
        } else if(target.equals(player)) {
            player.sendErrorMessage("Savo klausimo priimti negalite.");
        } else {
            String question = questions.get(target);
            target.sendMessage(Color.MODERATOR, "D�mesio, J�s� pateikt� klausim� patvirtino moderatorius " + player.getName() + ", pasistengsime kuo grei�iau pateikti atsakym�.");
            LtrpPlayer.sendModMessage("Moderatorius " + player.getName() + " priem� �aid�jo " + target.getName() + " klausim�: " + question);
            AdminLog.log(player, String.format("Accepted users %s(uuid:%d) question:%s", target.getName(), target.getUUID(), question));
            eventManager.dispatchEvent(new PlayerAcceptPlayerQuestion(player, target, question));
        }
        return true;
    }

    @Command
    @CommandHelp("I�meta �aid�j� i� serverio")
    public boolean mKick(Player p,
                         @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "�aid�jo i�metimo prie�astis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(target.isNpc()) {
            player.sendErrorMessage("NPC i�mesti negalite!");
        } else if(player.getAdminLevel() < target.getAdminLevel()) {
            player.sendErrorMessage("Auk�tesnio lygio administratoriaus i�mesti negalite!");
        } else {
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("AdmCmd %s i�spyr� �aid�j� %s i� serverio, prie�astis: %s", player.getName(), target.getName(), reason));
            AdminLog.log(player, "Kicked user " + target.getName() + "(uuid=" + target.getUUID() + ") from server, reason: "+  reason);
            target.kick();
        }
        return true;
    }

}
