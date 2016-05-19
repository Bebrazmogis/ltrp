package lt.ltrp.dialog;

import lt.ltrp.PlayerController;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseInfoMessageDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, House house) {
        return MsgboxDialog.create(player, eventManager)
                .caption("Namo " + house.getName() + " informacija")
                .line("ID:" + house.getUUID())
                .line("Pavadinimas: " + house.getName())
                .line("Kaina:" + house.getPrice())
                .line("Nuomos kaina:" + house.getRentPrice())
                .line("Pinigai: " + house.getMoney())
                .line("Radijo stotis:" + (house.getRadio() != null && house.getRadio().getStation() != null ? house.getRadio().getStation().getName() : "i�jungtas"))
                .line("Savininkas:" + (house.isOwned() ? PlayerController.get().getPlayerDao().getUsername(house.getOwner()) : "n�ra"))
                .line("\n\n")
                .line("Atnaujinimai\n" + house.getUpgrades().stream().map(u -> "\t" + u.getName()).collect(Collectors.joining("\n")))
                .line("�ol�s augalai(" + house.getWeedSaplings().size() + ")\n\n")
                .line(house.getWeedSaplings().stream().map(w -> String.format("%d. U�aug�s: %s", w.getId(), w.isGrown() ? "Taip" : "Ne")).collect(Collectors.joining("\n")))
                .buttonOk("Atgal")
                .buttonCancel("")
                .onClickOk(d -> parent.show())
                .build();
    }

}
