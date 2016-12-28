package lt.maze.colorpicker

import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.resource.Plugin

/**
 * Created by Bebras on 2016-12-26.
 *
 */
@ShoebillMain("Color picker plugin", "Bebras")
class ColorPickerPlugin : Plugin() {


    internal lateinit var textDraws: ColorPickerTextDraws

    override fun onEnable() {
        textDraws = ColorPickerTextDraws()
    }

    override fun onDisable() {
        textDraws.destroy()
    }
}