package lt.ltrp.property.event

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.property.`object`.Property


/**
 * @author Bebras
 * *         2015.11.29.
 */
class PlayerExitPropertyEvent(property: Property, val player: LtrpPlayer) : PropertyEvent(property)
