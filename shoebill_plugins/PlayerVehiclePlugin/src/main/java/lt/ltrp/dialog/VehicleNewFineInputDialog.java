package lt.ltrp.dialog;

import lt.ltrp.PlayerVehiclePlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.VehicleFine;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class VehicleNewFineInputDialog {

    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, PlayerVehicle vehicle) {
        return InputDialog.create(player, eventManager)
                .caption("Nauja bauda automobiliui " + vehicle.getModelName())
                .buttonOk("T�sti")
                .buttonCancel("Atgal")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .message("�veskite pa�eidim�, kur� padar� �is automobilis")
                .onClickOk((d, crime) -> {
                    IntegerInputDialog.create(player, eventManager)
                            .caption(d.getCaption())
                            .buttonOk("I�saugoti")
                            .parentDialog(d)
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .buttonCancel("Atgal")
                            .message("�veskite baudos dyd�")
                            .line("Baudos dydis gali b�ti ir lygus 0, tokiu atveju bauda bus tik �odinis �sp�jimas")
                            .onClickOk((dd, fine) -> {
                                if (fine < 0)
                                    dd.show();
                                else {
                                    Instant now = Instant.now();
                                    VehicleFine vehicleFine = new VehicleFine(vehicle.getUUID(), vehicle.getLicense(), crime, player.getName(), new Timestamp(now.getEpochSecond()), fine);
                                    PlayerVehiclePlugin plugin = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class);
                                    int uuid = plugin.getFineDao().insert(vehicleFine);
                                    vehicleFine.setUUID(uuid);
                                    if (fine == 0) {
                                        vehicleFine.setPaidAt(new Timestamp(now.getEpochSecond()));
                                        plugin.getFineDao().update(vehicleFine);
                                    }
                                    player.sendMessage(Color.NEWS, "Automobilio bauda #"+ uuid + " s�kmingai �ra�yta.");
                                }
                            })
                            .build()
                            .show();
                })
                .build();
    }

}
