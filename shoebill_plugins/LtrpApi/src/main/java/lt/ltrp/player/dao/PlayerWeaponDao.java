package lt.ltrp.player.dao;

import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.30.
 */
public interface PlayerWeaponDao {

    LtrpWeaponData[] get();
    int insert(LtrpPlayer player, LtrpWeaponData weapon);
    void remove(LtrpWeaponData weaponData);
    LtrpWeaponData[] get(LtrpPlayer player);
    void update(LtrpWeaponData weaponData);

}
