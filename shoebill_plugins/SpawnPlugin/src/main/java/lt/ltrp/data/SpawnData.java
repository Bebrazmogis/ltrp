package lt.ltrp.data;

import net.gtaun.shoebill.data.WeaponData;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class SpawnData {

    public static final SpawnData DEFAULT = new SpawnData(SpawnType.Default, 0, 104, new WeaponData[0]);

    public enum SpawnType {
        Default,
        House,
        Business,
        Garage,
        Faction,
    }

    private int id;
    private SpawnType type;
    private int skin;
    private WeaponData[] weaponData;

    public SpawnData() {

    }

    public SpawnData(SpawnType type, int id, int skin, WeaponData[] weaponData) {
        this.type = type;
        this.id = id;
        this.skin = skin;
        this.weaponData = weaponData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SpawnType getType() {
        return type;
    }

    public void setType(SpawnType type) {
        this.type = type;
    }

    public int getSkin() {
        return skin;
    }

    public void setSkin(int skin) {
        this.skin = skin;
    }

    public WeaponData[] getWeaponData() {
        return weaponData;
    }

    public void setWeaponData(WeaponData[] weaponData) {
        this.weaponData = weaponData;
    }
}
