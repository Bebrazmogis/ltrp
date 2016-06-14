package lt.ltrp.colorpicker;

import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.constant.VehicleColor;
import net.gtaun.util.event.EventManager;

import java.util.Arrays;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class VehicleColorPicker extends ColorPicker {

    public static AbstractColorPickerBuilder create(LtrpPlayer player, EventManager eventManager) {
        return new VehicleColorPickerBuilder(new VehicleColorPicker(player, eventManager));
    }

    protected VehicleColorPicker(LtrpPlayer player, EventManager eventManager) {
        super(player, eventManager, Arrays.asList(VehicleColor.getColors()));
    }


    private static class VehicleColorPickerBuilder extends AbstractColorPickerBuilder<VehicleColorPicker, VehicleColorPickerBuilder> {

        public VehicleColorPickerBuilder(VehicleColorPicker colorPicker) {
            super(colorPicker);
        }
    }
}
