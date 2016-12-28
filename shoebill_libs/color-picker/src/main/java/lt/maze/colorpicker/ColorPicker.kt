package lt.maze.colorpicker


import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.event.player.PlayerClickTextDrawEvent
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.EventManagerNode

/**
 * @author Bebras
 * *         2016.02.08.
 */
open class ColorPicker protected constructor(protected var player: Player, eventManager: EventManager, colors: List<Color>) {

    protected var eventManagerNode: EventManagerNode = eventManager.createChildNode()
    protected var selectHandler: ((ColorPicker, Color) -> Unit)? = null
    protected var cancelHandler: ((ColorPicker) -> Unit)? = null
    var currentPage: Int = 0
    protected val colors: MutableList<Color> = mutableListOf()
    internal var textDraws: ColorPickerTextDraws

    init {
        textDraws = ResourceManager.get().getPlugin(ColorPickerPlugin::class.java)?.textDraws ?: throw IllegalArgumentException("Color picker plugin not initialized")
    }


    fun addColor(color: Color) {
        colors.add(color)
    }

    fun show() {
        var index = 0
        for (color in colors) {
            if (index > ColorPickerTextDraws.MAX_COLORS)
                break
            textDraws.colorTextDraws[index++].backgroundColor = color
        }

        show(0)
        player.selectTextDraw(Color.YELLOW)
        eventManagerNode.registerHandler(PlayerClickTextDrawEvent::class, { onPlayerClickTextDraw(it) })
    }

    protected fun show(page: Int) {
        currentPage = page
        for (i in page * ColorPickerTextDraws.ITEMS_PER_PAGE..(page + 1) * ColorPickerTextDraws.ITEMS_PER_PAGE - 1) {
            val prevPageTdIndex = i - ColorPickerTextDraws.ITEMS_PER_PAGE
            val nextPageTdIndex = i + ColorPickerTextDraws.ITEMS_PER_PAGE
            if (prevPageTdIndex >= 0 && textDraws.colorTextDraws[prevPageTdIndex].isShownForPlayer(player)) {
                textDraws.colorTextDraws[prevPageTdIndex].hide(player)
            }
            if (nextPageTdIndex < textDraws.colorTextDraws.size &&
                    textDraws.colorTextDraws[nextPageTdIndex].isShownForPlayer(player)) {
                textDraws.colorTextDraws[nextPageTdIndex].hide(player)
            }
            if (i < textDraws.colorTextDraws.size)
                textDraws.colorTextDraws[i].show(player)
        }
        if (!textDraws.prevButton.isShownForPlayer(player)) {
            textDraws.prevButton.show(player)
        }
        if (!textDraws.nextButton.isShownForPlayer(player)) {
            textDraws.nextButton.show(player)
        }
        if (!textDraws.exitButton.isShownForPlayer(player))
            textDraws.exitButton.show(player)
    }

    fun hide() {
        player.cancelSelectTextDraw()
        textDraws.colorTextDraws
                .filter { it.isShownForPlayer(player) }
                .forEach { it.hide(player) }

        textDraws.nextButton.hide(player)
        textDraws.prevButton.hide(player)
        textDraws.exitButton.hide(player)
        eventManagerNode.cancelAll()
    }

    protected fun onPlayerClickTextDraw(e: PlayerClickTextDrawEvent) {
        if (e.textdraw == null) {
            hide()
            return
        }
        var i = currentPage * ColorPickerTextDraws.ITEMS_PER_PAGE
        val pageEndIndex = (currentPage + 1) * ColorPickerTextDraws.ITEMS_PER_PAGE
        // Loop through the text draws in the current page
        while (i < pageEndIndex && i < textDraws.colorTextDraws.size) {
            if (e.textdraw == textDraws.colorTextDraws[i]) {
                hide()
                selectHandler?.invoke(this, textDraws.colorTextDraws[i].backgroundColor)

                // TODO event
            }
            i++
        }
        if (e.textdraw == textDraws.nextButton && currentPage < textDraws.MAX_PAGE) {
            show(currentPage + 1)
        } else if (e.textdraw == textDraws.prevButton && currentPage > 0) {
            show(currentPage - 1)
        } else if (e.textdraw == textDraws.exitButton) {
            cancelHandler?.invoke(this)
            hide()
            // TODO event
        }
    }

    @Suppress("UNCHECKED_CAST")
    open class AbstractColorPickerBuilder<ColorPickerType : ColorPicker,
            ColorPickerBuilderType : AbstractColorPickerBuilder<ColorPickerType, ColorPickerBuilderType>>
            (private val colorPicker: ColorPickerType) {

        fun color(c: Color): ColorPickerBuilderType {
            colorPicker.addColor(c)
            return this as ColorPickerBuilderType
        }

        fun color(r: Int, g: Int, b: Int, a: Int) = color(Color(r, g, b, a))

        fun onSelectColor(handler: (ColorPicker, Color) -> Unit): ColorPickerBuilderType {
            colorPicker.selectHandler = handler
            return this as ColorPickerBuilderType
        }

        fun onCancel(handler: (ColorPicker) -> Unit): ColorPickerBuilderType {
            colorPicker.cancelHandler = handler
            return this as ColorPickerBuilderType
        }

        fun build(): ColorPickerType {
            return colorPicker
        }

    }

    class ColorPickerBuilder internal constructor(colorPicker: ColorPicker) : AbstractColorPickerBuilder<ColorPicker, ColorPickerBuilder>(colorPicker)


    companion object {

        fun create(player: Player, eventManager: EventManager, colors: MutableList<Color>): AbstractColorPickerBuilder<*, *> {
            return ColorPickerBuilder(ColorPicker(player, eventManager, colors))
        }

        fun create(player: Player, eventManager: EventManager, init: (ColorPickerBuilder.() -> Unit)): ColorPicker {
            val builder = ColorPickerBuilder(ColorPicker(player, eventManager, listOf()))
            builder.init()
            return builder.build()
        }
    }

    init {
        this.eventManagerNode = eventManager.createChildNode()
        this.colors.addAll(colors)
    }


}
