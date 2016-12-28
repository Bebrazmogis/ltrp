package lt.ltrp.player.description

import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2016-12-26.
 * TODO add command for initiating dialogs
 */
@ShoebillMain("Player description plugin", "Bebras")
class PlayerDescriptionPlugin : Plugin() {

    private lateinit var eventManagerNode: EventManagerNode

    override fun onEnable() {
        eventManagerNode = eventManager.createChildNode()

    }

    override fun onDisable() {
        eventManagerNode.cancelAll()
    }

}