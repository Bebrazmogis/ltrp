package lt.maze.colorpicker

import net.gtaun.shoebill.constant.TextDrawFont
import net.gtaun.shoebill.constant.VehicleColor
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Vector2D
import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.shoebill.entities.Textdraw

/**
 * Created by Bebras on 2016-12-29.
 * This class creates all the textdraws, that's all
 * Well it also destroys them if someone asks
 */
internal class ColorPickerTextDraws : Destroyable {
    override val isDestroyed: Boolean
        // Hey, as long as it works, eh?
        get() = nextButton.isDestroyed
                && prevButton.isDestroyed
                && exitButton.isDestroyed
                && colorTextDraws.filter { it.isDestroyed }.size == colorTextDraws.size



    val nextButton: Textdraw
    val prevButton: Textdraw
    val exitButton: Textdraw
    val colorTextDraws = arrayOf<Textdraw>()

    init {
        var x = X + MARGIN
        var y = Y + MARGIN

        var count = 0
        for (c in VehicleColor.get()) {
            colorTextDraws[count] = Textdraw.create(x, y, " ")
            colorTextDraws[count].font = TextDrawFont.MODEL_PREVIEW
            colorTextDraws[count].setPreviewModelRotation(0f, 0f, 45f, 0.8f)
            colorTextDraws[count].backgroundColor = Color(0, 0, 0, 0x77)
            colorTextDraws[count].textSize = Vector2D(COLOR_BOX_WIDTH, COLOR_BOX_HEIGHT)
            colorTextDraws[count].isSelectable = true
            x += COLOR_BOX_WIDTH + MARGIN
            count++
            if (count > 0 && count % 5 == 0) {
                x = X + MARGIN
                y += COLOR_BOX_HEIGHT + MARGIN
            }
            if (count % ITEMS_PER_PAGE == 0) {
                x = X + MARGIN
                y = Y + MARGIN
            }
        }
        x = 5 * MARGIN + (COLOR_BOX_WIDTH * 5) + 30f
        y = 5 * MARGIN + (COLOR_BOX_HEIGHT * 5) + 110f

        prevButton = Textdraw.create(x, y, "Atgal")
        prevButton.font = TextDrawFont.BANK_GOTHIC
        prevButton.shadowSize = 0
        prevButton.setTextSize(x + PREV_BTN_WIDTH, PREV_BTN_HEIGHT)
        prevButton.isSelectable = true

        x += BROWSE_BTN_OFFSET_X
        y += BROWSE_BTN_OFFSET_Y

        nextButton = Textdraw.create(x, y, "Toliau")
        nextButton.font = TextDrawFont.BANK_GOTHIC
        nextButton.shadowSize = 0
        nextButton.setTextSize(x + NEXT_BTN_WIDTH, NEXT_BTN_HEIGHT)
        nextButton.isSelectable = true

        x = X + (MARGIN + COLOR_BOX_WIDTH) * 5 + 3f
        exitButton = Textdraw.create(x, Y - 5f, "LD_BEAT:cross")
        exitButton.font = TextDrawFont.SPRITE_DRAW
        exitButton.setTextSize(EXIT_BTN_WIDTH, EXIT_BTN_HEIGHT)
        exitButton.isSelectable = true
    }

    override fun destroy() {
        nextButton.destroy()
        prevButton.destroy()
        exitButton.destroy()
        colorTextDraws.forEach { it.destroy() }
    }

    protected fun finalize() {
        if(!isDestroyed) destroy()
    }

    companion object {
        val MAX_COLORS = 255
        val ITEMS_PER_PAGE = 25

        val X = 220f
        val Y = 100f
        val MARGIN = 1f

        val COLOR_BOX_WIDTH = 40f
        val COLOR_BOX_HEIGHT = 40f

        val PREV_BTN_HEIGHT = 15f
        val PREV_BTN_WIDTH = 80f

        val NEXT_BTN_WIDTH = 80f
        val NEXT_BTN_HEIGHT = 8f

        val BROWSE_BTN_OFFSET_X = 120f
        val BROWSE_BTN_OFFSET_Y = 0f

        val EXIT_BTN_WIDTH = 15f
        val EXIT_BTN_HEIGHT = 15f
    }


       val MAX_PAGE = MAX_COLORS / ITEMS_PER_PAGE

}