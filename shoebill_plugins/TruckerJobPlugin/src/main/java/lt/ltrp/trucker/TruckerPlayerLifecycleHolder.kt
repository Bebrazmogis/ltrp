package lt.ltrp.trucker

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.trucker.`object`.TruckerPlayer
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-30.
 */
class TruckerPlayerLifecycleHolder(eventManager: EventManager): PlayerLifecycleHolder(eventManager) {

    init {
        registerClass(TruckerPlayer::class.java, { eventManager, player ->
            TruckerPlayer(eventManager, LtrpPlayer.get(player))
        })
    }

    fun getObject(player: LtrpPlayer): TruckerPlayer {
        return super.getObject(player, TruckerPlayer::class.java)
    }

}