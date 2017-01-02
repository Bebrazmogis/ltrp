package lt.ltrp.business.command;

import lt.ltrp.BusinessController;
import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.BusinessType;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.Color;
import lt.ltrp.business.dialog.BusinessManagementDialog;
import lt.ltrp.event.property.BusinessDoorLockToggleEvent;
import lt.ltrp.event.property.PlayerEnterBusinessEvent;
import lt.ltrp.event.property.PlayerExitBusinessEvent;
import lt.ltrp.modelpreview.SkinModelPreview;
import lt.ltrp.object.Business;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.business.impl.BusinessImpl;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class BusinessCommands {

    private static Collection<String> insideCommands = new ArrayList<>();
    private static Collection<String> ownerCommands = new ArrayList<>();

    static {
        insideCommands.add("buy");
        insideCommands.add("clothes");

        ownerCommands.add("cargoprice");
        ownerCommands.add("biz");

    }

    private EventManager eventManager;

    public BusinessCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business closestBiz = Business.getClosest(player.getLocation(), 5f);
        if(closestBiz != null) {
            if(insideCommands.contains(cmd.toLowerCase())) {
                if(Business.get(player) != null)
                    return true;
                else
                    player.sendErrorMessage("�i� komand� galite naudoti tik b�dami verslo viduje!");
            }

            if(ownerCommands.contains(cmd.toLowerCase())) {
                if(closestBiz.isOwner(player)) {
                    return true;
                } else
                    player.sendErrorMessage("�i� komand� gali naudoti tik verslo savininkas!");
            }
            else
                return true;

        }
        return false;
    }

    @Command
    @CommandHelp("Nustato preki� pirkimo kain�")
    public boolean cargoPrice(Player player, @CommandParameter(name = "Prek�s vieneto kaina")int price) {
        LtrpPlayer p = LtrpPlayer.get(player);
        Business business = Business.getClosest(player.getLocation(), 5f);
        if(business == null)
            return false;
        if(price > business.getMoney()) {
            p.sendErrorMessage("J�s� versle n�ra pakankamai pinig� nusipirki nei vienai prekei.");
        } else if(business.getBusinessType() == BusinessType.None) {
            p.sendErrorMessage("J�s� verslas nereikalauja joki� preki�!");
        } else if(business.getResources() >= Business.MAX_RESOURCES) {
            p.sendErrorMessage("J�s� versle daugiau preki� nebetelpa!");
        } if(price < 0) {
            p.sendErrorMessage("Kaina turi b�ti didesn� u� 0!");
        } else {
            if(price == 0) {
                ((BusinessImpl)business).StopAcceptCargo();
                p.sendMessage(Color.BUSINESS, "J�s� verslas nebepriims preki�!");
            } else {
                int min = ((BusinessImpl)business).GetMinCommodityPrice();
                int max = ((BusinessImpl)business).GetMaxCommodityPrice();
                if(price > max) {
                    p.sendErrorMessage("Kaina negali b�ti didesn� nei " + Currency.SYMBOL + price);
                } else if(price < min) {
                    p.sendErrorMessage("Kaina negali b�ti ma�esn� nei " + Currency.SYMBOL + price);
                } else {
                    business.setResourcePrice(price);
                    p.sendMessage(Color.BUSINESS, "Verslo preki� kaina nustatyta � " + Currency.SYMBOL + price + "/produkt�. ");
                    p.sendMessage(Color.BUSINESS, "Sunkve�imi� vairuotojai informuoti.");
                }
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Atidaro parduodam� verslo preki� s�ra��")
    public boolean buy(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.get(player);
        if(business == null)
            return false;

        if(business.getCommodityCount() != 0 && business.getBusinessType() != BusinessType.None) {
            business.showCommodities(player, (d, pp, commodity) -> {
                if(commodity == null || commodity.getPrice() > player.getMoney()) {
                    player.sendErrorMessage("Jums neu�tenka pinig� �iai prekei, " + commodity.getName() + " kainauoja " + Currency.SYMBOL + commodity.getPrice());
                } else {
                    /*
                    if(commodity instanceof ShopBusinessCommodity) {
                        if(player.getInventory().isFull()) {
                            player.sendErrorMessage("J�s� inventorius pilnas!");
                        } else {
                            ShopBusinessCommodity bc = (ShopBusinessCommodity)commodity;
                            Item item = Item.create(bc.getItemType(), bc.getName(), player, eventManager);
                            player.getInventory().add(item);
                        }
                    } else {

                    }
                    */
                    boolean success = commodity.onBuy(player);
                    player.sendDebug(success, commodity);
                    player.sendMessage(Color.BUSINESS, "Nusipirkote " + commodity.getName() + ", prek� kainavo " + Currency.SYMBOL + commodity.getPrice());
                    business.setResources(business.getResources()-1);
                    int vat = LtrpWorld.get().getTaxes().getVAT(commodity.getPrice());
                    business.addMoney(commodity.getPrice() - vat);
                    player.giveMoney(- commodity.getPrice());
                    LtrpWorld.get().addMoney(vat);
                }
            });
        } else {
            player.sendErrorMessage("�iame versle nieko n�ra!");
        }
        return true;
    }

    @Command
    @CommandHelp("Atidaro verslo valdymo meniu")
    public boolean biz(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.getClosest(player.getLocation(), 5f);
        if(business == null)
            return false;
        if(!business.isOwner(player))
            player.sendErrorMessage("�is verslas jums nepriklauso!");
        else {
            BusinessManagementDialog.creatE(player, eventManager, business)
                .show();
        }
        return true;
    }

    @Command
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.getClosest(p.getLocation(), 8f);
        if(business == null)
            return false;
        if(business.getOwner() != player.getUUID())
            player.sendErrorMessage("Gara�as jums nepriklauso!");
        else {
            business.setLocked(!business.isLocked());
            if(business.isLocked())
                player.sendGameText(8000, 1, "Verslas~r~ uzrakintas");
            else
                player.sendGameText(8000, 1, "Verslas~g~ atrakintas");
            eventManager.dispatchEvent(new BusinessDoorLockToggleEvent(business, player, business.isLocked()));
        }
        return true;
    }

    @Command
    public boolean enter(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.getClosest(p.getLocation(), 8f);
        if(business == null)
            return false;
        if(business.isLocked())
            player.sendErrorMessage("Verslas u�rakintas");
        else if(business.getExit() == null)
            player.sendErrorMessage("Verslas dar ne�rengtas!");
        else if(!player.isInAnyVehicle()){
            player.setLocation(business.getExit());
            eventManager.dispatchEvent(new PlayerEnterBusinessEvent(business, player));
        }
        return true;
    }

    @Command
    public boolean exit(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.get(player);
        if(business == null)
            return false;

        if(business.isLocked())
            player.sendErrorMessage("Verslas u�rakintas");
        else if(!player.isInAnyVehicle()){
            player.setLocation(business.getEntrance());
            eventManager.dispatchEvent(new PlayerExitBusinessEvent(business, player));
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia �sigyti nauj� drabu�i�")
    public boolean clothes(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.get(player);
        if(business == null)
            return false;
        else if(business.getBusinessType() != BusinessType.ClothesShop)
            player.sendErrorMessage("�is verslas drabu�i� neparduoda.");
        else {
            SkinModelPreview.create(player, eventManager, (pv, i) -> {
                player.setSkin(i);
                business.setResources(business.getResources()-1);
                player.giveMoney(-20);
                business.addMoney(20);
                BusinessController.get().getBusinessDao().update(business);
            }).show();
        }
        return true;
    }
}
