package lt.ltrp.dao

import lt.ltrp.LtrpWorld

/**
 * Created by Bebras on 2016-10-06.
 */
interface LtrpWorldDao {

    fun save(world: LtrpWorld)
    fun load(world: LtrpWorld)

}