package lt.ltrp.player;


import lt.ltrp.ActionMessenger;
import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.Animation;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.data.PlayerFriskOffer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static lt.ltrp.constant.LtrpColorKt.getMODERATOR;
import static lt.ltrp.constant.LtrpColorKt.getNEWS;

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
        disconnectEntry = eventManager.registerHandler(PlayerDisconnectEvent.class, e -> askqUseTimestamps.remove(LtrpPlayer.Companion.get(e.getPlayer())));
        playerPlugin = ResourceManager.get().getPlugin(PlayerPlugin.class);
    }

    @Override
    protected void finalize() {
        disconnectEntry.cancel();
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = playerPlugin.get(p);
        if(player != null) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "GeneralCommands :: beforeCheck. Player logged in? " + player.isLoggedIn());
            return player.isLoggedIn();
        }
        return false;
    }

/*
    @Command(name = "javainv")
    @CommandHelp("Leidþia perþiûrëti turimus daiktus")
    public boolean inv(Player player) {
        LtrpPlayer p = LtrpPlayer.Companion.get(player);
        Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "GeneralCommands :: inv called");
        p.sendMessage(p.getInventory().getName());
        p.getInventory().show(player);
        return true;
    }

    @Command
    @CommandHelp("Leidþia iðmokti naujus kovos stilius")
    public boolean learnfight(Player player) {
        LtrpPlayer p =LtrpPlayer.Companion.get(player);
        if(player.getLocation().distance(PlayerController.Companion.getGYM_LOCATION()) > 10f) {
            p.sendErrorMessage("Jûs turite bûti sporto salëje!");
        } else {
            FightStyleDialog.create(p, eventManager).show();
        }
        return true;
    }
    */
/*
    @Command
    @CommandHelp("Atðaukia vykdomà veiksmà")
    public boolean stop(Player player) {
        LtrpPlayer p =LtrpPlayer.Companion.get(player);
        if(p.getCountdown() == null) {
            p.sendErrorMessage("Jûs neatlikinëjate jokio veiksmo!");
        } else if(!p.getCountdown().isStoppable()) {
            p.sendErrorMessage("Ðio veiksmo atðaukti negalima!");
        } else {
            p.getCountdown().stop();
            p.sendMessage("Veiksmas sëkmingia atðauktas.");
            p.setCountdown(null);
            return true;
        }
        return true;
    }
// TODO
    @Command
    @CommandHelp("Esant komos bûsenoje, leidþia susitaikyti su mirtimi")
    public boolean die(Player player) {
        LtrpPlayer p =LtrpPlayer.Companion.get(player);
        if(!p.isInComa()) {
            p.sendErrorMessage("Jûs neesate komos bûsenoje!");
        } else if(p.getCountdown().getTimeleft() > 420) {
            p.sendErrorMessage("Dar nepraëjo 3 minutës.");
        } else {
            player.setHealth(0f);
            p.clearAnimations();
            p.getCountdown().forceStop();
            return true;
        }
        return true;
    }*/

/*
    @Command
    @CommandHelp("Iðsiunèia klausimà administracijai")
    public boolean askq(Player p, @CommandParameter(name = "Klausimas")String message) {
        LtrpPlayer player = playerPlugin.get(p);
        long current = Instant.now().getEpochSecond();
        if(askqUseTimestamps.containsKey(player) && current - askqUseTimestamps.get(player) > 60) {
            player.sendErrorMessage("Klausti klausimus galite tik kas minutæ!");
        } else {
            askqUseTimestamps.put(player, current);
            eventManager.dispatchEvent(new PlayerAskQuestionEvent(player, message));
        }
        return true;
    }
*/
/*
    @Command
    @CommandHelp("Parodo prisijungusiø moderatoriø sàraðà")
    public boolean moderators(Player pl) {
        LtrpPlayer p = LtrpPlayer.Companion.get(pl);
        Collection<LtrpPlayer> onlineMods = LtrpPlayer.Companion.get()
                .stream()
                .filter(LtrpPlayer::isModerator)
                .collect(Collectors.toList());
        if(onlineMods.size() > 0) {
            p.sendMessage(getMODERATOR(Color.Companion), "|_________________PRISIJUNGÆ MODERATORIAI_________________|");
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
*/
    @Command
    @CommandHelp("Prideda pasirinktà sumà á miesto biudþetà")
    public boolean charity(Player p, @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = playerPlugin.get(p);
        if(amount > p.getMoney() || amount < 0)
            player.sendErrorMessage("Jûs tiek pinigø neturite");
        else {
            p.giveMoney(-amount);
            player.sendMessage(getNEWS(Color.Companion), "Parëmëte miesto biudþetà " + amount + Currency.SYMBOL);
            LtrpWorld.get().addMoney(amount);
        }
        return true;
    }


    @Command
    @CommandHelp("Perduoda pinigø kitam þaidëjui")
    public boolean pay(Player p, @CommandParameter(name = "Þaidëjo ID/ Dalis vardo")LtrpPlayer target,
                       @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = playerPlugin.get(p);
        if(target == null)
            return false;
        else if(player.equals(target))
            player.sendErrorMessage("Sau duoti pinigø negalite.");
        else if(player.getLevel() < 2)
            player.sendErrorMessage("Pinigus perduoti galite tik nuo antro lygio.");
        else if(amount <= 0)
            player.sendErrorMessage("Suma turi bûti didesnë uþ 0.");
        else if(player.getPlayer().getLocation().distance(target.getPlayer().getLocation()) > 5f)
            player.sendErrorMessage(target.getCharName() + " yra per toli.");
        else if(player.getMoney() < amount)
            player.sendErrorMessage("Jûs neturite tiek pinigø.");
        else if(p.getIp().equals(target.getPlayer().getIp()) || player.getUcpId() == target.getUcpId())
            player.sendErrorMessage("Negalite perduoti pinigø savo vëikëjui.");
        else {
            player.setMoney(player.getMoney()-amount);
            target.setMoney(player.getMoney()+amount);
            player.applyAnimation(new Animation("DEALER", "shop_pay", false, 500));
            player.sendMessage(getNEWS(Color.Companion), "Sëkmiingai perdavëte " + amount + Currency.SYMBOL + " þaidëjui " + target.getName());
            target.sendMessage(getNEWS(Color.Companion), "Þaidëjas " + player.getName() + " jums davë " + amount + Currency.SYMBOL);
            PlayerController.instance.update(player);
            PlayerController.instance.update(target);
        }
        return true;
    }


    @Command
    @CommandHelp("Leidþia perþiûrëti kito þaidëjo apraðymà")
    public boolean cCard(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = playerPlugin.get(p);
        if(target == null)
            return false;
        if(target.getDescription() == null)
            player.sendErrorMessage(target.getName() + " neturi susikûræs veikëjo apraðymo!");
        else {
            player.sendMessage(ActionMessenger.Companion.getDEFAULT_COLOR(), String.format("%s (( %s ))", target.getDescription(), target.getCharName()));
        }
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia veikëjo veiksmo þinutæ aplinkiniems")
    public boolean me(Player p, @CommandParameter(name = "Iðsamus veiksmas")String action) {
        LtrpPlayer player = playerPlugin.get(p);
        if(action == null)
            return false;
        // TODO mute check
        else {
            player.sendActionMessage(action);
        }
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia þaidëjo bûsenos þinutæ aplinkiniams")
    public boolean dO(Player p, @CommandParameter(name = "Veiksmas")String action) {
        LtrpPlayer player = playerPlugin.get(p);
        if(action == null)
            return false;
        else {
            player.sendStateMessage(action);
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo þaidëjo ID pagal ávesà tekstà")
    public boolean id(Player pp, @CommandParameter(name = "Þaidëjo vardo dalis")String text) {
        LtrpPlayer player = LtrpPlayer.Companion.get(pp);
        if(text == null)
            return false;
        else {
            Collection<LtrpPlayer> matches = LtrpPlayer.Companion.get().stream()
                    .filter(p -> p.getName().contains(text) || p.getCharName().contains(text))
                    .collect(Collectors.toList());
            if(matches.size() == 0)
                player.sendErrorMessage("Nëra þaidëjø su panaðiais vardais");
            else {
                matches.forEach(m -> {
                    player.sendMessage(Color.WHITE, String.format("Surastas veikëjas (ID: %d) %s", m.getPlayer().getId(), m.getName()));
                });
            }
        }
        return true;
    }


    @Command
    @CommandHelp("Apieðko pasirinktà þaidëjà")
    public boolean frisk(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = playerPlugin.get(p);
        if(player == null || target == null)
            return false;
        Collection<PlayerFriskOffer> offers = target.getOffers(PlayerFriskOffer.class);
        boolean offered = offers.stream().filter(o -> o.getOfferedBy().equals(player)).findFirst().isPresent();
        if(player.getPlayer().getLocation().distance(target.getPlayer().getLocation()) > 5f)
            player.sendErrorMessage(target.getCharName() + " yra per toli kad galëtumëte já apieðkoti.");
        else if(offered)
            player.sendErrorMessage("Jûs jau iðsiuntëte siûlymà apieðkoti ðiam þaidëjui.");
        else {
            target.sendMessage(Color.WHITE, String.format("Dëmesio, %s nori Jus apieðkoti, jei leidþiatës apieðkomas raðykite /accept frisk %d", player.getName(), player.getPlayer().getId()));
            player.sendMessage(Color.WHITE, String.format("Veikëjas %s gavo praðymà leisti bøti apieðkomas Jûsø, palaukite kol veikëjas atsakys. ", target.getName()));
        }
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia OOC þinutæ aplinkiniams þaidëjams")
    public boolean b(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = playerPlugin.get(p);
        if(text == null)
            return false;
        else {
            player.sendFadeMessage(new Color(0xE6E6E6E6), String.format("(([ID: %d] {ca965a}%s{d6d6d6}: %s ))", player.getPlayer().getId(), player.getName(), text), 10f);
        }
        return true;
    }

    @Command
    @CommandHelp("Paslepia/parodo kitø þaidëjø vardus")
    public boolean togNames(Player pp) {
        LtrpPlayer player = LtrpPlayer.Companion.get(pp);
        if(namesToggled[pp.getId()]) {
            LtrpPlayer.Companion.get().forEach(p -> pp.showNameTagForPlayer(pp, false));
            player.sendMessage("[TOGNAMES] Kitø veikëjø vardai buvo paslëpti. Norëdami ájungti pakartokite komandà: /tognames. ");
        } else {
            LtrpPlayer.Companion.get().forEach(p -> pp.showNameTagForPlayer(pp, true));
            player.sendMessage("[TOGnames] Kitø veikëjø vardø rodymas buvo ájungtas..");
        }
        namesToggled[pp.getId()] = !namesToggled[pp.getId()];
        return true;
    }

    @Command
    @CommandHelp("Parodo jûsø paskutinës transporto priemonës ID")
    public boolean oldCar(Player pp) {
        LtrpPlayer player=  LtrpPlayer.Companion.get(pp);
        if(player.getLastUsedVehicle() == null)
            player.sendErrorMessage("Nuo prisijungimo, jûs dar nesedëjote jokioje transporto priemonëje.");
        else
            player.sendMessage(getNEWS(Color.Companion), "Paskutinës naudotos transporto priemonës ID yra " + player.getLastUsedVehicle().getId());
        return true;
    }

    @Command
    @CommandHelp("Parodo pasirinktam þaidëjui jûsø asmens tapatybës dokumentà")
    public boolean sId(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player=  playerPlugin.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        else if(player.getPlayer().getLocation().distance(target.getPlayer().getLocation()) > 2f)
            player.sendErrorMessage(target.getCharName() + " yra per toli kad galëtumëte jam parodyti savo asmens tapatybës kortelæ.");
        else {
            target.sendMessage(Color.GREEN, "|______________" + player.getName() + "______________|");
            target.sendMessage(Color.WHITE, String.format("*| Vardas: %s Pavardë: %s", player.getFirstName(), player.getLastName()));
            target.sendMessage(Color.WHITE, String.format("*| Gimimo metai: %d Metai: %d", Calendar.getInstance().get(Calendar.YEAR) - player.getAge(), player.getAge()));
            target.sendMessage(Color.WHITE, String.format("*| Tautybë: %s", player.getOrigin()));
            target.sendMessage(Color.WHITE, String.format("*| Asmens kodas: %d000000%d%d", player.getUcpId(), player.getUUID(), player.getAge()));
            player.sendActionMessage("parodo savo asmens dokumentà " + target.getCharName());
        }
        return true;
    }



    // TODO cmd:togooc
    // TODO cmd:togpm
    // TODO cmd:togadmin
    // TODO cmd:tognews
    // TODO cmd:o
}
