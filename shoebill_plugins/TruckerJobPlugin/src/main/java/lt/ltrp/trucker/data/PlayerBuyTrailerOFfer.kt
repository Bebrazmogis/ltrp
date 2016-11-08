package lt.ltrp.trucker.data

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.data.PlayerOffer
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-06.
 * A player offer for buying a trailer
 * Basically an offer to buy a vehicle the only difference should be in the UI
 */
class PlayerBuyTrailerOFfer(player: LtrpPlayer, offeredBy: LtrpPlayer, val trailer: LtrpVehicle, val price: Int,
                            eventManager: EventManager):
        PlayerOffer(player, offeredBy, eventManager, 60, PlayerBuyTrailerOFfer::class.java) {
}