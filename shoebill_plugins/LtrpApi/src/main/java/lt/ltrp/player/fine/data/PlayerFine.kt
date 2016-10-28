package lt.ltrp.player.fine.data

import lt.ltrp.`object`.PlayerData
import lt.ltrp.`object`.impl.EntityImpl
import java.time.LocalDateTime


/**
 * @author Bebras
 *         2015.12.30.
 */
class PlayerFine(uuid: Int,
                 val player: PlayerData,
                 val issuedBy: PlayerData,
                 val description: String,
                 val fine: Int,
                 var isPaid: Boolean,
                 val createdAt: LocalDateTime):
        EntityImpl(uuid) {

    constructor(uuid: Int, player: PlayerData, issuedBy: PlayerData, description: String, fine: Int):
            this(uuid, player, issuedBy, description, fine, false, LocalDateTime.now())

}
