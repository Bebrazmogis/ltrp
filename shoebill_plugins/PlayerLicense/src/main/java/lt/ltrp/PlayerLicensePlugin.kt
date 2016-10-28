package lt.ltrp

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.command.LicenseCommands
import lt.ltrp.dao.impl.MySqlPlayerLicenseDaoImpl
import lt.ltrp.dao.impl.MySqlPlayerLicenseWarningDaoImpl
import lt.ltrp.dao.impl.PlayerLicenseControllerImpl
import lt.ltrp.event.player.PlayerDataLoadEvent
import lt.ltrp.player.PlayerController
import lt.ltrp.player.licenses.dao.PlayerLicenseDao
import lt.ltrp.player.licenses.dao.PlayerLicenseWarningDao
import lt.ltrp.resource.DependentPlugin
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority

/**
 * Created by Bebras on 2016-10-08.
 * This plugin is responsible for handling player licenses
 *
 */
class PlayerLicensePlugin: DependentPlugin() {

    lateinit var eventManager: EventManagerNode
    lateinit var playerLicenseDao: PlayerLicenseDao
    lateinit var playerLicenseWarningDao: PlayerLicenseWarningDao
    lateinit var commandManager: PlayerCommandManager
    lateinit var licenseController: PlayerLicenseControllerImpl

    override fun onEnable() {
        super.onEnable()
        eventManager = getEventManager().createChildNode()
    }

    override fun onDependenciesLoaded() {
        val ds = DatabasePlugin.get(DatabasePlugin::class.java).dataSource
        playerLicenseWarningDao = MySqlPlayerLicenseWarningDaoImpl(ds)
        playerLicenseDao = MySqlPlayerLicenseDaoImpl(ds, playerLicenseWarningDao)

        licenseController = PlayerLicenseControllerImpl(playerLicenseDao, playerLicenseWarningDao)

        registerEvents()
        registerCommands()
        PlayerController.instance.getPlayers().forEach { loadLicenses(it) }
    }

    override fun onDisable() {
        eventManager.destroy()
        commandManager.destroy()
        PlayerController.instance.getPlayers().forEach { it.licenses = null }
    }

    private fun registerCommands() {
        commandManager = PlayerCommandManager(eventManager)
        commandManager.registerCommands(LicenseCommands())
        commandManager.installCommandHandler(HandlerPriority.NORMAL)
    }

    private fun registerEvents() {
        eventManager.registerHandler(PlayerDataLoadEvent::class.java, { onPlayerDataLoad(it.player) })
    }


    private fun onPlayerDataLoad(player: LtrpPlayer) {
        loadLicenses(player)
    }

    private fun loadLicenses(player: LtrpPlayer) {
        val licenses = playerLicenseDao.getAll(player)
        player.licenses = licenses
    }
}