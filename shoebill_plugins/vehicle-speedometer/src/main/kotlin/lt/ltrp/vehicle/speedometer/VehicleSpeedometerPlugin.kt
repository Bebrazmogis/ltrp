package lt.ltrp.vehicle.speedometer

import lt.ltrp.resource.DependentPlugin
import lt.ltrp.vehicle.VehiclePlugin
import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.event.VehicleCreateEvent
import lt.ltrp.vehicle.event.VehicleDestroyEvent
import lt.ltrp.vehicle.speedometer.`object`.Speedometer
import lt.ltrp.vehicle.speedometer.`object`.impl.SpeedometerImpl
import lt.maze.lastvehicle.PlayerLastVehiclePlugin
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.constant.PlayerState
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.Timer
import net.gtaun.shoebill.entities.TimerCallback
import net.gtaun.shoebill.event.player.PlayerDeathEvent
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2017-01-02.
 *  This plugin manages the lifecycle of speedometers
 *  Plugin supports being reloaded at runtime
 */
@ShoebillMain("Vehicle speedometer plugin", "Bebras")
class VehicleSpeedometerPlugin: DependentPlugin() {

    private lateinit var eventManagerNode: EventManagerNode
    private var updateTimer: Timer? = null
    private val vehicleSpeedometers = mutableMapOf<LtrpVehicle, Speedometer>()

    init {
        addDependency(VehiclePlugin::class)
    }

    override fun onEnable() {
        super.onEnable()
        eventManagerNode = eventManager.createChildNode()

        // If there are any created vehicle when enabled, create their speedometers
        LtrpVehicle.get().forEach { createSpeedometer(it) }

         // If there are any drivers when enabled, show them speedometers
        Player.get()
                .filter { it.state == PlayerState.DRIVER }
                .forEach {
                    val vehicle = it.vehicle ?: return
                    val ltrpVehicle = LtrpVehicle.getByVehicle(vehicle) ?: return
                    val speedometer = vehicleSpeedometers[ltrpVehicle] ?: return
                    speedometer.show(it)
                }
    }

    override fun onDisable() {
        super.onDisable()
        eventManagerNode.cancelAll()
    }

    override fun onDependenciesLoaded() {

        addEventHandlers()
        val timer = Timer.create(UPDATE_INTERVAL, TimerCallback {
            updateSpeedometers()
        })
        timer.start()
        updateTimer = timer
    }

    private fun addEventHandlers() {
        eventManagerNode.registerHandler(PlayerStateChangeEvent::class.java, {
            onPlayerStateChange(it.player, it.newState, it.oldState)
        })
        eventManagerNode.registerHandler(VehicleCreateEvent::class.java, { onVehicleCreate(it.vehicle) })
        eventManagerNode.registerHandler(VehicleDestroyEvent::class.java, { onVehicleDestroy(it.vehicle) })
        eventManagerNode.registerHandler(PlayerDeathEvent::class.java, { onPlayerDeath(it.player) })
    }

    private fun onPlayerStateChange(player: Player, newState: PlayerState, oldState: PlayerState) {
        when(newState) {
            PlayerState.DRIVER -> {
                val vehicle = player.vehicle
                if(vehicle != null) {
                    val ltrpVehicle = LtrpVehicle.getByVehicle(vehicle) ?: return
                    val speedometer = vehicleSpeedometers[ltrpVehicle] ?: return
                    speedometer.show(player)
                }
            }
            else -> { }
        }
        when(oldState) {
            PlayerState.DRIVER -> {
                val vehicle = ResourceManager.get().getPlugin(PlayerLastVehiclePlugin::class.java)?.getLastPlayerVehicle(player) ?: return
                val ltrpVehicle = LtrpVehicle.getByVehicle(vehicle) ?: return
                val speedometer = vehicleSpeedometers[ltrpVehicle] ?: return
                speedometer.hide(player)
            }
            else -> { }
        }
    }

    private fun onVehicleCreate(vehicle: LtrpVehicle) {
        createSpeedometer(vehicle)
    }

    private fun onVehicleDestroy(vehicle: LtrpVehicle) {
        vehicleSpeedometers[vehicle]?.hide()
        vehicleSpeedometers.remove(vehicle)
    }

    private fun onPlayerDeath(player: Player) {
        val vehicle = ResourceManager.get().getPlugin(PlayerLastVehiclePlugin::class.java)?.getLastPlayerVehicle(player) ?: return
        val ltrpVehicle = LtrpVehicle.getByVehicle(vehicle) ?: return
        val speedometer = vehicleSpeedometers[ltrpVehicle] ?: return
        speedometer.hide(player)
    }

    private fun updateSpeedometers() {
        vehicleSpeedometers.values.forEach { it.update() }
    }

    private fun createSpeedometer(vehicle: LtrpVehicle) {
        vehicleSpeedometers.put(vehicle, SpeedometerImpl(vehicle, eventManagerNode))
    }

    companion object {
        val UPDATE_INTERVAL = 120
    }
}