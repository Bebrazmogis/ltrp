package lt.ltrp.player.`object`.impl;


import lt.ltrp.ActionMessenger
import lt.ltrp.StateMessenger
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.PlayerData
import lt.ltrp.data.Animation
import lt.ltrp.data.LtrpWeaponData
import lt.ltrp.data.PlayerOffer
import lt.ltrp.player.textdraw.InfoTextDraw
import lt.maze.audio.AudioHandle
import lt.maze.audio.AudioPlugin
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.shoebill.constant.WeaponSlot
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.data.Radius
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.Timer
import net.gtaun.shoebill.entities.TimerCallback
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManager
import org.slf4j.LoggerFactory

/**
 * @author Bebras
 *         2015.11.12.
 *
 *         Implementation of the mighty [LtrpPlayer]]
 */

class LtrpPlayerImpl(override val player: Player, playerData: PlayerData, eventManager: EventManager):
        LtrpPlayer, PlayerDataImpl(playerData, eventManager) {

    companion object {
        private val logger = LoggerFactory.getLogger(LtrpPlayerImpl::class.java)
    }

    override var isFrozen: Boolean = playerData.isFrozen
        set(value) {
            player.toggleControllable(!value)
            logger.debug("Player freeze status changed to $value")
            field = value
        }
    override var isMuted: Boolean = playerData.isMuted

    override val ucpId: Int = 0

    override var animation: Animation? = null

    override val isAudioConnected: Boolean
        get() = ResourceManager.get().getPlugin(AudioPlugin::class.java)?.isConnected(player) ?: false

    override var money: Int = playerData.money
        set(value) {
            player.money = money
            field = value
        }

    override var audioHandle: AudioHandle? = null

    internal val offers = mutableSetOf<PlayerOffer>()
    private val weapons = mutableSetOf<LtrpWeaponData>()

    private val infoTextTextDraw = InfoTextDraw.create(player)
    private var infoTextTimer: Timer? = null


    override fun applyAnimation(animation: Animation) {
        this.animation = animation
        player.applyAnimation(animation.animLib, animation.animName, animation.speed, if(animation.isLoop) 1 else 0,
                if(animation.isLockX) 1 else 0, if(animation.isLockY)  1 else 0, if(animation.isFreeze) 1 else 0,
                animation.time, if(animation.isForceSync) 1 else 0)
    }

    override fun clearAnimations() {
        if(animation != null) animation = null
        player.clearAnimations(1)
    }

    override fun containsOffer(type: Class<Any>): Boolean {
        return offers.find { it.javaClass == type } != null
    }

    override fun getOffers(): Collection<PlayerOffer> {
        return offers
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: PlayerOffer> getOffers(type: Class<T>): Collection<T> {
        val tOffers = mutableSetOf<T>()
        this.offers.forEach { if(it.javaClass == type) tOffers.add(it as T) }
        return tOffers
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: PlayerOffer> getOffer(type: Class<T>): T? {
        offers.forEach {
            if(it.javaClass == type)
                return it as T
        }
        return null
    }

    override fun getArmedWeaponData(): LtrpWeaponData {
        return weapons.find { it.weaponData.model == player.armedWeapon } ?: LtrpWeaponData.UNARMED
    }

    override fun getWeapons(): Collection<LtrpWeaponData> {
        return weapons
    }

    override fun getWeaponData(weaponModel: WeaponModel): LtrpWeaponData? {
        return weapons.firstOrNull { it.weaponData.model == weaponModel }
    }

    override fun ownsWeapon(model: WeaponModel): Boolean {
        return getWeaponData(model) != null
    }

    override fun isWeaponSlotUsed(slot: WeaponSlot): Boolean {
        return weapons.firstOrNull { it.weaponData.model.slot == slot } != null
    }

    override fun removeWeapon(weaponData: LtrpWeaponData) {
        weapons.remove(weaponData)
    }

    override fun removeWeapon(model: WeaponModel) {
        val data = getWeaponData(model) ?: return
        removeWeapon(data)
    }

    override fun removeJobWeapons() {
        weapons.forEach {
            if(it.isJob) removeWeapon(it)
        }
    }

    override fun giveWeapon(weaponData: LtrpWeaponData) {
        if(weapons.size != 12) {
            weapons.add(weaponData)
        } else logger.error("Player $UUID can not have more weapons. $weaponData")
    }


    override fun getClosestPlayer(maxDistance: Float): LtrpPlayer? {
        val closestPlayer = getClosestPlayer() ?: return null

        if(closestPlayer.player.location.distance(player.location) < maxDistance) {
            return closestPlayer
        } else {
            return null
        }
    }

    override fun getClosestPlayer(): LtrpPlayer? {
        return LtrpPlayer.Companion.get().minBy { it.player.location.distance(player.location) }
    }

    override fun getClosestPlayers(maxDistance: Float): Collection<LtrpPlayer> {
        return LtrpPlayer.Companion.get().filter { it.player.location.distance(player.location) < maxDistance }
    }

    override fun setVolume(volume: Int) {

    }

    override fun playAudioStream(url: String) {
        if(!isAudioConnected) {
            player.playAudioStream(url)
        } else {
            audioHandle = AudioHandle.play(player, url)
        }
    }

    override fun playAudioStream(url: String, radius: Radius) {
        if(isAudioConnected) {
            audioHandle = AudioHandle.play(player, url)
            audioHandle!!.set3DPosition(radius)
        } else {
            player.playAudioStream(url, radius)
        }
    }


    /*
     * MESSAGES
     */

    override fun sendFadeMessage(color: Color, text: String, distance: Float) {
        val hsb = java.awt.Color.RGBtoHSB(color.r, color.g, color.b, null)
        LtrpPlayer.Companion.get().forEach {
            val distanceToMessage = it.player.location.distance(player.location)
            if(distanceToMessage <= distance) {
                hsb[2] -= distance - distanceToMessage
                val c = Color(java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]))
                it.sendMessage(c, text)
            }
        }
    }

    override fun sendFadeMessage(color: Color, text: String, location: Location) {
        val hsb = java.awt.Color.RGBtoHSB(color.r, color.g, color.b, null)
        val distanceToMessage = player.location.distance(location)
        hsb[2] -= distanceToMessage
        val c = Color(java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]))
        sendMessage(c, text)
    }

    override fun sendErrorMessage(message: String) {
        sendMessage(Color.RED, "[KLAIDA]" + message)
    }

    override fun sendErrorMessage(errorCode: Int) {
        sendMessage(Color.RED, "Atsipraðome bet ðiuo metu negalime uþbaigti jûsø veiksmo. Klaidos kodas $errorCode.")
        logger.error("Error ID " + errorCode)
    }

    override fun sendMessage(color: Color, s: String, distance: Float) {
        LtrpPlayer.Companion.get().forEach {
            if(it.player.location.distance(player.location) < distance) {
                it.sendMessage(color, s)
            }
        }
    }

    override fun sendDebug(color: Color, message: String) {
        sendMessage(color, "[DEBUG]" + message)
    }

    override fun sendDebug(message: String) {
        sendDebug(Color.LIGHTYELLOW, message)
    }

    override fun sendDebug(vararg objects: Any?) {
        objects.forEach { o ->
            sendDebug(if(o == null) "null" else o.javaClass.kotlin.simpleName + ":" + o.toString())
        }
    }

    override fun sendInfoText(msg: String, seconds: Int) {
        infoTextTextDraw.text = msg
        if(infoTextTimer != null)  {
            infoTextTimer!!.stop()
            infoTextTimer!!.destroy()
        } else {
            infoTextTextDraw.show()
        }
        infoTextTimer = Timer.create(seconds * 1000, TimerCallback  {
            infoTextTimer!!.destroy()
            infoTextTimer = null
            infoTextTextDraw.hide()
        })
        infoTextTimer!!.start()

    }

    override fun sendStateMessage(state: String, radius: Float) {
        sendMessage(StateMessenger.COLOR, "* $state (( $charName ))", radius)
        player.setChatBubble(state, StateMessenger.COLOR, radius, state.length * 50)
    }

    override fun sendActionMessage(action: String, radius: Float) {
        sendMessage(ActionMessenger.DEFAULT_COLOR, "* $charName $action", radius)
        player.setChatBubble(action, ActionMessenger.DEFAULT_COLOR, radius, action.length * 50)
    }

    /*
     * END OF MESSAGES
     */
}
