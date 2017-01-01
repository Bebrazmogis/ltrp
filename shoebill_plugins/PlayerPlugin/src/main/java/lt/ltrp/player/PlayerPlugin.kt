package lt.ltrp.player

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.`object`.PlayerCountdown
import lt.ltrp.player.`object`.impl.LtrpPlayerImpl
import lt.ltrp.player.command.PlayerAcceptOffers
import lt.ltrp.player.command.PlayerAnimationCommands
import lt.ltrp.player.command.PlayerChatCommands
import lt.ltrp.player.dao.impl.MySqlPlayerWeaponDaoImpl
import lt.ltrp.player.dao.impl.SqlPlayerDaoImpl
import lt.ltrp.data.Animation
import lt.ltrp.player.event.PlayerOfferExpireEvent
import lt.ltrp.player.event.PlayerDisconnectEvent
import lt.ltrp.player.event.PlayerLogInEvent
import lt.ltrp.player.dao.PlayerDao
import lt.ltrp.player.dao.PlayerWeaponDao
import lt.ltrp.player.util.PlayerLog
import lt.ltrp.resource.DependentPlugin
import lt.ltrp.spawn.event.PlayerFirstSpawnEvent
import lt.maze.DatabasePlugin
import lt.maze.streamer.StreamerPlugin
import lt.maze.streamer.constant.StreamerType
import net.gtaun.shoebill.common.command.CommandGroup
import net.gtaun.shoebill.`entities`.Player
import net.gtaun.shoebill.entities.PlayerKeyState
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.shoebill.common.timers.TemporaryTimer
import net.gtaun.shoebill.constant.*
import net.gtaun.shoebill.data.AngledLocation
import net.gtaun.shoebill.data.WeaponData
import net.gtaun.shoebill.entities.TimerCallback
import net.gtaun.shoebill.event.player.*
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.function.Function

/**
 * @author Bebras
 *         2016.04.07.
 */
@ShoebillMain("Player plugin", "Bebras")
class PlayerPlugin: DependentPlugin() {

    private lateinit var eventManagerNode: EventManagerNode
    private lateinit var playerDao: PlayerDao
    private lateinit var playerWeaponDao: PlayerWeaponDao
    lateinit var playerController: PlayerControllerImpl
    private lateinit var playerCommandManager: PlayerCommandManager
    private val playerContainer = PlayerContainer

    init {
        addDependency(DatabasePlugin::class)
    }

    override fun onDependenciesLoaded() {
        eventManagerNode = eventManager.createChildNode()
        replaceTypeParsers()

        val dataSource = ResourceManager.get().getPlugin(DatabasePlugin::class.java)!!.dataSource
        playerDao = SqlPlayerDaoImpl(dataSource, eventManager)
        playerWeaponDao = MySqlPlayerWeaponDaoImpl(dataSource)
        PlayerLog.init(dataSource, eventManager)
        playerController = PlayerControllerImpl(eventManager, playerDao, playerContainer)

        registerEventHandlers()
        registerCommands()
        logger.info("Player plugin loaded");
    }

    private fun registerCommands() {
        playerCommandManager = PlayerCommandManager(eventManager)
        playerCommandManager.registerCommands(GeneralCommands(eventManager))
        playerCommandManager.registerCommands(PlayerAcceptOffers())
        playerCommandManager.registerCommands(PlayerChatCommands(eventManager))
        playerCommandManager.registerCommands(PlayerAnimationCommands())
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL)
    }

    private fun registerEventHandlers() {
        eventManager.registerHandler(PlayerConnectEvent::class.java, { onPlayerConnect(get(it.player)!!) })
        eventManager.registerHandler(PlayerLogInEvent::class.java, { onPlayerLogIn(it.player, it.failedAttempts) })
        eventManager.registerHandler(PlayerDisconnectEvent::class.java, { onPlayerDisconnect(it.player, it.reason) })
        eventManager.registerHandler(PlayerRequestClassEvent::class.java, { onPlayerRequestClass(get(it.player), it) })
        eventManager.registerHandler(PlayerSpawnEvent::class.java, { onPlayerSpawn(LtrpPlayer.get(it.player)!!) })
        eventManager.registerHandler(PlayerFirstSpawnEvent::class.java, { onPlayerFirstSpawn(it.player) })
        eventManager.registerHandler(PlayerDeathEvent::class.java, { onPlayerDeath(LtrpPlayer.get(it.player)!!, LtrpPlayer.get(it.killer!!)!!, it.reason) })
        eventManager.registerHandler(PlayerWeaponShotEvent::class.java, { onPlayerWeaponShot(LtrpPlayer.get(it.player)!!, it.weapon) })
        eventManager.registerHandler(PlayerKeyStateChangeEvent::class.java, { onPlayerKeyStateChange(LtrpPlayer.get(it.player)!!, it.oldState, it.player.keyState) })
        eventManager.registerHandler(PlayerStreamInEvent::class.java, { onPlayerStreamIn(LtrpPlayer.get(it.player)!!, LtrpPlayer.get(it.forPlayer)!!) })
        eventManager.registerHandler(PlayerOfferExpireEvent::class.java, HandlerPriority.BOTTOM, {
            (it.player as LtrpPlayerImpl).offers.remove(it.offer)
        })

        // In the end... we play an animation if the player has set it
        eventManager.registerHandler(PlayerTextEvent::class.java, HandlerPriority.BOTTOM, { e ->
            val player = get(e.player)
            if(player != null) {
                player.applyAnimation(player.talkStyle.animation)
                val time = 200 * e.text.length
                TemporaryTimer.create(time, 1, TimerCallback { i ->
                    if(player.animation == player.talkStyle.animation)player.clearAnimations()
                })
            }
        })
    }
    /*
    private void load() {
        this.playerWeaponDao = new MySqlPlayerWeaponDaoImpl(dataSource);
        playerCommandManager = new PlayerCommandManager(eventManagerNode);
        playerSettingsDao = new SqlPlayerSettingsDaoImpl(dataSource, eventManagerNode);

        playerController = new PlayerControllerImpl(eventManagerNode, playerDao, playerSettingsDao, playerCommandManager);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        this.gameTextStyleManager = new GameTextStyleManager(eventManagerNode);
    }*/

    override fun onDisable() {
        eventManager.cancelAll()
        eventManager.destroy()
        playerCommandManager.destroy()
        playerController.destroy()
        //gameTextStyleManager.destroy();
        logger.info("Player plugin shutting down...");
    }

    fun get(player: Player): LtrpPlayer? {
        return LtrpPlayer.Companion.get(player);
    }

    private fun replaceTypeParsers() {
        CommandGroup.replaceTypeParser(LtrpPlayer::class.java, Function {
            var id = Player.INVALID_ID
            try {
                id = Integer.parseInt(it)
            } catch (e: NumberFormatException) {
                Unit
            }
            LtrpPlayer.get(id)
        })
    }

    private fun onPlayerConnect(player: LtrpPlayer) {
        // Various options and settings
        player.player.color = net.gtaun.shoebill.data.Color.WHITE // Make the users radar blip invisible
    }

    private fun onPlayerLogIn(player: LtrpPlayer, failedAttemps: Int) {
        player.sendMessage("{FFFFFF}Sveikiname sugráþus, Jûs prisijungëte su veikëju " + player.name + ". Sëkmës serveryje!")

        player.player.sendGameText(5000, 1, "~w~Sveikas ~n~~h~~g~" + player.name)
        player.lastLogin = LocalDateTime.now()
        playerDao.updateLastLogin(player)

        // TODO perhaps call Pawn?
    }

    private fun onPlayerText(player: LtrpPlayer, text: String, event: PlayerTextEvent) {
        if(player.isMuted) {
            player.sendErrorMessage("Jums uþdrausta kalbëti!");
        } else {
            var msg = ""
            if(!player.player.isInAnyVehicle) {
                msg = String.format("%s sako: %s", player.charName, text)
            } else {
               /* val vehicle = LtrpVehicle.getByVehicle(player.vehicle)
                when(vehicle.isSeatWindowOpen(player.vehicleSeat)) {
                    true -> msg = String.format("(Langas Atidarytas) %s sako: %s", player.charName, text)
                    false -> msg = String.format("(Langas uþdarytas) %s sako: %s", player.charName, text)

                }*/
            }
            player.sendFadeMessage(net.gtaun.shoebill.data.Color.WHITE, msg, 20.0f)
            return
        }
        event.interrupt()
    }

    private fun onPlayerDisconnect(p: LtrpPlayer, reason: DisconnectReason) {
        var leaveMessage = ""
        when(reason) {
            DisconnectReason.LEFT -> leaveMessage = String.format("%s paliko serverá (Klientas atsijungë).", p.name)
            DisconnectReason.KICK -> leaveMessage = String.format("%s paliko serverá (Klientas iðmestas).", p.name)
            else -> leaveMessage = String.format("%s paliko serverá (ávyko kliento klaida/nutrøko ryðys).", p.name)
        }
        p.sendFadeMessage(net.gtaun.shoebill.data.Color.WHITE, leaveMessage, 20f)

        // If he disconnects while in coma, automatically we add a death
        if(p.isInComa) {
            p.deaths++
        }

        p.player.attach.get().forEach {
            if(it.isUsed)
                it.remove()
        }
        playerDao.update(p)
        //p.destroy()
    }

    private fun onPlayerRequestClass(player: LtrpPlayer?, e: PlayerRequestClassEvent) {
        if(player != null && player.isLoggedIn) {
            player.player.spawn()
        } else {
            e.player.sendMessage(net.gtaun.shoebill.data.Color.RED, "Jûs neesate prisijungæs.")
        }
        e.disallow()
    }

    private fun onPlayerFirstSpawn(ltrpPlayer: LtrpPlayer) {
        val player = ltrpPlayer.player
        setDefaultWeaponSkillLevel(ltrpPlayer)
        //preloadAnimLimbs(player);
        player.team = Player.NO_TEAM
        player.score = ltrpPlayer.level
    }

    private fun onPlayerSpawn(ltrpPlayer: LtrpPlayer) {
        val player = ltrpPlayer.player
        if(ltrpPlayer.isLoggedIn) {
            player.setCameraBehind()
            // Another fail-safe
            if(ltrpPlayer.level == 0) {
                ltrpPlayer.level = 1
                logger.error(String.format("User %s(UDI:%d) level is 0.", player.name, ltrpPlayer.UUID))
            }

            if(ltrpPlayer.isMasked) {
                ltrpPlayer.isMasked = false
            }

            if(ltrpPlayer.isInComa) {
                ltrpPlayer.applyAnimation(Animation("CRACK", "crckdeth2", true, 0))
                // We start the coma countdown
                // TODO
                /*player.countdown = PlayerCountdown.create(player, 600, true, { p, success ->
                    if (success)
                        player.health = 0f
                }, false, "~w~Iki mirties")*/
            }
            StreamerPlugin.getInstance().update(player, StreamerType.Object);
        }
    }

    private fun onPlayerDeath(ltrpPlayer: LtrpPlayer, killer: LtrpPlayer, reason: WeaponModel) {
        val player = ltrpPlayer.player
       /* // If he was doing something, he isn't doing it anymore
        if(player.countdown != null) {
            player.countdown.forceStop()
        }*/

        player.cancelEdit()
        player.cancelSelectTextDraw()
        player.cancelDialog()

        logger.debug("death location:" + player.location + " death skin: "+ player.skin);
        // We put him in a coma
        if(!ltrpPlayer.isInComa) {
            ltrpPlayer.isInComa = true

            player.setSpawnInfo(player.location, player.skin, Player.NO_TEAM, WeaponData(), WeaponData(), WeaponData())

            // Actual death
        } else {
            player.setSpawnInfo(AngledLocation(180f, 600f, 10f, 0f), player.skin, Player.NO_TEAM, WeaponData(), WeaponData(), WeaponData());
            // TODO pakeisti koordinates ligoninës
            ltrpPlayer.isInComa = false
            ltrpPlayer.deaths++
        }

        playerDao.update(ltrpPlayer)
    }

    private fun onPlayerWeaponShot(player: LtrpPlayer, model: WeaponModel) {
        val weaponData = player.getWeaponData(model)
        //weaponData?.setAmmo(player.getArmedWeaponData().)
    // TODO
/*
        Thread(() -> {
            if(weaponData.getAmmo() > 0) {
                PlayerPlugin.get(PlayerPlugin.class).getPlayerWeaponDao().update(weaponData);
            } else {
                PlayerPlugin.get(PlayerPlugin.class).getPlayerWeaponDao().remove(weaponData);
            }
        }).start();
        */
    }

    private fun onPlayerKeyStateChange(player: LtrpPlayer, oldKeys: PlayerKeyState, newKeys: PlayerKeyState) {
        if(!oldKeys.isKeyPressed(PlayerKey.SPRINT) && newKeys.isKeyPressed(PlayerKey.SPRINT)) {
            val animation = player.animation
            val action = player.player.specialAction;
            if(animation != null && animation.isStoppable) {
                player.clearAnimations()
            } else if(action == SpecialAction.SPECIAL_ACTION_PISSING ||
                    action == SpecialAction.HANDSUP ||
                    action == SpecialAction.DANCE1 ||
                    action == SpecialAction.DANCE2 ||
                    action == SpecialAction.DANCE3 ||
                    action == SpecialAction.DANCE4) {
                player.player.specialAction = SpecialAction.NONE
            }
        }
        if(!oldKeys.isKeyPressed(PlayerKey.WALK) && newKeys.isKeyPressed(PlayerKey.WALK)) {
            if(!player.player.isInAnyVehicle) {
                player.applyAnimation(player.walkStyle.animation)
                player.sendInfoText("Norint sustoti spauskite ~r~SPACE")
            }
        }
    }

    private fun onPlayerStreamIn(player: LtrpPlayer, forPlayer: LtrpPlayer) {
        forPlayer.player.showNameTagForPlayer(player.player, player.isMasked)

        // If the streamed in player is muted and the forPlayer is admin, show mute label
        // TODO
        /*if(player.isMuted && (forPlayer.isAdmin || forPlayer.isModerator)) {
            (player as? LtrpPlayerImpl)?.updateMuteLabel()
        }*/
    }

    private fun setDefaultWeaponSkillLevel(player: LtrpPlayer) {
        val skill = player.player.weaponSkill
        skill.setLevel(WeaponSkill.PISTOL, 1)
        skill.setLevel(WeaponSkill.SHOTGUN, 200)
        skill.setLevel(WeaponSkill.SAWNOFF_SHOTGUN, 1)
        skill.setLevel(WeaponSkill.SPAS12_SHOTGUN, 200)
        skill.setLevel(WeaponSkill.MICRO_UZI, 1)
        skill.setLevel(WeaponSkill.MP5, 300)
        skill.setLevel(WeaponSkill.AK47, 200)
        skill.setLevel(WeaponSkill.M4, 200)
        skill.setLevel(WeaponSkill.SNIPERRIFLE, 200)
    }

    private fun preloadAnimLimbs(player: LtrpPlayer) {
        Animation.ANIMATION_LIBS.forEach { player.applyAnimation(it, "null", false, false, false, 0, false) }
    }

    /*
    // TODO
    private fun showStats(showTo: LtrpPlayer, player: LtrpPlayer) {
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        SpawnData spawnData = SpawnPlugin.get(SpawnPlugin.class).getSpawnData(player);
        BankAccount bankAccount = BankPlugin.get(BankPlugin.class).getBankController().getAccount(player);
        PlayerJobData jobData = JobPlugin.get(JobPlugin.class).getJobData(player);
        showTo.sendMessage(Color.GREEN, "________________________________" + player.getCharName() + "___________________________");
        showTo.sendMessage(Color.WHITE, String.format("|VEIKËJAS| Lygis:[%d] Praþaista valandø:[%d] Amþius:[%d] Lytis:[%s] Tautybë:[%s]",
                player.level, player.onlineHours, player.age, player.sex, player.nationality));
        showTo.sendMessage(Color.LIGHTGREY, String.format("|VEIKËJAS| Mirèiø skaièius:[%d] Liga:[%s] Alkis:[%s]",
                player.deaths, "-", player.hunger))
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
        if(player.isAdmin || player.isModerator)
            showTo.sendMessage(Color.LIGHTGREY, String.format("|ADMINISTRACIJA| Int:[%d], VirtW[%d], Administratoriaus lygis:[%d] Moderatoriaus lygis:[%d]",
                    player.location.getInteriorId(), player.location.getWorldId(), player.getLevel(), player.getModLevel()));
    }*/

    // I hate this code that's why it's down here
    // Eventually I should probably move weapon onto a different module
    public fun getWeaponDao(): PlayerWeaponDao {
        return playerWeaponDao
    }

}
