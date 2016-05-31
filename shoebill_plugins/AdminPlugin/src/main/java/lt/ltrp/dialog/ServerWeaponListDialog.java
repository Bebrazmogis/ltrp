package lt.ltrp.dialog;

import lt.ltrp.ItemPlugin;
import lt.ltrp.PlayerPlugin;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.object.*;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class ServerWeaponListDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        return create(player, eventManager, null);
    }

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, WeaponModel model) {
        PlayerPlugin playerPlugin = ResourceManager.get().getPlugin(PlayerPlugin.class);
        ItemPlugin itemPlugin = ResourceManager.get().getPlugin(ItemPlugin.class);
        return ListDialog.create(player, eventManager)
                .caption("Serverio ginklø perþiûra.")
                .item("Automobiliuose esantys ginklai", i -> {
                    new Thread(() -> {
                        WeaponItem[] items;
                        if(model == null)
                            items = itemPlugin.getItemDao().getWeaponItems(LtrpVehicle.class);
                        else
                            items = itemPlugin.getItemDao().getWeaponItems(model, LtrpVehicle.class);
                        LtrpWeaponData weaponData[] = new LtrpWeaponData[items.length];
                        int c = 0;
                        for(WeaponItem item : items)
                            weaponData[c++] = item.getWeaponData();
                        showWeaponsOnSampThread(player, eventManager, weaponData);
                    }).start();
                })
                .item("Namuose/garaþuose esantys ginklai", i -> {
                    new Thread(() -> {
                        WeaponItem[] hitems;
                        WeaponItem[] gitems;
                        if(model == null) {
                            hitems = itemPlugin.getItemDao().getWeaponItems(House.class);
                            gitems = itemPlugin.getItemDao().getWeaponItems(Garage.class);
                        } else {
                            hitems = itemPlugin.getItemDao().getWeaponItems(model, House.class);
                            gitems = itemPlugin.getItemDao().getWeaponItems(model, Garage.class);
                        }
                        LtrpWeaponData weaponData[] = new LtrpWeaponData[hitems.length + gitems.length];
                        int c = 0;
                        for(WeaponItem item : gitems)
                            weaponData[c++] = item.getWeaponData();
                        for(WeaponItem item : hitems)
                            weaponData[c++] = item.getWeaponData();
                        showWeaponsOnSampThread(player, eventManager, weaponData);
                    }).start();
                })
                .item("Þaidëjø kuprinëse esantys ginklai", i -> {
                    new Thread(() -> {
                        WeaponItem[] items;
                        if(model == null)
                            items = itemPlugin.getItemDao().getWeaponItems(LtrpPlayer.class);
                        else
                            items = itemPlugin.getItemDao().getWeaponItems(model, LtrpPlayer.class);
                        LtrpWeaponData weaponData[] = new LtrpWeaponData[items.length];
                        int c = 0;
                        for(WeaponItem item : items)
                            weaponData[c++] = item.getWeaponData();
                        showWeaponsOnSampThread(player, eventManager, weaponData);
                    }).start();
                })
                .item("Þaidëjø laikomi ginklai(darbiniai neskaièiuojami)", i -> {
                    new Thread(() -> {
                        showWeaponsOnSampThread(player, eventManager, playerPlugin.getPlayerWeaponDao().get());
                    }).start();
                })
                .buttonOk("Perþiûrëti")
                .buttonCancel("Uþdaryti.")
                .onClickOk((d, i) -> {
                    player.sendMessage("Informacijos surinkimas gali uþtrukti. Jûs uþðaldytas, gavus duomenis galësite vël judëti.");
                    player.toggleControllable(false);
                })
                .build();
    }

    private static void showWeaponsOnSampThread(LtrpPlayer player, EventManager eventManager, LtrpWeaponData[] weaponData) {
        Shoebill.get().runOnSampThread(() -> {
            new WeaponListDialog(player, eventManager, weaponData)
                    .show();
            player.toggleControllable(true);
        });
    }

}
