package lt.ltrp.data;

import net.gtaun.shoebill.data.WeaponData;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class SpawnData implements Cloneable {

    public static final SpawnData DEFAULT = new SpawnData(SpawnType.Default, 0, 104, new WeaponData[0]);

    public enum SpawnType {
        Default("Los Santos"),
        House("Nuomojamas/nuosavas namas"),
        Business("Verslas"),
        Garage("Gara�as"),
        Faction("Frakcijos b�stin�");

        String name;


        SpawnType(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
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

    @Override
    public SpawnData clone() {
        // Sadly Shoebill WeaponData does not support cloning
        WeaponData[] weapons = new WeaponData[weaponData.length];
        int i = 0;
        for(WeaponData weapon : weaponData) {
            weapons[ i++ ] = new WeaponData(weapon);
        }
        return new SpawnData(this.type, this.id, this.skin, weapons);
    }
}
