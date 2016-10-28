package lt.ltrp.service

import lt.ltrp.AfkPlugin
import lt.ltrp.`object`.LtrpPlayer
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.service.Service
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

/**
 * Created by Bebras on 2016-10-12.
 */
class PlayerKickerService(val afkPlugin: AfkPlugin): Service {

    lateinit var timer: Timer

    init {
        timer = Timer("PlayerKickerService timer")
        timer.scheduleAtFixedRate(0, 60*1000, { kickTask() })
    }

    private fun kickTask() {
        Player.get().forEach {
            val seconds = afkPlugin.getPlayerAfkSeconds(it)
            if(seconds > 5*60*1000)
                if (seconds >= 7 * 60) {
                    LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, "Þaidëjas " + it.getName() + " buvo iðmestas ið serverio. Prieþastis: AFK")
                    it.kick()
                }
        }
    }

}