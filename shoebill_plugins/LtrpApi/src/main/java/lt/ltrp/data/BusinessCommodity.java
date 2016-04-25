package lt.ltrp.data;

import lt.ltrp.EntityImpl;
import lt.ltrp.object.Business;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessCommodity extends EntityImpl {

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

    //public abstract void onBuy(LtrpPlayer player);

    @Override
    public boolean equals(Object o) {
        return o instanceof BusinessCommodity && getUUID() == ((BusinessCommodity) o).getUUID();
    }
}
