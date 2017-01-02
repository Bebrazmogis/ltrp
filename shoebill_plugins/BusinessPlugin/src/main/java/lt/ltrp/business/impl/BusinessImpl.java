package lt.ltrp.business.impl;


import lt.ltrp.constant.BusinessType;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.dialog.BusinessCommodityListDialog;
import lt.ltrp.event.property.BusinessDestroyEvent;
import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.PawnFunc;
import lt.maze.streamer.StreamerPlugin;
import lt.maze.streamer.constant.StreamerType;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicPickup;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class BusinessImpl extends AbstractProperty implements Business {

    private int money;
    private int resources;
    private int commodityLimit;
    private BusinessType type;
    private List<BusinessCommodity> commodities;
    private int resourcePrice;
    private int entrancePrice;

    public BusinessImpl(int id, String name, BusinessType type, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Color labelColor,
                        int money, int resources, int commodityLimit, EventManager eventManager) {
        super(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, eventManager);
        this.type = type;
        this.money = money;
        this.resources = resources;
        this.commodityLimit = commodityLimit;
        this.commodities = new ArrayList<>();
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
            if(Shoebill.get().getResourceManager().getPlugin(StreamerPlugin.class) != null)
                LtrpPlayer.get().stream().filter(pickup::isVisible).forEach(p -> StreamerPlugin.getInstance().update(p, getEntrance(), StreamerType.Pickup));
        } else {
            String text = String.format("%s{" + getLabelColor().toRgbHexString() + "}\nPardavimo kaina: %d$\nÁsigijimui - /buybiz", getName(), getPrice());
            entranceLabel = DynamicLabel.create(text, getLabelColor(), getEntrance());
            if(Shoebill.get().getResourceManager().getPlugin(StreamerPlugin.class) != null)
                LtrpPlayer.get().stream().filter(entranceLabel::isVisible).forEach(p -> StreamerPlugin.getInstance().update(p, getEntrance(), StreamerType.Label));
        }
    }


    @Override
    public int getMoney() {
        return money;
    }

    @Override
    public void addMoney(int i) {
        money += i;
    }

    public void setMoney(int money) {
        this.money = money;
    }


    @Override
    public BusinessType getBusinessType() {
        return type;
    }

    @Override
    public void setBusinessType(BusinessType businessType) {
        this.type = businessType;
    }

    @Override
    public int getCommodityCount() {
        return commodities.size();
    }

    @Override
    public int getCommodityLimit() {
        return commodityLimit;
    }

    @Override
    public List<BusinessCommodity> getCommodities() {
        return commodities;
    }

    @Override
    public void addCommodity(int i, BusinessCommodity businessCommodity) {
        commodities.add(i, businessCommodity);
    }

    @Override
    public void addCommodity(BusinessCommodity businessCommodity) {
        if(commodities.size() < commodityLimit) {
            commodities.add(businessCommodity);
        }
    }

    @Override
    public void removeCommodity(BusinessCommodity businessCommodity) {
        commodities.remove(businessCommodity);
    }

    @Override
    public void removeCommodity(int i) {
        commodities.remove(i);
    }

    @Override
    public int getResources() {
        return resources;
    }

    @Override
    public void setResources(int i) {
        resources = i;
    }

    @Override
    public void addResources() {
        resources += 50;
    }

    @Override
    public int getEntrancePrice() {
        return entrancePrice;
    }

    @Override
    public void setEntrancePrice(int i) {
        this.entrancePrice = i;
    }

    @Override
    public int getResourcePrice() {
        return resourcePrice;
    }

    @Override
    public void setResourcePrice(int i) {
        this.resourcePrice = i;
    }

    @Override
    public void showCommodities(LtrpPlayer ltrpPlayer) {
        BusinessCommodityListDialog.create(ltrpPlayer, eventManager, getCommodities(), null)
                .show();
    }

    @Override
    public void showCommodities(LtrpPlayer ltrpPlayer, BusinessCommodityListDialog.SelectCommodityHandler selectCommodityHandler) {
        BusinessCommodityListDialog.create(ltrpPlayer, eventManager, getCommodities(), selectCommodityHandler)
                .show();
    }

    @Override
    public DynamicPickup getPickup() {
        return pickup;
    }

    @Override
    public void destroy() {
        super.destroy();
        eventManager.dispatchEvent(new BusinessDestroyEvent(this));
        commodities.clear();
    }

    // PAWN LEGACY CODE
    public void StopAcceptCargo() {
        AmxCallable func = PawnFunc.getPublicMethod("StopAcceptingCargo");
        if(func != null)
            func.call(getUUID(), getBusinessType().ordinal());
    }
    public int GetMinCommodityPrice() {
        AmxCallable func = PawnFunc.getPublicMethod("GetMinCommodityPrice");
        if(func != null)
            return (Integer)func.call(getUUID(), getBusinessType().ordinal());
        return 0;
    }

    public int GetMaxCommodityPrice() {
        AmxCallable func = PawnFunc.getPublicMethod("GetMaxCommodityPrice");
        if(func != null)
            return (Integer)func.call(getUUID(), getBusinessType().ordinal());
        return 0;
    }
}
