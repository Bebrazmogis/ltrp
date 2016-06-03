package lt.ltrp;

import lt.ltrp.constant.Currency;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.dao.PlayerWeaponDao;
import lt.ltrp.dao.impl.MySqlPlayerWeaponDaoImpl;
import lt.ltrp.dao.impl.SqlPlayerDaoImpl;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.data.SpawnData;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.BankAccount;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class PlayerPlugin extends Plugin{

    private static Logger logger;
    private static PlayerPlugin instance;

    private PlayerControllerImpl playerController;
    private PlayerCommandManager playerCommandManager;
    private GameTextStyleManager gameTextStyleManager;
    private EventManagerNode eventManagerNode;
    private PlayerWeaponDao playerWeaponDao;


    @Override
    public void onEnable() throws Throwable {
        instance = this;
        logger = getLogger();
        replaceTypeParsers();

        eventManagerNode = getEventManager().createChildNode();
        if(Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class) != null) {
            load();
        } else {
            eventManagerNode.registerHandler(ResourceEnableEvent.class, e -> {
                Resource resource = e.getResource();
                if(resource.getClass().equals(DatabasePlugin.class)) {
                    load();
                }
            });
        }
    }

    private void load() {
        DataSource dataSource = Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource();
        this.playerWeaponDao = new MySqlPlayerWeaponDaoImpl(dataSource);
        playerCommandManager = new PlayerCommandManager(eventManagerNode);
        PlayerDao playerDao = new SqlPlayerDaoImpl(dataSource);
        playerController = new PlayerControllerImpl(eventManagerNode, playerDao, playerCommandManager);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        this.gameTextStyleManager = new GameTextStyleManager(eventManagerNode);

        logger.info("Player plugin loaded");
    }

    @Override
    public void onDisable() throws Throwable {
        instance = null;
        playerController.destroy();
        playerCommandManager.destroy();
        gameTextStyleManager.destroy();
        logger.info("Player plugin shutting down...");
    }

    private static void replaceTypeParsers() {
        PlayerCommandManager.replaceTypeParser(LtrpPlayer.class, s -> {
            int id = Player.INVALID_ID;
            try {
                id = Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return null;
            }
            return LtrpPlayer.get(id);
        });
    }

    public PlayerWeaponDao getPlayerWeaponDao() {
        return playerWeaponDao;
    }

    public PlayerDao getPlayerDao() {
        return playerController.getPlayerDao();
    }

    public void showStats(LtrpPlayer showTo, LtrpPlayer player) {
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        SpawnData spawnData = SpawnPlugin.get(SpawnPlugin.class).getSpawnData(player);
        BankAccount bankAccount = BankPlugin.get(BankPlugin.class).getBankController().getAccount(player);
        PlayerJobData jobData = JobPlugin.get(JobPlugin.class).getJobData(player);
        showTo.sendMessage(Color.GREEN, "________________________________" + player.getCharName() + "___________________________");
        showTo.sendMessage(Color.WHITE, String.format("|VEIKËJAS| Lygis:[%d] Praþaista valandø:[%d] Amþius:[%d] Lytis:[%s] Tautybë:[%s]",
                player.getLevel(), player.getOnlineHours(), player.getAge(), player.getSex(), player.getNationality()));
        showTo.sendMessage(Color.LIGHTGREY, String.format("|VEIKËJAS| Mirèiø skaièius:[%d] Liga:[%s] Alkis:[%s]",
                player.getDeaths(), "-", player.getHunger()));
        showTo.sendMessage(Color.WHITE, String.format("|VEIKËJAS| Remëjo lygis:[%d] Áspëjimai:[%d] Atsiradimas:[%s] Gyvybës:[%.1f] Jëga:[%d]",
                0, penaltyPlugin.getWarnCount(player), spawnData.getType().name(), player.getHealth(), 0));
        if(bankAccount != null)
            showTo.sendMessage(Color.LIGHTGREY, String.format("|FINANSAI| Grynieji pinigai:[%d%c] Sàskaitos numeris[%s] Banko sàskaitoje:[%d$] Padëtas indëlis:[%d$] Palûkanø procentas: %d% ",
                    player.getMoney(), Currency.SYMBOL, bankAccount.getNumber(), bankAccount.getMoney(), bankAccount.getDeposit(), bankAccount.getInterest()));
        else
            showTo.sendMessage(Color.LIGHTGREY, "|FINANSAI| Grynieji pinigai:[%d%c] Bankos sàskaitos nëra.", player.getMoney(), Currency.SYMBOL);
        if(jobData != null)
            showTo.sendMessage(Color.WHITE, String.format("|DARBAS| Dirba:[%s] Kontraktas:[%d] Rangas darbe:[%s] Patirties taðkai darbe:[%d]",
                    jobData.getJob().getName(), jobData.getRemainingContract(), jobData.getJobRank().getName(), jobData.getXp()));
        else
            showTo.sendMessage(Color.WHITE, "|DARBAS| Jûs esate bedarbis");
        if(player.isAdmin() || player.isModerator())
            showTo.sendMessage(Color.LIGHTGREY, String.format("|ADMINISTRACIJA| Int:[%d], VirtW[%d], Administratoriaus lygis:[%d] Moderatoriaus lygis:[%d]",
                    player.getLocation().getInteriorId(), player.getLocation().getWorldId(), player.getLevel(), player.getModLevel()));
    }
}
