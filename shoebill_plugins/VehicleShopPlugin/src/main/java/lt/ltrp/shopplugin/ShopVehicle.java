package lt.ltrp.shopplugin;

/**
 * @author Bebras
 *         2016.03.15.
 */
public class ShopVehicle {

    private int id;
    private int modelId;
    private int price;
    private VehicleShop shop;

    public ShopVehicle(int modelId, int price, VehicleShop shop) {
        this.modelId = modelId;
        this.price = price;
        this.shop = shop;
    }

    public ShopVehicle(int id, int modelId, int price, VehicleShop shop) {
        this.id = id;
        this.modelId = modelId;
        this.price = price;
        this.shop = shop;
    }


    public int getId() {
        return id;
    }

    public int getModelId() {
        return modelId;
    }

    public int getPrice() {
        return price;
    }

    public VehicleShop getShop() {
        return shop;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setShop(VehicleShop shop) {
        this.shop = shop;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ShopVehicle && ((ShopVehicle) obj).getId() == getId();
    }
}
