package lt.ltrp.business.commodity;

import lt.ltrp.object.impl.EntityImpl;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.19.
 *
 *         This class represents a business commodity
 *         Even though the name suggests it belongs to a {@link lt.ltrp.object.Business} that's not neccesarily the case, it can also represent an available commodity for businesses to choose
 *         It should never be directly assigned to a business, {@link BusinessCommodity#clone} method should be used instead
 */
public class BusinessCommodity extends EntityImpl implements Cloneable {

    private Business business;
    private String name;
    private int price;
    private int number;

    public BusinessCommodity(int uuid, Business business, String name, int price, int number) {
        super(uuid);
        this.business = business;
        this.name = name;
        this.price = price;
        this.number = number;
    }

    public BusinessCommodity(int uuid, String name) {
        super(uuid);
        this.name = name;
    }

    public BusinessCommodity(Business business, int number, BusinessCommodity commodity) {
        this.business = business;
        this.number = number;
        this.name = commodity.getName();
        this.setUUID(commodity.getUUID());
        this.setPrice(business.getPrice());
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean onBuy(LtrpPlayer player) {
        return true;
    }


    @Override
    public BusinessCommodity clone() {
        return new BusinessCommodity(getUUID(), business, name, price, number);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BusinessCommodity && getUUID() == ((BusinessCommodity) o).getUUID();
    }
}
