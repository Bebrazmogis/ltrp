package lt.ltrp

import lt.maze.event.PayDayEvent
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.util.event.EventHandler
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2016-12-21.
 * This plugin manages player paydays
 */
@ShoebillMain(name = "Player Payday", author = "Bebras", version = "1.0")
class PlayerPaydayPlugin : Plugin() {

    private lateinit var eventManagerNode: EventManagerNode
    private lateinit var paydayEventHandler: EventHandler<PayDayEvent>

    override fun onEnable() {
        eventManagerNode = eventManager.createChildNode()
        paydayEventHandler = PayDayEventHandler(logger)
        registerEventHandlers()
    }

    override fun onDisable() {
        eventManagerNode.cancelAll()
    }

    private fun registerEventHandlers() {
        eventManagerNode.registerHandler(PayDayEvent::class.java, paydayEventHandler)
    }


}