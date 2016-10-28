package lt.ltrp

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.PlayerData
import lt.ltrp.event.player.PlayerLogInEvent
import lt.ltrp.player.PlayerController
import lt.ltrp.player.dao.PlayerDao
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.HandlerPriority

class PlayerControllerImpl(eventManager: EventManager,
                           private var playerDao: PlayerDao,
                           private var playerContainer: PlayerContainer):
        PlayerController() {

    private val eventManager = eventManager.createChildNode()

    init {
        this.eventManager.registerHandler(PlayerLogInEvent::class.java, { playerContainer.playerList.add(it.player) })
        this.eventManager.registerHandler(PlayerDisconnectEvent::class.java, HandlerPriority.BOTTOM, { playerContainer.playerList.remove(LtrpPlayer.get(it.player)) })
    }

    override fun getPlayers(): Collection<LtrpPlayer> {
        return playerContainer.playerList
    }

    override fun getUsernameByUUID(uuid: Int): String? {
        return playerContainer.playerList.firstOrNull{ it.uuid == uuid }?.name ?: playerDao.getUsername(uuid)
    }

    override fun getData(uuid: Int): PlayerData? {
        return playerContainer.playerList.firstOrNull { it.uuid == uuid } ?: playerDao.get(uuid)
    }

    override fun getData(name: String): PlayerData? {
        return playerContainer.playerList.firstOrNull { it.name == name} ?: playerDao.get(name)
    }

    override fun update(player: LtrpPlayer) {
        playerDao.update(player)
    }

    fun destroy() {
        eventManager.cancelAll()
    }


/*
    protected static final int MINUTES_FOR_PAYDAY = 20;
    public static final Color DEFAULT_PLAYER_COLOR = new Color(0xFFFFFF00);
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);
    public static Collection<LtrpPlayer> playerList = new ArrayList<>();
*/

/*
    private void onPlayerPayday(int hour) {
        BankPlugin bankPlugin = Shoebill.get().getResourceManager().getPlugin(BankPlugin.class);
        LtrpPlayer.get().forEach(p -> {
            // fail-safe
            if(Player.get(p.getId()) == null)
                playerList.remove(p);

            Taxes taxes = LtrpWorld.get().getTaxes();
            int houseTax = (int) House.get().stream().filter(h -> h.getOwner() == p.getUUID()).count() * taxes.getHouseTax();
            int businessTax = (int) Business.get().stream().filter(b -> b.getOwner() == p.getUUID()).count() * taxes.getBusinessTax();
            int garageTax = (int) Garage.get().stream().filter(g -> g.getOwner() == p.getUUID()).count() * taxes.getGarageTax();
            //int vehicleTax = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class).getVehicleDao().getPlayerVehicleCount(p) * taxes.getVehicleTax();
            // TODO perkelti mokesčius į atskirą modulį
            int vehicleTax = 0;
            if(p.getMinutesOnlineSincePayday() > MINUTES_FOR_PAYDAY) {
                int paycheck = 0;

                PlayerJobData jobData = JobController.get().getJobData(p);
                if (jobData != null) {
                    paycheck = jobData.getJobRank().getSalary();
                } else {
                    paycheck = 100;
                }
                BankAccount bankAccount = bankPlugin.getBankController().getAccount(p);
                int totalTaxes = houseTax + businessTax + garageTax + vehicleTax;
                p.sendMessage(Color.LIGHTGREEN, "|______________ Los Santos banko ataskaita______________ |");
                p.sendMessage(Color.WHITE, String.format("| Gautas atlyginimas: %d%c | Papildomi mokesčiai: %d%c |", paycheck, Currency.SYMBOL,totalTaxes, Currency.SYMBOL));
                p.sendMessage(Color.WHITE, String.format("| Buvęs banko balansas: %d%c |", bankAccount.getMoney(), Currency.SYMBOL));
                p.sendMessage(Color.WHITE, String.format("| Galutinė gauta suma: %d%c |", paycheck, Currency.SYMBOL));
                bankAccount.addMoney(-totalTaxes);
                bankPlugin.getBankController().update(bankAccount);
                p.sendMessage(Color.WHITE, String.format("| Dabartinis banko balansas: %d%c |", bankAccount.getMoney(), Currency.SYMBOL));
                p.addTotalPaycheck(paycheck);
                p.sendMessage(Color.WHITE, String.format("| Sukauptas atlyginimas: %d%c", p.getTotalPaycheck(), Currency.SYMBOL));
                if (houseTax > 0)
                    p.sendMessage(Color.WHITE, String.format("| Mokestis už nekilnojama turtą: %d%c |", houseTax, Currency.SYMBOL));
                if (businessTax > 0)
                    p.sendMessage(Color.WHITE, String.format("| Verslo mokestis: %d%c |", businessTax, Currency.SYMBOL));
                if (vehicleTax > 0)
                    p.sendMessage(Color.WHITE, String.format("| Tr. Priemonių mokestis: %d%c |", vehicleTax, Currency.SYMBOL));

                p.sendGameText(1, 5000, " ~y~Mokesciai~n~~g~Alga");
                p.setOnlineHours(p.getOnlineHours() + 1);

                p.setMinutesOnlineSincePayday(0);
            } else {
                p.sendErrorMessage("Apgailestaujame, bet atlyginimo už šią valandą negausite, kadangi Jūs nebuvote prisijungęs pakankamai.");
            }
            playerDao.update(p);
        });
    }
*/
/*
    private void addPawnFunctions() {
        logger.info("PlayerController :: addPawnFunctions. Called.");
        managerNode.registerHandler(AmxLoadEvent.class, e-> {
            e.getAmxInstance().registerFunction("updatePlayerInfoText", params -> {
                LtrpPlayer player = LtrpPlayer.get(Player.get((Integer) params[0]));
                if(player != null && player.getInfoBox() != null) {
                    player.getInfoBox().update();
                }
                return player == null ? 0 : 1;
            }, Integer.class);

            e.getAmxInstance().registerFunction("isPlayerLoggedIn", params -> {
                LtrpPlayer player = LtrpPlayer.get(Player.get((Integer) params[0]));
                if (player != null) {
                    return player.isLoggedIn() ? 1 : 0;
                }
                return 0;
            }, Integer.class);

            e.getAmxInstance().registerFunction("saveAccount", params -> {
                LtrpPlayer player = LtrpPlayer.get(Player.get((Integer) params[0]));
                if (player != null) {
                    playerDao.update(player);
                }
                return 0;
            }, Integer.class);

            new GettersSetters(e.getAmxInstance());
           logger.info("PlayerController :: addPawnFunctions :: lambda. Function registered");
        });
        logger.info("PlayerController :: addPawnFunctions.Pawn functions added");



        managerNode.registerHandler(AmxUnloadEvent.class, e -> {
            e.getAmxInstance().unregisterFunction("isPlayerLoggedIn");
            e.getAmxInstance().unregisterFunction("updatePlayerInfoText");
            e.getAmxInstance().unregisterFunction("isDmvVehicle");

        });
    }
    */

}