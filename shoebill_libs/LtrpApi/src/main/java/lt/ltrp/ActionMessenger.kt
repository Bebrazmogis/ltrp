package lt.ltrp

import lt.ltrp.constant.ACTION
import net.gtaun.shoebill.data.Color

/**
 * Created by Bebras on 2016-12-26.
 * An action message should start with an asterisk and a name
 */
interface ActionMessenger {

    fun sendActionMessage(action: String) = sendActionMessage(action, DEFAULT_RADIUS)
    fun sendActionMessage(action: String, radius: Float)

    companion object {
        val DEFAULT_RADIUS = 10f
        val DEFAULT_COLOR = Color.ACTION
    }
}