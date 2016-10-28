package lt.ltrp.player.fine

import lt.ltrp.`object`.PlayerData
import lt.ltrp.player.fine.dao.PlayerFineDao
import lt.ltrp.player.fine.data.PlayerFine

/**
 * Created by Bebras on 2016-10-28.
 *
 */
class PlayerFineControllerImpl(private val fineDao: PlayerFineDao): PlayerFineController() {

    override fun get(playerData: PlayerData): Collection<PlayerFine> {
        return fineDao.get(playerData)
    }

    override fun create(fine: PlayerFine) {
        val uuid = fineDao.insert(fine)
        fine.UUID = uuid
    }

    override fun update(fine: PlayerFine) {
        fineDao.update(fine)
    }


}