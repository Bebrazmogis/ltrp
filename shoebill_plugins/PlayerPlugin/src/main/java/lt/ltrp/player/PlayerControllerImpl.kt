package lt.ltrp.player

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.player.dao.PlayerDao
import lt.ltrp.player.event.PlayerLogInEvent
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
        this.eventManager.registerHandler(PlayerDisconnectEvent::class.java, HandlerPriority.BOTTOM, { playerContainer.playerList.remove(LtrpPlayer.Companion.get(it.player)) })
    }

    override fun getPlayers(): Collection<LtrpPlayer> {
        return playerContainer.playerList
    }

    override fun getUsernameByUUID(uuid: Int): String? {
        return playerContainer.playerList.firstOrNull{ it.UUID == uuid }?.name ?: playerDao.getUsername(uuid)
    }

    override fun getData(uuid: Int): PlayerData? {
        return playerContainer.playerList.firstOrNull { it.UUID == uuid } ?: playerDao.get(uuid)
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
    private void addPawnFunctions() {
        logger.info("PlayerController :: addPawnFunctions. Called.");
        managerNode.registerHandler(AmxLoadEvent.class, e-> {
            e.getAmxInstance().registerFunction("updatePlayerInfoText", params -> {
                LtrpPlayer player = LtrpPlayer.Companion.get(Player.get((Integer) params[0]));
                if(player != null && player.getInfoBox() != null) {
                    player.getInfoBox().update();
                }
                return player == null ? 0 : 1;
            }, Integer.class);

            e.getAmxInstance().registerFunction("isPlayerLoggedIn", params -> {
                LtrpPlayer player = LtrpPlayer.Companion.get(Player.get((Integer) params[0]));
                if (player != null) {
                    return player.isLoggedIn() ? 1 : 0;
                }
                return 0;
            }, Integer.class);

            e.getAmxInstance().registerFunction("saveAccount", params -> {
                LtrpPlayer player = LtrpPlayer.Companion.get(Player.get((Integer) params[0]));
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