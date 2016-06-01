package lt.ltrp;


import lt.ltrp.constant.Currency;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.data.Animation;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerFriskOffer;
import lt.ltrp.data.PlayerLicense;
import lt.ltrp.dialog.FightStyleDialog;
import lt.ltrp.dialog.PlayerDescriptionListDialog;
import lt.ltrp.dialog.PlayerDescriptionMsgBoxDialog;
import lt.ltrp.dialog.PlayerSettingsListDialog;
import lt.ltrp.event.player.PlayerAskQuestionEvent;
import lt.ltrp.event.player.PlayerSendPrivateMessageEvent;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.11.28.
 */
public class GeneralCommands {

    private EventManager eventManager;
    private Map<LtrpPlayer, Long> askqUseTimestamps;
    private HandlerEntry disconnectEntry;
    private PlayerPlugin playerPlugin;

    // Simple array to store name toggle status
    private boolean[] namesToggled;

    public GeneralCommands(EventManager eventManager) {
        this.namesToggled = new boolean[Player.getMaxPlayers()];
        this.eventManager = eventManager;
        this.askqUseTimestamps = new HashMap<>();
        disconnectEntry = eventManager.registerHandler(PlayerDisconnectEvent.class, e -> askqUseTimestamps.remove(LtrpPlayer.get(e.getPlayer())));
        playerPlugin = PlayerPlugin.get(PlayerPlugin.class);
    }

    @Override
    protected void finalize() {
        disconnectEntry.cancel();
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "GeneralCommands :: beforeCheck. Player logged in? " + player.isLoggedIn());
            return player.isLoggedIn();
        }
        return false;
    }


    @Command(name = "javainv")
    @CommandHelp("Leid�ia per�i�r�ti turimus daiktus")
    public boolean inv(Player player) {
        LtrpPlayer p = LtrpPlayer.get(player);
        Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "GeneralCommands :: inv called");
        p.sendMessage(p.getInventory().getName());
        p.getInventory().show(p);
        return true;
    }

    @Command
    @CommandHelp("Parodo j�s� turimas licenzijas pasirinktam �aid�jui")
    public boolean licenses(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player =LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli!");
        } else {
            target.sendMessage(Color.GREEN, String.format("|________%s licencijos________|", player.getCharName()));
            for(PlayerLicense license : player.getLicenses().get()) {
                if(license != null)
                    player.sendMessage(Color.WHITE, String.format("Licenzijos tipas:%s. Etapas: %s I�laikymo data: %s �sp�jim� skai�ius: %d",
                        license.getType().getName(), license.getStage() == 2 ? "Praktika" : "Teorija", license.getDateAquired(), license.getWarnings().length));
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia i�mokti naujus kovos stilius")
    public boolean learnfight(LtrpPlayer player) {
        if(player.getLocation().distance(PlayerController.GYM_LOCATION) > 10f) {
            player.sendErrorMessage("J�s turite b�ti sporto sal�je!");
        } else {
            FightStyleDialog.create(player, eventManager).show();
        }
        return true;
    }

    @Command
    @CommandHelp("At�aukia vykdom� veiksm�")
    public boolean stop(Player player) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(p.getCountdown() == null) {
            p.sendErrorMessage("J�s neatlikin�jate jokio veiksmo!");
        } else if(!p.getCountdown().isStoppable()) {
            p.sendErrorMessage("�io veiksmo at�aukti negalima!");
        } else {
            p.getCountdown().stop();
            p.sendMessage("Veiksmas s�kmingia at�auktas.");
            p.setCountdown(null);
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Esant komos b�senoje, leid�ia susitaikyti su mirtimi")
    public boolean die(Player player) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(!p.isInComa()) {
            p.sendErrorMessage("J�s neesate komos b�senoje!");
        } else if(p.getCountdown().getTimeleft() > 420) {
            p.sendErrorMessage("Dar nepra�jo 3 minut�s.");
        } else {
            p.setHealth(0f);
            p.clearAnimations(1);
            p.getCountdown().forceStop();
            return true;
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
    @CommandHelp("Atidaro �aidimo nustatym� meniu")
    public boolean settings(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerSettingsListDialog.create(player, eventManager, player.getSettings())
                .show();
        return true;
    }

    @Command
    @CommandHelp("I�siun�ia klausim� administracijai")
    public boolean askq(Player p, @CommandParameter(name = "Klausimas")String message) {
        LtrpPlayer player = LtrpPlayer.get(p);
        long current = Instant.now().getEpochSecond();
        if(askqUseTimestamps.containsKey(player) && current - askqUseTimestamps.get(player) > 60) {
            player.sendErrorMessage("Klausti klausimus galite tik kas minut�!");
        } else {
            askqUseTimestamps.put(player, current);
            eventManager.dispatchEvent(new PlayerAskQuestionEvent(player, message));
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo prisijungusi� moderatori� s�ra��")
    public boolean moderators(Player pl) {
        LtrpPlayer p = LtrpPlayer.get(pl);
        Collection<LtrpPlayer> onlineMods = LtrpPlayer.get()
                .stream()
                .filter(LtrpPlayer::isModerator)
                .collect(Collectors.toList());
        if(onlineMods.size() > 0) {
            p.sendMessage(Color.MODERATOR, "|_________________PRISIJUNG� MODERATORIAI_________________|");
            onlineMods.forEach(m -> {
                if(AdminController.get().getModeratorsOnDuty().contains(m))
                    p.sendMessage(Color.GREEN, String.format("Moderatorius %s (%s) �jung�s budin�io moderatoriaus r��im�.", p.getName(), p.getForumName()));
                else
                    p.sendMessage(Color.LIGHTRED, String.format("Moderatorius %s (%s) i�jung�s budin�io moderatoriaus r��im�.", p.getName(), p.getForumName()));
            });
        } else {
            p.sendErrorMessage("N�ra nei vieno prisingusio moderatoriaus!");
        }
        return true;
    }

    @Command
    @CommandHelp("Prideda pasirinkt� sum� � miesto biud�et�")
    public boolean charity(Player p, @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(amount > player.getMoney() || amount < 0)
            player.sendErrorMessage("J�s tiek pinig� neturite");
        else {
            player.giveMoney(-amount);
            player.sendMessage(Color.NEWS, "Par�m�te miesto biud�et� " + amount + Currency.SYMBOL);
            LtrpWorld.get().addMoney(amount);
        }
        return true;
    }


    @Command
    @CommandHelp("Perduoda pinig� kitam �aid�jui")
    public boolean pay(Player p, @CommandParameter(name = "�aid�jo ID/ Dalis vardo")LtrpPlayer target,
                       @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        else if(player.equals(target))
            player.sendErrorMessage("Sau duoti pinig� negalite.");
        else if(player.getLevel() < 2)
            player.sendErrorMessage("Pinigus perduoti galite tik nuo antro lygio.");
        else if(amount <= 0)
            player.sendErrorMessage("Suma turi b�ti didesn� u� 0.");
        else if(player.getDistanceToPlayer(target) > 5f)
            player.sendErrorMessage(target.getCharName() + " yra per toli.");
        else if(player.getMoney() < amount)
            player.sendErrorMessage("J�s neturite tiek pinig�.");
        else if(player.getIp().equals(target.getIp()) || player.getUcpId() == target.getUcpId())
            player.sendErrorMessage("Negalite perduoti pinig� savo v�ik�jui.");
        else {
            player.giveMoney(-amount);
            target.giveMoney(amount);
            player.applyAnimation(new Animation("DEALER", "shop_pay", false, 500));
            player.sendMessage(Color.NEWS, "S�kmiingai perdav�te " + amount + Currency.SYMBOL + " �aid�jui " + target.getName());
            target.sendMessage(Color.NEWS, "�aid�jas " + player.getName() + " jums dav� " + amount + Currency.SYMBOL);
            PlayerDao dao = playerPlugin.getPlayerDao();
            dao.update(player);
            dao.update(target);
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia redaguoti savo �aid�jo apra�ym�")
    public boolean setCard(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerDescriptionListDialog.create(player, eventManager)
                .show();
        return true;
    }

    @Command
    @CommandHelp("Leid�ia per�i�r�ti kito �aid�jo apra�ym�")
    public boolean cCard(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        if(target.getDescription() == null)
            player.sendErrorMessage(target.getName() + " neturi susik�r�s veik�jo apra�ymo!");
        else {
            player.sendMessage(Color.ACTION, String.format("%s (( %s ))", target.getDescription(), target.getCharName()));
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia per�i�r�ti kito �aid�jo apra�ym� GUI lentele")
    public boolean cCard(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Per�i�ros b�das: gui")String method) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null || method == null)
            return false;
        if(target.getDescription() == null)
            player.sendErrorMessage(target.getName() + " neturi susik�r�s veik�jo apra�ymo!");
        else if(!method.equalsIgnoreCase("gui"))
            return false;
        else {
            PlayerDescriptionMsgBoxDialog.create(player, eventManager, null, target)
                    .show();
        }
        return true;
    }

    @Command
    @CommandHelp("I�siun�ia veik�jo veiksmo �inut� aplinkiniems")
    public boolean me(Player p, @CommandParameter(name = "I�samus veiksmas")String action) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(action == null)
            return false;
        // TODO mute check
        else {
            player.sendActionMessage(action);
        }
        return true;
    }

    @Command
    @CommandHelp("I�siun�ia �aid�jo b�senos �inut� aplinkiniams")
    public boolean dO(Player p, @CommandParameter(name = "Veiksmas")String action) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(action == null)
            return false;
        else {
            player.sendStateMessage(action);
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo j�s� veik�jo duomenis")
    public boolean stats(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        playerPlugin.showStats(player, player);
        return true;
    }

    @Command
    @CommandHelp("Parodo �aid�jo ID pagal �ves� tekst�")
    public boolean id(Player pp, @CommandParameter(name = "�aid�jo vardo dalis")String text) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        if(text == null)
            return false;
        else {
            Collection<LtrpPlayer> matches = LtrpPlayer.get().stream()
                    .filter(p -> p.getName().contains(text) || p.getCharName().contains(text))
                    .collect(Collectors.toList());
            if(matches.size() == 0)
                player.sendErrorMessage("N�ra �aid�j� su pana�iais vardais");
            else {
                matches.forEach(m -> {
                    player.sendMessage(Color.WHITE, String.format("Surastas veik�jas (ID: %d) %s", m.getId(), m.getName()));
                });
            }
        }
        return true;
    }


    @Command
    @CommandHelp("Apie�ko pasirinkt� �aid�j�")
    public boolean frisk(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player == null || target == null)
            return false;
        Collection<PlayerFriskOffer> offers = target.getOffers(PlayerFriskOffer.class);
        boolean offered = offers.stream().filter(o -> o.getOfferedBy().equals(player)).findFirst().isPresent();
        if(player.getDistanceToPlayer(target) > 5f)
            player.sendErrorMessage(target.getCharName() + " yra per toli kad gal�tum�te j� apie�koti.");
        else if(offered)
            player.sendErrorMessage("J�s jau i�siunt�te si�lym� apie�koti �iam �aid�jui.");
        else {
            target.sendMessage(Color.WHITE, String.format("D�mesio, %s nori Jus apie�koti, jei leid�iat�s apie�komas ra�ykite /accept frisk %d", player.getName(), player.getId()));
            player.sendMessage(Color.WHITE, String.format("Veik�jas %s gavo pra�ym� leisti b�ti apie�komas J�s�, palaukite kol veik�jas atsakys. ", target.getName()));
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
    @CommandHelp("Paslepia/parodo kit� �aid�j� vardus")
    public boolean togNames(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        if(namesToggled[player.getId()]) {
            LtrpPlayer.get().forEach(p -> player.showNameTagForPlayer(p, false));
            player.sendMessage("[TOGNAMES] Kit� veik�j� vardai buvo pasl�pti. Nor�dami �jungti pakartokite komand�: /tognames. ");
        } else {
            LtrpPlayer.get().forEach(p -> player.showNameTagForPlayer(p, true));
            player.sendMessage("[TOGnames] Kit� veik�j� vard� rodymas buvo �jungtas..");
        }
        namesToggled[player.getId()] = !namesToggled[player.getId()];
        return true;
    }

    // TODO cmd:togooc
    // TODO cmd:togpm
    // TODO cmd:togadmin
    // TODO cmd:tognews
    // TODO cmd:o
    // TODO cmd:ad
}
