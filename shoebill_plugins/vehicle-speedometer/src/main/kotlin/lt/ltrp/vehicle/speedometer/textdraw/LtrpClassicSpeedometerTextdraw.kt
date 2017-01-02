package lt.ltrp.vehicle.speedometer.textdraw

import net.gtaun.shoebill.constant.TextDrawFont
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Vector2D
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.PlayerTextdraw

/**
 * Created by Bebras on 2017-01-02.
 * The classic, black transparent box white text textdraw
 */
object LtrpClassicSpeedometerTextdraw {

    fun create(player: Player): PlayerTextdraw {
        val textdraw = PlayerTextdraw.create(player, 535.0f, 350.0f, "_" );
        textdraw.font = TextDrawFont.FONT2
        textdraw.letterSize = Vector2D(0.2f, 1.3f)
        textdraw.shadowSize = 0
        textdraw.isUseBox = true
        textdraw.boxColor = Color(0x00000044)
        textdraw.outlineSize = 0
        return textdraw
    }
}