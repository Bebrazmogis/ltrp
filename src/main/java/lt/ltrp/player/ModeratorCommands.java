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
    @CommandHelp("Pradeda/uþbaigia moderatoriaus budëjimà")
    public boolean mDuty(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(controller.getModeratorsOnDuty().contains(player)) {
            eventManager.dispatchEvent(new PlayerToggleModDutyEvent(player, false));
            LtrpPlayer.sendModMessage("Moderatorius " + player.getName() + " iðjungë aktyvaus bûdëjimo rëþimà.");

        } else {
            eventManager.dispatchEvent(new PlayerToggleModDutyEvent(player, true));
            LtrpPlayer.sendModMessage("Moderatorius " + player.getName() + " ájungë aktyvaus bûdëjimo rëþimà ");
        }
        return true;
    }

    @Command
    @CommandHelp("Nusiunèia þinutæ visiems prisijungusiems administratoriams ir moderatoriams")
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
    @CommandHelp("Atmeta þaidëjo pateiktà klausimà")
    public boolean dcpg(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Map<LtrpPlayer, String> questions = controller.getPendingPlayerQuestions();
        if(!questions.containsKey(target)) {
            player.sendErrorMessage("Ðis þaidëjas nëra uþdavæs klausimo.");
        } else if(target.equals(player)) {
            player.sendErrorMessage("Á savo klausimus atsakinëti negalima.");
        } else {
            String question = questions.get(target);
            target.sendMessage(Color.MODERATOR, "Dëmesio, Jûsø pateiktas klausimas buvo atmestas moderatoriaus: " + player.getName() + ".");
            LtrpPlayer.sendModMessage("Moderatorius " + player.getName() + " atmetë þaidëjo " + target.getName() + " klausimà: " + question);
            AdminLog.log(player, String.format("Rejected users %s(uuid:%d) question:%s", target.getName(), target.getUUID(), question));
            eventManager.dispatchEvent(new PlayerRejectPlayerQuestion(player, target, question));
        }
        return true;
    }

    @Command
    @CommandHelp("Priema þaidëjo pateiktà klausimà")
    public boolean acpg(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Map<LtrpPlayer, String> questions = controller.getPendingPlayerQuestions();
        if(!questions.containsKey(target)) {
            player.sendErrorMessage("Ðis þaidëjas nëra uþdavæs klausimo.");
        } else if(target.equals(player)) {
            player.sendErrorMessage("Savo klausimo priimti negalite.");
        } else {
            String question = questions.get(target);
            target.sendMessage(Color.MODERATOR, "Dëmesio, Jûsø pateiktà klausimà patvirtino moderatorius " + player.getName() + ", pasistengsime kuo greièiau pateikti atsakymà.");
            LtrpPlayer.sendModMessage("Moderatorius " + player.getName() + " priemë þaidëjo " + target.getName() + " klausimà: " + question);
            AdminLog.log(player, String.format("Accepted users %s(uuid:%d) question:%s", target.getName(), target.getUUID(), question));
            eventManager.dispatchEvent(new PlayerAcceptPlayerQuestion(player, target, question));
        }
        return true;
    }

    @Command
    @CommandHelp("Iðmeta þaidëjà ið serverio")
    public boolean mKick(Player p,
                         @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Þaidëjo iðmetimo prieþastis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(target.isNpc()) {
            player.sendErrorMessage("NPC iðmesti negalite!");
        } else if(player.getAdminLevel() < target.getAdminLevel()) {
            player.sendErrorMessage("Aukðtesnio lygio administratoriaus iðmesti negalite!");
        } else {
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("AdmCmd %s iðspyrë þaidëjà %s ið serverio, prieþastis: %s", player.getName(), target.getName(), reason));
            AdminLog.log(player, "Kicked user " + target.getName() + "(uuid=" + target.getUUID() + ") from server, reason: "+  reason);
            target.kick();
        }
        return true;
    }

}
