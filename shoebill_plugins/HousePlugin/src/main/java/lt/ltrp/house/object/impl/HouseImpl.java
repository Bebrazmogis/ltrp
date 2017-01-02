package lt.ltrp.house.object.impl;

import lt.ltrp.house.upgrade.constant.HouseUpgradeType;
import lt.ltrp.data.Color;
import lt.ltrp.data.HouseRadio;
import lt.ltrp.house.event.HouseDestroyEvent;
import lt.ltrp.house.object.House;
import lt.ltrp.house.rent.object.HouseTenant;
import lt.ltrp.house.upgrade.data.HouseUpgrade;
import lt.ltrp.house.weed.object.HouseWeedSapling;
import lt.ltrp.object.Inventory;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.object.impl.InventoryPropertyImpl;
import lt.maze.streamer.StreamerPlugin;
import lt.maze.streamer.constant.StreamerType;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicPickup;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class HouseImpl extends InventoryPropertyImpl implements House {

    private List<HouseWeedSapling> weedSaplings;
    private Set<HouseUpgrade> upgrades;
    private List<HouseTenant> tenants;
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
        upgrades = new HashSet<>();
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

    public Set<HouseUpgrade> getUpgrades() {
        return upgrades;
    }

    @Override
    public boolean isUpgradeInstalled(HouseUpgradeType houseUpgradeType) {
        return getUpgrades().stream()
                .map(HouseUpgrade::getType)
                .collect(Collectors.toList())
                .contains(houseUpgradeType);
    }

    public void addUpgrade(HouseUpgrade up) {
        this.upgrades.add(up);
    }

    public void removeUpgrade(HouseUpgrade up) {
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

    @Override
    public int getRentPrice() {
        return rentPrice;
    }

    @Override
    public void setRentPrice(int rentPrice) {
        this.rentPrice = rentPrice;
    }

    @Override
    public Collection<HouseTenant> getTenants() {
        return tenants;
    }


    @Override
    public void sendTenantMessage(String s) {
        tenants.forEach(i -> {
            LtrpPlayer p = i.getPlayer() instanceof LtrpPlayer ? (LtrpPlayer) i.getPlayer() : LtrpPlayer.get(i.getPlayer().getUUID());
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
            LtrpPlayer.get().stream().filter(pickup::isVisible).forEach(p -> StreamerPlugin.getInstance().update(p, getEntrance(), StreamerType.Pickup));
        } else {
            String text = String.format("Parduodamas namas\nPardavimo kaina: %d$\nÁsigijimui - /buyhouse",  getPrice());
            entranceLabel = DynamicLabel.create(text, getLabelColor(), getEntrance());
            LtrpPlayer.get().stream().filter(entranceLabel::isVisible).forEach(p -> StreamerPlugin.getInstance().update(p, getEntrance(), StreamerType.Label));
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        eventManager.dispatchEvent(new HouseDestroyEvent(this));
    }
}
