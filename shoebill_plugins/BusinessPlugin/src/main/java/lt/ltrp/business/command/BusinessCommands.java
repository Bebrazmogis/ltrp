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
import lt.ltrp.object.LtrpPlayer;
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
        ownerCommands.add("sellbiz");

    }

    private EventManager eventManager;

    public BusinessCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business closestBiz = Business.getClosest(player.getLocation(), 5f);
        if(closestBiz != null) {
            if(insideCommands.contains(cmd.toLowerCase())) {
                if(Business.get(player) != null)
                    return true;
                else
                    player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami verslo viduje!");
            }

            if(ownerCommands.contains(cmd.toLowerCase())) {
                if(closestBiz.isOwner(player)) {
                    return true;
                } else
                    player.sendErrorMessage("Ðià komandà gali naudoti tik verslo savininkas!");
            }
            else
                return true;

        }
        return false;
    }

    @Command
    @CommandHelp("Nuperka versla")
    public boolean buyBiz(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business business = Business.getClosest(player.getLocation(), 5f);
        if(business == null || business.getOwner() != LtrpPlayer.INVALID_USER_ID) {
            player.sendErrorMessage("Prie jûsø nëra jokio verslo arba jis neparduodamas");
        } else if(business.getPrice() > player.getMoney())
            player.sendErrorMessage("Jums neuþtenka pinigø ásigyti ðá verslà");
        else {
            int price = business.getPrice();
            business.setOwner(player.getUUID());
            player.giveMoney(-price);
            LtrpWorld.get().addMoney(price);
            player.sendMessage("Sëkmingai ásigijote verslà uþ " + Currency.SYMBOL + price + ".");
            eventManager.dispatchEvent(new BusinessBuyEvent(business, player));
        }
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia pasiûlymà pirkti jûsø verslà kitam þaidëjui")
    public boolean sellBiz(Player p, @CommandParameter(name = "ÞaidëjoID/Dalis vardo")LtrpPlayer target,
                           @CommandParameter(name = "Verslo kaina")int price) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business business = Business.getClosest(player.getLocation(), 6f);
        if(target == null) {
            return false;
        } else if(business == null || !business.isOwner(player))
            player.sendErrorMessage("Jûs nestovite prie verslo arba jis jums nepriklauso!");
        else if(player.getDistanceToPlayer(target) > 10f)
            player.sendErrorMessage("Þaidëjas yra per toli!");
        else if(price < 0)
            player.sendErrorMessage("Kaina negali bûti neigiama!");
        else if(player.getIp().equals(target.getIp()))
            player.sendErrorMessage("Negalite parduoti verslo savo vartotojui.");
        else if(target.containsOffer(BuyBusinessOffer.class))
            player.sendErrorMessage("Ðiam þaidëjui jau kaþkas siûlo pirkti verslà, palaukite.");
        else {
            BuyBusinessOffer offer = new BuyBusinessOffer(target, player, eventManager, business, price);
            target.getOffers().add(offer);
            player.sendMessage(Color.BUSINESS, "Pasiûlymas pirkti jûsø verslà \"" + business.getName() + "\" uþ " + price + Currency.SYMBOL + " " + target.getName() + " iðsiøstas");
            target.sendMessage(Color.BUSINESS, "Þaidëjas " + player.getName() + " siûlo jums pirkti jo verslà uþ " + price + Currency.SYMBOL + ". Raðykite /accept business norëdami já pirkti.");
        }
        return true;
    }

    @Command
    @CommandHelp("Nustato prekiø pirkimo kainà")
    public boolean cargoPrice(Player player, @CommandParameter(name = "Prekës vieneto kaina")int price) {
        LtrpPlayer p = LtrpPlayer.Companion.get(player);
        Business business = Business.getClosest(player.getLocation(), 5f);
        if(business == null)
            return false;
        if(price > business.getMoney()) {
            p.sendErrorMessage("Jûsø versle nëra pakankamai pinigø nusipirki nei vienai prekei.");
        } else if(business.getBusinessType() == BusinessType.None) {
            p.sendErrorMessage("Jûsø verslas nereikalauja jokiø prekiø!");
        } else if(business.getResources() >= Business.MAX_RESOURCES) {
            p.sendErrorMessage("Jûsø versle daugiau prekiø nebetelpa!");
        } if(price < 0) {
            p.sendErrorMessage("Kaina turi bûti didesnë uþ 0!");
        } else {
            if(price == 0) {
                ((BusinessImpl)business).StopAcceptCargo();
                p.sendMessage(Color.BUSINESS, "Jûsø verslas nebepriims prekiø!");
            } else {
                int min = ((BusinessImpl)business).GetMinCommodityPrice();
                int max = ((BusinessImpl)business).GetMaxCommodityPrice();
                if(price > max) {
                    p.sendErrorMessage("Kaina negali bûti didesnë nei " + Currency.SYMBOL + price);
                } else if(price < min) {
                    p.sendErrorMessage("Kaina negali bûti maþesnë nei " + Currency.SYMBOL + price);
                } else {
                    business.setResourcePrice(price);
                    p.sendMessage(Color.BUSINESS, "Verslo prekiø kaina nustatyta á " + Currency.SYMBOL + price + "/produktà. ");
                    p.sendMessage(Color.BUSINESS, "Sunkveþimiø vairuotojai informuoti.");
                }
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Atidaro parduodamø verslo prekiø sàraðà")
    public boolean buy(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business business = Business.get(player);
        if(business == null)
            return false;

        if(business.getCommodityCount() != 0 && business.getBusinessType() != BusinessType.None) {
            business.showCommodities(player, (d, pp, commodity) -> {
                if(commodity == null || commodity.getPrice() > player.getMoney()) {
                    player.sendErrorMessage("Jums neuþtenka pinigø ðiai prekei, " + commodity.getName() + " kainauoja " + Currency.SYMBOL + commodity.getPrice());
                } else {
                    /*
                    if(commodity instanceof ShopBusinessCommodity) {
                        if(player.getInventory().isFull()) {
                            player.sendErrorMessage("Jûsø inventorius pilnas!");
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
                    player.sendMessage(Color.BUSINESS, "Nusipirkote " + commodity.getName() + ", prekë kainavo " + Currency.SYMBOL + commodity.getPrice());
                    business.setResources(business.getResources()-1);
                    int vat = LtrpWorld.get().getTaxes().getVAT(commodity.getPrice());
                    business.addMoney(commodity.getPrice() - vat);
                    player.giveMoney(- commodity.getPrice());
                    LtrpWorld.get().addMoney(vat);
                }
            });
        } else {
            player.sendErrorMessage("Ðiame versle nieko nëra!");
        }
        return true;
    }

    @Command
    @CommandHelp("Atidaro verslo valdymo meniu")
    public boolean biz(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business business = Business.getClosest(player.getLocation(), 5f);
        if(business == null)
            return false;
        if(!business.isOwner(player))
            player.sendErrorMessage("Ðis verslas jums nepriklauso!");
        else {
            BusinessManagementDialog.creatE(player, eventManager, business)
                .show();
        }
        return true;
    }

    @Command
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business business = Business.getClosest(p.getLocation(), 8f);
        if(business == null)
            return false;
        if(business.getOwner() != player.getUUID())
            player.sendErrorMessage("Garaþas jums nepriklauso!");
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
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business business = Business.getClosest(p.getLocation(), 8f);
        if(business == null)
            return false;
        if(business.isLocked())
            player.sendErrorMessage("Verslas uþrakintas");
        else if(business.getExit() == null)
            player.sendErrorMessage("Verslas dar neárengtas!");
        else if(!player.isInAnyVehicle()){
            player.setLocation(business.getExit());
            eventManager.dispatchEvent(new PlayerEnterBusinessEvent(business, player));
        }
        return true;
    }

    @Command
    public boolean exit(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business business = Business.get(player);
        if(business == null)
            return false;

        if(business.isLocked())
            player.sendErrorMessage("Verslas uþrakintas");
        else if(!player.isInAnyVehicle()){
            player.setLocation(business.getEntrance());
            eventManager.dispatchEvent(new PlayerExitBusinessEvent(business, player));
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia ásigyti naujø drabuþiø")
    public boolean clothes(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Business business = Business.get(player);
        if(business == null)
            return false;
        else if(business.getBusinessType() != BusinessType.ClothesShop)
            player.sendErrorMessage("Ðis verslas drabuþiø neparduoda.");
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
