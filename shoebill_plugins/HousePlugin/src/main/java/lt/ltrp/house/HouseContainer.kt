package lt.ltrp.house

import lt.ltrp.house.`object`.House

/**
 * Created by Bebras on 2016-10-13.
 *
 * This class contains all the instances of House
 */
class HouseContainer private constructor() {

    val houses = mutableSetOf<House>()
        get() {
            field.forEach {
                if(it.isDestroyed)
                    field.remove(it)
            }
            return field.filterNot { it.isDestroyed }.toMutableSet()
        }

    private object Holder {
        val INSTANCE = HouseContainer()
    }

    companion object {
        val instance: HouseContainer by lazy { Holder.INSTANCE }
    }
}