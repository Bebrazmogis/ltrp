package lt.ltrp.data;

import lt.ltrp.object.Entity;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Vector2D;
import net.gtaun.shoebill.data.WeaponData;

/**
 * @author Bebras
 *         2015.12.02.
 */
public class LtrpWeaponData extends WeaponData implements Entity {



    private int uuid;
    public boolean job;
    private boolean isDropped;


    public LtrpWeaponData(WeaponModel type, int ammo, boolean job) {
        super(type, ammo);
        this.job = job;
    }

    public LtrpWeaponData(int uuid, WeaponModel type, int ammo, boolean job) {
        super(type, ammo);
        this.job = job;
        this.uuid = uuid;
    }

    public LtrpWeaponData() {

    }


    public LtrpWeaponData(boolean job) {
        this.job = job;
    }

    public LtrpWeaponData(WeaponData data, boolean job) {
        super(data);
        this.job = job;
    }

    @Override
    public LtrpWeaponData clone() {
        return new LtrpWeaponData(this.getModel(), this.getAmmo(), this.isJob());
    }

    public boolean isJob() {
        return job;
    }

    public void setJob(boolean job) {
        this.job = job;
    }

    public boolean isDropped() {
        return isDropped;
    }

    public void setDropped(Vector2D loc) {
        this.isDropped = true;
        new DroppedWeaponData(this, loc);
    }

    @Override
    public void setUUID(int uuid) {
        this.uuid = uuid;
    }

    @Override
    public int getUUID() {
        return uuid;
    }
}
