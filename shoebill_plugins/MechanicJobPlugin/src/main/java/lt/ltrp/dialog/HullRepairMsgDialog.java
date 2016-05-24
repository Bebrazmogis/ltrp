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
                    (dmg.getTires() & 1) > 0 ? "Sprog�s" : "Sveikas",
                    (dmg.getTires() & 2) > 0 ? "Sprog�s" : "Sveikas");
        else
            tires = String.format("Priekinis-kairys ratas: %s\nPriekinis-de�inys ratas: %s\nGalinis-kairys ratas: %s\nGalini-De�inys ratas: %s",
                    (dmg.getTires() & 1) > 0 ? "Sprog�s" : "Sveikas",
                    (dmg.getTires() & 4) > 0 ? "Sprog�s" : "Sveikas",
                    (dmg.getTires() & 2) > 0 ? "Sprog�s" : "Sveikas",
                    (dmg.getTires() & 8) > 0 ? "Sprog�s" : "Sveikas"
                    );
        doors = String.format("Kapotas: %s, %s\nBaga�in�: %s, %s\nVairuotojo durel�s: %s, %s\nKeleivio durel�s: %s, %s",
                (dmg.getDoors() & 2) != 0 ? "apgadintos" : "sveikos",
                (dmg.getDoors() & 4) != 0 ? "n�ra" : "yra",

                (dmg.getDoors() & 2 << 8) != 0 ? "apgadintos" : "sveikos",
                (dmg.getDoors() & 4 << 8) != 0 ? "n�ra" : "yra",

                (dmg.getDoors() & 2 << 16) != 0 ? "apgadintos" : "sveikos",
                (dmg.getDoors() & 4 << 16) != 0 ? "n�ra" : "yra",

                (dmg.getDoors() & 2 << 24) != 0 ? "apgadintos" : "sveikos",
                (dmg.getDoors() & 4 << 24) != 0 ? "n�ra" : "yra"
        );
        return MsgboxDialog.create(player, manager)
                .caption(vehicle.getModelName() + " k�bulo remontas.")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .message("K�bulo remontas." +
                        "\n\nAutomobilis: " + vehicle.getModelName() +
                        "\nKaina: $" + price +
                        "\n" + tires +
                        "\n" + doors)
                .build();
    }

}
