package lt.maze.colorpicker

import net.gtaun.shoebill.constant.VehicleColor
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager


/**
 * @author Bebras
 * *         2016.06.14.
 */
class VehicleColorPicker protected constructor(player: Player, eventManager: EventManager) :
        ColorPicker(player, eventManager, VehicleColor.get().toList()) {


    class VehicleColorPickerBuilder internal constructor(colorPicker: VehicleColorPicker) :
            ColorPicker.AbstractColorPickerBuilder<VehicleColorPicker, VehicleColorPickerBuilder>(colorPicker)

    companion object {

        fun create(player: Player, eventManager: EventManager): ColorPicker.AbstractColorPickerBuilder<*, *> {
            return VehicleColorPickerBuilder(VehicleColorPicker(player, eventManager))
        }

        fun create(player: Player, eventManager: EventManager, init: VehicleColorPickerBuilder.() -> Unit): VehicleColorPicker {
            val builder = VehicleColorPickerBuilder(VehicleColorPicker(player, eventManager))
            builder.init()
            return builder.build()
        }
    }
}
