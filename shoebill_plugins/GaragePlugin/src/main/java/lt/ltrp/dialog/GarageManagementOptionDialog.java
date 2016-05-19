package lt.ltrp.dialog;


import lt.ltrp.GarageController;
import lt.ltrp.event.property.garage.GarageEditEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;


/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageManagementOptionDialog {


    public static ListDialog create(LtrpPlayer player, EventManager eventManager, ListDialog parentDialog, Garage garage) {
        return ListDialog.create(player, eventManager)
                .parentDialog(parentDialog)
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .item("Garaþo informacija", i -> {
                    GarageInfoMessageDialog.create(player, eventManager, i.getCurrentDialog(), garage).show();
                })
                .item("{FF6E62}Paðalinti garaþà", i -> {
                    GarageDestroyMsgDialog.create(player, eventManager, i.getCurrentDialog(), garage).show();
                })
                .item("Perkelti garaþo áëjima á mano pozicijà", i -> {
                    garage.setEntrance(player.getLocation());
                    eventManager.dispatchEvent(new GarageEditEvent(garage, player));
                    GarageController.get().getDao().update(garage);
                })
                .item("Perkelti garaþo iðëjimà á mano pozicijà", i -> {
                    Location loc = player.getLocation();
                    loc.setWorldId(garage.getUUID());
                    garage.setExit(loc);
                    eventManager.dispatchEvent(new GarageEditEvent(garage, player));
                    GarageController.get().getDao().update(garage);
                })
                .item("Perkelti garaþo automobilio áëjimà á mano pozicijà", i -> {
                    garage.setVehicleEntrance(player.getLocation());
                    eventManager.dispatchEvent(new GarageEditEvent(garage, player));
                    GarageController.get().getDao().update(garage);
                })
                .item("Perkelti garaþo automobilio iðëjimà á manjo pozicijà", i -> {
                    AngledLocation loc = player.getLocation();
                    loc.setWorldId(garage.getUUID());
                    garage.setVehicleExit(loc);
                    GarageController.get().getDao().update(garage);
                    eventManager.dispatchEvent(new GarageEditEvent(garage, player));
                })
                .item("Paðalinti savininkà", garage::isOwned, i -> {
                    GarageRemoveOwnerMsgBoxDialog.create(player, eventManager, i.getCurrentDialog(), garage).show();
                })
                .item("Keisti pavadinimà", i -> {
                    GarageNameInputDialog.create(player, eventManager, i.getCurrentDialog(), garage).show();
                })
                .item("Keisti kainà", i -> {
                    GaragePriceInputDialog.create(player, eventManager, i.getCurrentDialog(), garage).show();
                })
                .item("Keisti pickup modelá", i -> {
                    SampModelInputDialog.create(player, eventManager)
                            .caption("Garaþo áëjimo pickup modelio keitimas")
                            .parentDialog(i.getCurrentDialog())
                            .message("Áveskite naujo modelio ID.")
                            .line("Dabartinis modelis:" + garage.getPickupModelId())
                            .buttonCancel("Atgal")
                            .buttonOk("Keisti")
                            .onClickOk((d, val) -> {
                                garage.setPickupModelId(val);
                                eventManager.dispatchEvent(new GarageEditEvent(garage, player));
                                i.getCurrentDialog().show();
                            })
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .build()
                            .show();
                })
                .build();
    }
}
