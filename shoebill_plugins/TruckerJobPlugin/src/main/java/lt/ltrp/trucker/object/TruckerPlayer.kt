package lt.ltrp.trucker.`object`

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.data.Animation
import lt.ltrp.trucker.data.Cargo
import net.gtaun.shoebill.common.player.PlayerLifecycleObject
import net.gtaun.shoebill.constant.SpecialAction
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-30.
 * Player related data for truckers
 */
class TruckerPlayer(eventManager: EventManager, player: LtrpPlayer): PlayerLifecycleObject(eventManager, player) {


    /**
     * Commodity the player is carrying, or null if he isn't
     */
    var cargo: Cargo? = null
        set(value) {
            if(value != null) {
                getPlayer().applyAnimation(PICKUP_COMMODITY_ANIM)
                getPlayer().specialAction = SpecialAction.CARRY
            } else {
                getPlayer().specialAction = SpecialAction.NONE
            }
            field = value
        }

    override fun onInit() {

    }

    override fun onDestroy() {

    }

    override fun getPlayer(): LtrpPlayer {
        return super.getPlayer() as LtrpPlayer
    }






    companion object {
        internal val PICKUP_COMMODITY_ANIM = Animation("CARRY", "liftup", false, 800, false)
    }

}