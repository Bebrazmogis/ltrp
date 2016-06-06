package lt.ltrp.command;

import lt.ltrp.data.Color;
import lt.ltrp.event.player.PlayerSendPrivateMessageEvent;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.03.
 */
public class PlayerChatCommands extends Commands {

    private EventManager eventManager;

    public PlayerChatCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean bC(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            if(player.isLoggedIn()) {
                if(player.isMuted())
                    player.sendErrorMessage("Jums draud�iama kalb�ti.");
                else {
                    return true;
                }
            } else player.sendErrorMessage("Pra�ome prisijungti!");
        }
        return false;
    }

    @Command
    @CommandHelp("Pasako ka�k� tyliai pasirinktam �aid�jui")
    public boolean w(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                     @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player=  LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        else if(text == null)
            return false;
        else if(player.getDistanceToPlayer(target) > 2f)
            player.sendErrorMessage(target.getCharName() + " yra per toli kad gal�tum�te jam k� nors pasakyti � aus�.");
        else {
            String msg = String.format("%s �nab�d�damas sako: %s", player.getCharName(), text);
            player.sendMessage(Color.LIGHTRED, msg);
            target.sendMessage(Color.LIGHTRED, msg);
            player.sendActionMessage("pasilenk�s prie " + target.getCharName() + ", negirdimai su�nabd�a �od�ius ir atsitraukia.");
        }
        return true;
    }

    @Command
    @CommandHelp("Pasako ka�k� �mon�ms s�dintiems toj pa�ioje transporto priemon�je")
    public boolean cW(Player pp, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player=  LtrpPlayer.get(pp);
        if(text == null)
            return false;
        else if(!player.isInAnyVehicle())
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas transporto priemon�je.");
        else {
            Color color = new Color(0xD7DFF3AA);
            LtrpPlayer.get().stream().filter(p -> player.getVehicle().equals(p.getVehicle())).forEach(p -> {
                if(player.getState() == PlayerState.DRIVER)
                    p.sendMessage(color, String.format("Vairuotojas %s sako: %s", player.getCharName(), text));
                else
                    p.sendMessage(color, String.format("Pakeleivis %s sako: %s", player.getCharName(), text));
            });
        }
        return true;
    }

    @Command
    @CommandHelp("I�siun�ia OOC �inut� aplinkiniams �aid�jams")
    public boolean b(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(text == null)
            return false;
        else {
            player.sendFadeMessage(new Color(0xE6E6E6E6), String.format("(([ID: %d] {ca965a}%s{d6d6d6}: %s ))", player.getId(), player.getName(), text), 10f);
        }
        return true;
    }

    @Command
    @CommandHelp("I�siun�ia OOC �inut� � bendr� chat�")
    public boolean ooc(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(text == null)
            return false;
        else {
            LtrpPlayer.sendGlobalOocMessage(String.format("(( %s[%d] sako: %s ))", player.getName(), player.getId(), text));
        }
        return true;
    }

    @Command
    public boolean o(Player p, String text) {
        return ooc(p, text);
    }

    @Command
    @CommandHelp("Pasako ka�k� savo tautyb�s kalba")
    public boolean g(Player pp, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        if(text == null)
            return false;
        else {
            LtrpPlayer.get().stream().forEach(p -> {
                if(p.getNationality().equals(player.getNationality()))
                    p.sendMessage(new Color(0xE6E6E6E6), String.format("%s sako %s: %s", player.getCharName(), player.getNationality(), text));
                else
                    p.sendMessage(new Color(0xE6E6E6E6), player.getCharName() + " kalba nesuprantama kalba");
            });
        }
        return true;
    }

    @Command
    @CommandHelp("Nusiun�ia �aid�jui priva�i�, OOC �inut�")
    public boolean pm(Player player,
                      @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                      @CommandParameter(name = "�inut�s tekstas")String text) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(target == null) {
            p.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(target.getSettings().isPmDisabled()) {
            p.sendErrorMessage(target.getName() + " �aid�jas yra i�jung�s PM �inu�i� gavim�.");
        } else {
            target.playSound(1057);
            target.sendMessage(Color.PM_RECEIVED, String.format("(( Gauta P� nuo %s[ID:%d]: %s ))", p.getName(), p.getId(), text));
            p.sendMessage(Color.PM_SENT, String.format("(( P� i�si�sta %s[ID:%d]: %s ))", target.getName(), target.getId(), text));
            eventManager.dispatchEvent(new PlayerSendPrivateMessageEvent(p, target, text));
        }
        return true;
    }

    @Command
    public boolean s(Player p, @CommandParameter(name = "�inut�s tekstas")String text) {
        return shout(p, text);
    }

    @Command
    public boolean shout(Player p, @CommandParameter(name = "�inut�s tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(text == null)
            return false;
        else {
            player.sendFadeMessage(Color.WHITE, player.getCharName() + " �aukia:" + text, 15f);
        }
        return true;
    }


    @Command
    public boolean low(Player p, @CommandParameter(name = "�inut�s tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(text == null)
            return false;
        else {
            player.sendFadeMessage(Color.WHITE, player.getCharName() + " sako:[Tyliai]:" + text, 15f);
        }
        return true;
    }


}
