package lt.maze

import lt.maze.event.PayDayEvent
import net.gtaun.shoebill.Shoebill
import net.gtaun.util.event.EventManager
import java.util.*

/**
 * Created by Bebras on 2016-12-21.
 * This is the main task
 */
class PayDayTask(private val eventManager: EventManager) : TimerTask() {


    override fun run() {
        Shoebill.get().runOnMainThread {
            eventManager.dispatchEvent(PayDayEvent())
        }
    }

}