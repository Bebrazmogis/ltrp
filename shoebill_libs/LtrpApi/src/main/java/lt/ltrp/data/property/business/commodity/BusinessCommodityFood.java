package lt.ltrp.data.property.business.commodity;

import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.30.
 */
public class BusinessCommodityFood extends BusinessCommodity {

    private float health;

    public BusinessCommodityFood(int uuid, Business business, String name, int price, int number, float health) {
        super(uuid, business, name, price, number);
        this.health = health;
    }

    public BusinessCommodityFood(int uuid, String name, float health) {
        super(uuid, name);
        this.health = health;
    }

    public BusinessCommodityFood(Business business, int number, BusinessCommodityFood commodity) {
        super(business, number, commodity);
        this.health = commodity.health;
    }

    public float getHealth() {
        return health;
    }

    @Override
    public BusinessCommodity clone() {
        return new BusinessCommodityFood(getUUID(), getBusiness(), getName(), getPrice(), getNumber(), getHealth());
    }

    @Override
    public boolean onBuy(LtrpPlayer player) {
        if(player.getHealth() < 100f) {
            player.setHealth(player.getHealth() + health);
        }
        player.sendActionMessage("suvalgo " + getName());
        return true;
    }
}
