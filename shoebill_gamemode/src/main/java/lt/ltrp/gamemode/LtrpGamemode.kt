package lt.ltrp.gamemode

import net.gtaun.shoebill.resource.Gamemode
import net.gtaun.shoebill.resource.ResourceManager

/**
 * Created by Bebras on 2016-10-28.
 */
class LtrpGamemode: Gamemode() {


    override fun onEnable() {
        logger.info("Gamemode started")
    }

    override fun onDisable() {
        logger.info("Gamemode disabled")
    }


}