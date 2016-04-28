package lt.ltrp.object.impl;

import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.data.Color;
import lt.ltrp.data.HouseRadio;
import lt.ltrp.data.HouseWeedSapling;
import lt.ltrp.object.House;
import lt.ltrp.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import lt.maze.streamer.StreamerPlugin;
import lt.maze.streamer.constant.StreamerType;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicPickup;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class HouseImpl extends InventoryPropertyImpl implements House {

    private List<HouseWeedSapling> weedSaplings;
    private Collection<HouseUpgradeType> upgrades;
    private List<Integer> tenants;
    private HouseRadio radio;
    private int money;
    private int rentPrice;


    public HouseImpl(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit,
                     Color labelColor, int money, int rentprice, EventManager eventManager) {
        super(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, eventManager);
        this.money = money;
        this.rentPrice = rentprice;
        radio = new HouseRadio(this, eventManager);
        weedSaplings = new ArrayList<>();
        setInventory(Inventory.create(eventManager, this, "Namo daiktai", 20));
        upgrades = new ArrayList<>();
        this.tenants = new ArrayList<>();
    }

    public HouseRadio getRadio() {
        return radio;
    }

    public List<HouseWeedSapling> getWeedSaplings() {
        return weedSaplings;
    }

    public void setWeedSaplings(List<HouseWeedSapling> weedSaplings) {
        this.weedSaplings = weedSaplings;
    }

    public Collection<HouseUpgradeType> getUpgrades() {
        return upgrades;
    }

    @Override
    public boolean isUpgradeInstalled(HouseUpgradeType houseUpgradeType) {
        return getUpgrades().contains(houseUpgradeType);
    }

    public void addUpgrade(HouseUpgradeType up) {
        this.upgrades.add(up);
    }

    public void removeUpgrade(HouseUpgradeType up) {
        this.upgrades.remove(up);
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public void addMoney(int i) {
        setMoney(getMoney() + i);
    }

    public int getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(int rentPrice) {
        this.rentPrice = rentPrice;
    }

    @Override
    public List<Integer> getTenants() {
        return tenants;
    }

    @Override
    public void sendTenantMessage(String s) {
        tenants.forEach(i -> {
            LtrpPlayer p = LtrpPlayer.get(i);
            if(p != null) p.sendMessage(Color.HOUSE, s);
        });
    }

    @Override
    protected void update() {
        if(entranceLabel != null) {
            entranceLabel.destroy();
            entranceLabel = null;
        }
        if(pickup != null) {
            pickup.destroy();
            pickup = null;
        }
        if(getOwner() != LtrpPlayer.INVALID_USER_ID)  {
            pickup = DynamicPickup.create(getPickupModelId(), 1, getEntrance());
            LtrpPlayer.get().stream().filter(entranceLabel::isVisible).forEach(p -> StreamerPlugin.getInstance().update(p, getEntrance(), StreamerType.Pickup));
        } else {
            String text = String.format("Parduodamas namas\nPardavimo kaina: %d$\\nÁsigijimui - /buyhouse",  getPrice());
            entranceLabel = DynamicLabel.create(text, getLabelColor(), getEntrance());
            LtrpPlayer.get().stream().filter(entranceLabel::isVisible).forEach(p -> StreamerPlugin.getInstance().update(p, getEntrance(), StreamerType.Label));
        }
    }
}
