package lt.ltrp

import lt.ltrp.`object`.LtrpPlayer

/**
 * Created by Bebras on 2016-10-15.
 * A container for player object list
 */
class PlayerContainer private constructor() {

    internal val playerList = mutableListOf<LtrpPlayer>()

    companion object {
        val instance: PlayerContainer = PlayerContainer()

    }

}