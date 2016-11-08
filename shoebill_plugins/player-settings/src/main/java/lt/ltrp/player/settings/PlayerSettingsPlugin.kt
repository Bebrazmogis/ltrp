package lt.ltrp.player.settings

import lt.ltrp.DatabasePlugin
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.event.player.PlayerLogInEvent
import lt.ltrp.player.PlayerController
import lt.ltrp.player.settings.command.SettingCommands
import lt.ltrp.player.settings.dao.PlayerSettingsDao
import lt.ltrp.player.settings.dao.impl.SqlPlayerSettingsDaoImpl
import lt.ltrp.player.settings.data.PlayerSettings
import lt.ltrp.resource.DependentPlugin
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority

/**
 * Created by Bebras on 2016-10-15.
 * This plugin manages player settings
 * It loads the once a player logs in, provides commands to open management GUI
 *
 * <b>This plugin supports being restarted at runtime</b>
 */
class PlayerSettingsPlugin: DependentPlugin() {

    private lateinit var settingsDao: PlayerSettingsDao
    private lateinit var eventManager: EventManagerNode
    private lateinit var commandManager: PlayerCommandManager
    private lateinit var settingsController: PlayerSettingsControllerImpl

    override fun onEnable() {
        super.onEnable()
        eventManager = getEventManager().createChildNode()
    }

    override fun onDependenciesLoaded() {
        settingsDao = SqlPlayerSettingsDaoImpl(DatabasePlugin.get(DatabasePlugin::class.java).dataSource, eventManager)
        settingsController = PlayerSettingsControllerImpl(settingsDao, eventManager)
        registerEventListeners()
        registerCommands()

        PlayerController.instance.getPlayers().forEach { loadSettings(it) }
    }

    override fun onDisable() {
        super.onDisable()
        eventManager.destroy()
        commandManager.destroy()
    }

    private fun registerCommands() {
        commandManager = PlayerCommandManager(eventManager)
        commandManager.registerCommands(SettingCommands(eventManager))
        commandManager.installCommandHandler(HandlerPriority.NORMAL)
    }

    /**
     * Register event listeners and their handler functions
     */
    private fun registerEventListeners() {
        eventManager.registerHandler(PlayerLogInEvent::class.java, { onPlayerLogIn(it.player) })
    }

    private fun onPlayerLogIn(player: LtrpPlayer) {
        loadSettings(player)
    }

    private fun loadSettings(player: LtrpPlayer) {
        var settings = settingsDao.get(player)
        if(settings == null) {
            settings = PlayerSettings(player)
            settingsDao.insert(settings)
        }
        player.settings = settings
    }

}