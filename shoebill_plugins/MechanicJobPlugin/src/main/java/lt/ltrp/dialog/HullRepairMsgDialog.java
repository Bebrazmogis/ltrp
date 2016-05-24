package lt.ltrp.dialog;

import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.object.VehicleDamage;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.25.
 */
public class HullRepairMsgDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager manager, LtrpVehicle vehicle, int price) {
        VehicleDamage dmg = vehicle.getDamage();
        String tires, panels, lights, doors;
        if(LtrpVehicleModel.isBike(vehicle.getModelId()))
            tires = String.format("Priekinis ratas: %s\nGalinis ratas: %s",
                    (dmg.getTires() & 1) > 0 ? "Sprogæs" : "Sveikas",
                    (dmg.getTires() & 2) > 0 ? "Sprogæs" : "Sveikas");
        else
            tires = String.format("Priekinis-kairys ratas: %s\nPriekinis-deðinys ratas: %s\nGalinis-kairys ratas: %s\nGalini-Deðinys ratas: %s",
                    (dmg.getTires() & 1) > 0 ? "Sprogæs" : "Sveikas",
                    (dmg.getTires() & 4) > 0 ? "Sprogæs" : "Sveikas",
                    (dmg.getTires() & 2) > 0 ? "Sprogæs" : "Sveikas",
                    (dmg.getTires() & 8) > 0 ? "Sprogæs" : "Sveikas"
                    );
        doors = String.format("Kapotas: %s, %s\nBagaþinë: %s, %s\nVairuotojo durelës: %s, %s\nKeleivio durelës: %s, %s",
                (dmg.getDoors() & 2) != 0 ? "apgadintos" : "sveikos",
                (dmg.getDoors() & 4) != 0 ? "nëra" : "yra",

                (dmg.getDoors() & 2 << 8) != 0 ? "apgadintos" : "sveikos",
                (dmg.getDoors() & 4 << 8) != 0 ? "nëra" : "yra",

                (dmg.getDoors() & 2 << 16) != 0 ? "apgadintos" : "sveikos",
                (dmg.getDoors() & 4 << 16) != 0 ? "nëra" : "yra",

                (dmg.getDoors() & 2 << 24) != 0 ? "apgadintos" : "sveikos",
                (dmg.getDoors() & 4 << 24) != 0 ? "nëra" : "yra"
        );
        return MsgboxDialog.create(player, manager)
                .caption(vehicle.getModelName() + " këbulo remontas.")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .message("Këbulo remontas." +
                        "\n\nAutomobilis: " + vehicle.getModelName() +
                        "\nKaina: $" + price +
                        "\n" + tires +
                        "\n" + doors)
                .build();
    }

}
