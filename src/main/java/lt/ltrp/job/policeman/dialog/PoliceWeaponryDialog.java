package lt.ltrp.job.policeman.dialog;

import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.job.policeman.OfficerJob;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.01.03.
 */
public class PoliceWeaponryDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, OfficerJob job) {
        return ListDialog.create(player, eventManager)
                .caption(job.getName() + " ginklinë.")
                .buttonOk("Paimti")
                .buttonCancel("Iðeiti")
                .item("{FF0000}Padëti visus darbinius ginklus{FFFFFF}", () -> {
                    for(LtrpWeaponData weaponData : player.getWeapons()) {
                        if(weaponData.isJob()) {
                            return true;
                        }
                    }
                    return false;
                }, (i) -> {
                    for(LtrpWeaponData weaponData : player.getWeapons()) {
                        if(weaponData.isJob()) {
                            player.removeWeapon(weaponData);
                        }
                    }
                })
                .item("Desert Eagle - 100 kulkø", () -> !player.ownsWeapon(WeaponModel.DEAGLE), (i) -> player.giveWeapon(new LtrpWeaponData(WeaponModel.DEAGLE, 100, true)))
                .item("Fotoaparatas - 20 kadrø", () -> !player.ownsWeapon(WeaponModel.CAMERA), (i) -> player.giveWeapon(new LtrpWeaponData(WeaponModel.CAMERA, 20, true)))
                .item("Lazda", () -> !player.ownsWeapon(WeaponModel.NITESTICK), (i) -> player.giveWeapon(new LtrpWeaponData(WeaponModel.NITESTICK, 1, true)))
                .item("Aðarinës dujos", () -> !player.ownsWeapon(WeaponModel.SPRAYCAN), (i) -> player.giveWeapon(new LtrpWeaponData(WeaponModel.SPRAYCAN, 150, true)))
                .build();
    }

}
