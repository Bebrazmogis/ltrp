package lt.ltrp.dialog;

import lt.ltrp.ItemPlugin;
import lt.ltrp.PlayerPlugin;
import lt.ltrp.`object`.Garage
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.`object`.WeaponItem
import lt.ltrp.house.`object`.House
import lt.maze.dialog.ListDialog
import lt.maze.dialog.ListDialogItem
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.30.
 */

object ServerWeaponListDialog {

    fun create(player: LtrpPlayer, eventManager: EventManager, model: WeaponModel?): lt.maze.dialog.ListDialog {
        return ListDialog.create(player, eventManager, {
            val playerPlugin = ResourceManager.get().getPlugin(PlayerPlugin::class.java)
            val itemPlugin = ResourceManager.get().getPlugin(ItemPlugin::class.java)
            caption { "Serverio ginklø perþiûra." }
            item(ListDialogItem.create {
                itemText { "Automobiliuose esantys ginklai" }
                selectHandler {
                    Thread( {
                        val items : Array<WeaponItem>
                        if(model == null)
                            items = itemPlugin.getItemDao().getWeaponItems(LtrpVehicle::class.java)
                        else
                            items = itemPlugin.getItemDao().getWeaponItems(model, LtrpVehicle::class.java)

                        val weapons = items.map { it.weaponData }
                        showWeaponsOnSampThread(player, eventManager, weapons)
                    }).start()
                }
            })
            item(ListDialogItem.create {
                caption { "Namuose/garaþuose esantys ginklai" }
                selectHandler {
                    Thread({
                        val hitems: Array<WeaponItem>
                        val gitems: Array<WeaponItem>
                        if(model == null) {
                            hitems = itemPlugin.getItemDao().getWeaponItems(House::class.java)
                            gitems = itemPlugin.getItemDao().getWeaponItems(Garage::class.java)
                        } else {
                            hitems = itemPlugin.getItemDao().getWeaponItems(model, House::class.java)
                            gitems = itemPlugin.getItemDao().getWeaponItems(model, Garage::class.java)
                        }
                        showWeaponsOnSampThread(player, eventManager, hitems.plus(gitems).map { it.weaponData });
                    }).start();
                }
            })
            item(ListDialogItem.create {
                caption { "Þaidëjø kuprinëse esantys ginklai" }
                selectHandler {
                    Thread({
                        val items: Array<WeaponItem>
                        if(model == null)
                            items = itemPlugin.getItemDao().getWeaponItems(LtrpPlayer::class.java)
                        else
                            items = itemPlugin.getItemDao().getWeaponItems(model, LtrpPlayer::class.java)
                        showWeaponsOnSampThread(player, eventManager, items.map { it.weaponData })
                    }).start()
                }
            })
            item(ListDialogItem.create {
                caption { "Þaidëjø laikomi ginklai(darbiniai neskaièiuojami)" }
                selectHandler {
                    Thread( {
                        showWeaponsOnSampThread(player, eventManager, playerPlugin.getWeaponDao().get().toList())
                    }).start()
                }
            })
            onSelectItem { dialog, item ->
                player.sendMessage("Informacijos surinkimas gali uþtrukti. Jûs uþðaldytas, gavus duomenis galësite vël judëti.")
                player.toggleControllable(false)
            }
            buttonCancel { "Uþdaryti" }
            buttonOk { "Perþiûrëti" }
        })
    }

    fun create(player: LtrpPlayer, eventManager: EventManager): ListDialog {
        return create(player, eventManager, null)
    }

    private fun showWeaponsOnSampThread(player: LtrpPlayer, eventManager: EventManager, weapons: List<LtrpWeaponData>) {
        Shoebill.get().runOnSampThread {
            WeaponListDialog(player, eventManager, weapons.toTypedArray())
                .show()
            player.toggleControllable(true)
        }
    }
}
