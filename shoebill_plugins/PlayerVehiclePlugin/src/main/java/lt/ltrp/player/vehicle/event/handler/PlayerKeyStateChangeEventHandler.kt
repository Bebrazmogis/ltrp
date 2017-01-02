package lt.ltrp.player.vehicle.event.handler

import lt.ltrp.constant.LtrpVehicleModel
import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.event.VehicleEngineKillEvent
import lt.ltrp.vehicle.event.VehicleEngineStartEvent
import net.gtaun.shoebill.constant.PlayerKey
import net.gtaun.shoebill.constant.PlayerState
import net.gtaun.shoebill.entities.Timer
import net.gtaun.shoebill.entities.TimerCallback
import net.gtaun.shoebill.entities.VehicleParam
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent
import net.gtaun.util.event.EventHandler
import net.gtaun.util.event.EventManager
import java.util.*

/**
 * Created by Bebras on 2016-12-29.
 *
 */
internal class PlayerKeyStateChangeEventHandler(private val eventManager: EventManager,
                                                private val vehicleEngineTimers: MutableMap<LtrpVehicle, Timer>):
        EventHandler<PlayerKeyStateChangeEvent> {

    private val random = Random()

    override fun handleEvent(event: PlayerKeyStateChangeEvent) {
        val oldState = event.oldState
        val player = event.player
        val ltrpVehicle = player.vehicle?.let { LtrpVehicle.getByVehicle(it) }
        val vehicle = ltrpVehicle?.vehicle

        if(ltrpVehicle != null && vehicle != null) {
            if(player.keyState.isKeyPressed(PlayerKey.FIRE) && !oldState.isKeyPressed(PlayerKey.FIRE)) {
                if(LtrpVehicleModel.isMotorVehicle(vehicle.modelId)) {
                    when(vehicle.state.engine) {
                        VehicleParam.PARAM_ON -> {
                            if(!vehicleEngineTimers.containsKey(ltrpVehicle)) {
                                val successRate = getSuccessRate(ltrpVehicle)
                                val time = 1500
                                val success = random.nextInt(101) < successRate
                                val engineEvent = VehicleEngineStartEvent(ltrpVehicle, player, success)
                                eventManager.dispatchEvent(event)
                                if(!engineEvent.isDenied) {
                                    player.sendActionMessage("pasuka automobilio raktelá ir bando uþvesti variklá.")
                                    val t = Timer.create(time, 1, TimerCallback {
                                        if (success) {
                                            ltrpVehicle.sendStateMessage("variklis uþsikuria")
                                            vehicle.state.engine = VehicleParam.PARAM_ON
                                        } else {
                                            ltrpVehicle.sendStateMessage("variklis neuþsikuria")
                                        }
                                        vehicleEngineTimers.remove(ltrpVehicle)
                                    })
                                    t.start()
                                    vehicleEngineTimers.put(ltrpVehicle, t)
                                }
                            }
                        }
                        else -> {
                            val engineEvent = VehicleEngineKillEvent(ltrpVehicle, player)
                            eventManager.dispatchEvent(event)
                            if(!engineEvent.isDenied) {
                                player.sendActionMessage("pasuka automobilio raktelá ir iðjungia variklá.");
                                vehicle.state.engine = VehicleParam.PARAM_OFF
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSuccessRate(vehicle: LtrpVehicle): Int {
        var percentage: Int
        val health = vehicle.vehicle?.health ?: 0f
        val dmg = 1000f - health

        percentage = 100 - (dmg / 40).toInt()
        // Special cased of failure:
        if(health < 400f) {
            percentage = 0
        } else if(vehicle.fuelTank.fuel == 0f) {
            percentage = 0
        }
        return percentage
    }
}