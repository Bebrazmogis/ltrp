package lt.ltrp.player.textdraw

import net.gtaun.shoebill.constant.TextDrawFont
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Vector2D
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.PlayerTextdraw

/**
 * @author Bebras
 * 2017.01.01.
 */
object InfoTextDraw {

    fun create(player: Player): PlayerTextdraw {
        val td = PlayerTextdraw.create(player, 13f, 150f, "_")
        td.isUseBox = true
        td.boxColor = Color(0, 0, 0, 0x66)
        td.textSize = Vector2D(158f, 91f)
        td.backgroundColor = Color(0, 0, 0, 0xFF)
        td.font = TextDrawFont.FONT2
        td.letterSize = Vector2D(0.36f, 1.5f)
        td.color = Color(-1)
        td.isProportional = true
        td.shadowSize = 0
        return td
    }

}