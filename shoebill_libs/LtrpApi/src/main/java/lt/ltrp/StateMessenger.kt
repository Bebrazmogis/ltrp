package lt.ltrp

import lt.ltrp.constant.ACTION
import net.gtaun.shoebill.data.Color

/**
 * Created by Bebras on 2016-12-26.
 * A state message is sent to everyone inside radius
 * It is supposed to end with (( name ))
 */
interface StateMessenger {

    fun sendStateMessage(state: String) = sendStateMessage(state, DEFAULT_RADIUS)
    fun sendStateMessage(state: String, radius: Float)

    companion object {
        val DEFAULT_RADIUS = 10f
        val COLOR = Color.ACTION
    }

}