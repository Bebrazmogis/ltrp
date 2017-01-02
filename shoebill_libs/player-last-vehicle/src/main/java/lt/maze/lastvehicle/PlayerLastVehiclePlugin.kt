package lt.maze.lastvehicle

import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.constant.PlayerState
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.Vehicle
import net.gtaun.shoebill.event.player.PlayerDeathEvent
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2017-01-02.
 * This plugin provides a method to get the last used vehicle by a player
 * That's basically all it does
 *
 * It supports being reloaded at runtime
 */
@ShoebillMain("Player last used vehicle plugin", "Bebras", "", "1.0")
class PlayerLastVehiclePlugin: Plugin() {

    private lateinit var eventManagerNode: EventManagerNode
    private val lastPlayerVehicles = mutableMapOf<Player, Vehicle>()

    override fun onEnable() {
        eventManagerNode = eventManager.createChildNode()
        Player.get()
                .filter { it.vehicle != null }
                .forEach { lastPlayerVehicles.put(it, it.vehicle!!) }
        registerEventHandlers()
    }

    override fun onDisable() {
        eventManagerNode.destroy()
        lastPlayerVehicles.clear()
    }

    fun getLastPlayerVehicle(player: Player): Vehicle? {
        return lastPlayerVehicles[player]
    }


    private fun registerEventHandlers() {
        eventManagerNode.registerHandler(PlayerStateChangeEvent::class.java, {
            onPlayerStateChange(it.player, it.oldState, it.newState)
        })
        eventManagerNode.registerHandler(PlayerDisconnectEvent::class.java, { onPlayerDisconnect(it.player) })
        eventManagerNode.registerHandler(PlayerDeathEvent::class.java, { onPlayerDeath(it.player) })
    }


    private fun onPlayerStateChange(player: Player, oldState: PlayerState, newState: PlayerState) {
        when(newState) {
            PlayerState.PASSENGER,
            PlayerState.DRIVER -> {
                lastPlayerVehicles.put(player, player.vehicle!!)
            }
        }
        when(oldState) {
            PlayerState.PASSENGER,
            PlayerState.DRIVER -> {
                lastPlayerVehicles.remove(player)
            }
        }
    }

    private fun onPlayerDisconnect(player: Player) {
        lastPlayerVehicles.remove(player)
    }

    private fun onPlayerDeath(player: Player) {
        lastPlayerVehicles.remove(player)
    }
}