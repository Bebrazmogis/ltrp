package lt.ltrp.dialog;

import lt.ltrp.player.PlayerController;
import lt.ltrp.house.object.House;
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
                .line("Radijo stotis:" + (house.getRadio() != null && house.getRadio().getStation() != null ? house.getRadio().getStation().getName() : "iğjungtas"))
                .line("Savininkas:" + (house.isOwned() ? PlayerController.get().getUsernameByUUID(house.getOwner()) : "nëra"))
                .line("\n\n")
                .line("Atnaujinimai\n" + house.getUpgrades().stream().map(u -> "\t" + u.getType().getName()).collect(Collectors.joining("\n")))
                .line("Şolës augalai(" + house.getWeedSaplings().size() + ")\n\n")
                .line(house.getWeedSaplings().stream().map(w -> String.format("%d. Uşaugæs: %s", w.getUUID(), w.isGrown() ? "Taip" : "Ne")).collect(Collectors.joining("\n")))
                .buttonOk("Atgal")
                .buttonCancel("")
                .onClickOk(d -> parent.show())
                .build();
    }

}
