package lt.ltrp.command;

import lt.ltrp.HouseController;
import lt.ltrp.PlayerController;
import lt.ltrp.constant.Currency;
import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.constant.ItemType;
import lt.ltrp.data.*;
import lt.ltrp.dialog.radio.RadioOptionListDialog;
import lt.ltrp.event.player.PlayerSpawnLocationChangeEvent;
import lt.ltrp.event.property.house.HouseEditEvent;
import lt.ltrp.event.property.house.HouseLockToggleEvent;
import lt.ltrp.event.property.house.HouseMoneyEvent;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeedItem;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class HouseOwnerCommands {

    private EventManager eventManager;

    public HouseOwnerCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean bc(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        cmd = cmd.toLowerCase();
        if(player != null && house != null) {
            if(house.getOwner() == player.getUUID())
                return true;

            player.sendErrorMessage("/" + cmd + " gali naudoti tik namo savininkas.");
        }
        return false;
    }


    @Command
    @CommandHelp("Atidaro namo inventoriø")
    public boolean hInv(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house.getInventory() == null) {
            player.sendErrorMessage("ummm.. klaida?");
        } else if(!house.isOwner(player)) {
            player.sendErrorMessage("Ðis namas jums nepriklauso");
        } else {
            house.getInventory().show(player);
        }
        return true;
    }

    @Command()
    @CommandHelp("Nuima uþaugintà þolæ namuose")
    public boolean cutWeed(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house != null && house.isOwner(player)) {
            if(house.getWeedSaplings().size() != 0) {
                if(!player.getInventory().isFull() || player.getInventory().containsType(ItemType.Weed)) {
                    int totalYield = 0;
                    List<HouseWeedSapling> grownSaplings = new ArrayList<>();
                    for(HouseWeedSapling sapling : house.getWeedSaplings()) {
                        totalYield += sapling.getYield();
                        sapling.setHarvestedByUser(player.getUUID());
                        grownSaplings.add(sapling);
                        sapling.destroy();
                    }
                    //house.getWeedSaplings().removeAll(grownSaplings);
                    grownSaplings.forEach(sapling -> HouseController.get().getHouseDao().update(sapling));


                    WeedItem weed = WeedItem.create(eventManager);
                    weed.setAmount(totalYield);
                    player.getInventory().add(weed);
                    player.sendMessage(Color.FORESTGREEN, "Sëkmingai nuëmëte derliø. Ið viso pavyko uþauginti " + totalYield + "gramus ið " + grownSaplings.size() + " augalø.");
                    return true;
                } else
                    player.sendErrorMessage("Jûsø inventorius pilnas.");
            } else
                player.sendErrorMessage("Jûsø namusoe neauga þolë.");
        } else
            player.sendErrorMessage("Tai ne jûsø namas.");
        return true;
    }

    @Command
    @CommandHelp("Atidaro namø radijo valdymo meniu")
    public boolean hradio(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null || !house.isOwner(player)) {
            player.sendErrorMessage("Tik savininkas gali valdyti radijà!");
        } else if(!house.isUpgradeInstalled(HouseUpgradeType.Radio)) {
            player.sendErrorMessage("Ðiame name nëra audio sistemos!");
        } else {
            final HouseRadio radio = house.getRadio();
            RadioOptionListDialog.create(player, eventManager,
                    (d, vol) -> {
                        radio.setVolume(vol);
                        player.sendActionMessage("Priena prie radijos ir pareguliuoja jos garsà");
                    },
                    (d, station) -> {
                        radio.play(station);
                        player.sendActionMessage("prieina prie radijos ir pakeièia radijo stotá á " + station.getName());
                    },
                    (d) -> {
                        radio.stop();
                        player.sendActionMessage("prieina prie radijos ir jà iðjungia.");
                    })
                    .show();
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia nusiimti pinigø ið namo banko")
    public boolean houseWithdraw(Player p, @CommandParameter(name = "Suma kurià norite paimti")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null || house.getOwner() != player.getUUID())
            player.sendErrorMessage("Ðis namas jums nepriklausote, todël negalite paimti pinigø.");
        else if(amount < 0 || amount > house.getMoney())
            player.sendErrorMessage("Tiek pinigø jûsø namo seife nëra.");
        else {
            int oldmoney = house.getMoney();
            player.giveMoney(amount);
            house.addMoney(-amount);
            player.sendMessage(Color.HOUSE, "Paëmëte " + amount + Currency.NAME_SHORT + " ið namo seifo, jame liko " + house.getMoney() + Currency.SYMBOL);
            eventManager.dispatchEvent(new HouseMoneyEvent(house, player, oldmoney, house.getMoney()));
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia padëti pinigø á namø seifà")
    public boolean houseDeposit(Player p, @CommandParameter(name = "Suma kurià norite padëti")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house.getOwner() != player.getUUID())
            player.sendErrorMessage("Ðis namas jums nepriklausote, todël negalite padëti pinigø.");
        else if(amount < 0 || amount > player.getMoney())
            player.sendErrorMessage("Tiek pinigø jûs neturite.");
        else {
            int oldmoney = house.getMoney();
            player.giveMoney(-amount);
            house.addMoney(+amount);
            player.sendMessage(Color.HOUSE, "Padëjote " + amount + Currency.NAME_SHORT + " á namo seifo, dabar jame yra " + house.getMoney() + Currency.SYMBOL);
            eventManager.dispatchEvent(new HouseMoneyEvent(house, player, oldmoney, house.getMoney()));
        }
        return true;
    }

    @Command
    @CommandHelp("Patikrina namo seifà")
    public boolean houseInfo(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house.getOwner() != player.getUUID())
            player.sendErrorMessage("Ðis namas jums nepriklausote, todël negalite padëti pinigø.");
        else {
            player.sendActionMessage("atidaro namo seifà, suskaièiuoja jame esanèius pinigus ir vël já uþdaro.");
            player.sendMessage(Color.HOUSE, "Namo seife yra " + house.getMoney() + " " + Currency.NAME);
        }
        return true;
    }

    @Command
    @CommandHelp("Nustato nuomoti namà ar ne, jei taip nuomos kainà")
    public boolean setRent(Player p, @CommandParameter(name = "Nuomos kaina, 0 - nenumuoti") int price) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        if(!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso");
        else if(price < 0)
            player.sendErrorMessage("Nuomos kaina negali bûti neigiama, ar norite mokëti nuomininkams?");
        else {
            house.setRentPrice(price);
            eventManager.dispatchEvent(new HouseEditEvent(house, player));
            player.sendMessage(Color.HOUSE, "Nuomos mokestis pakeistas á " + price + Currency.SYMBOL);
            house.sendTenantMessage("Jûsø nuomuojamo namo savininkas " + player.getCharName() + " pakeitë nuomos mokesti á " + price + Currency.SYMBOL);
            player.playSound(1052);
        }
        return true;
    }

    @Command
    @CommandHelp("Paðalina visu jûsø namo nuomininkus")
    public boolean evictAll(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        else if(!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso");
        else {
            house.getTenants().forEach(i -> {
                LtrpPlayer tenant = LtrpPlayer.get(i);
                if(tenant != null) {
                    tenant.sendMessage(Color.HOUSE, " * Jûs buvote iðkeldintas ið nuomojamo namo.");
                    house.getTenants().remove(tenant.getUUID());
                    eventManager.dispatchEvent(new PlayerSpawnLocationChangeEvent(tenant, SpawnData.DEFAULT));
                }
            });
            new Thread(() -> {
                house.getTenants().forEach(i -> {
                    PlayerController.get().getPlayerDao().update(i, SpawnData.DEFAULT);
                });
                house.getTenants().clear();
            }).start();
            player.sendMessage(Color.HOUSE, "Sëkmingai iðkeldinti " + house.getTenants().size() + " nuomininkai.");
        }
        return true;
    }

    @Command
    @CommandHelp("Paðalina nuomininkà ið jûsø namo")
    public boolean evict(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer tenant) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        else if(!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso");
        else if(tenant == null || tenant.equals(player))
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        else if(!house.getTenants().contains(tenant.getUUID()))
            player.sendErrorMessage("Ðis þaidëjas nesinomuoja jûsø namo.");
        else {
            tenant.sendMessage(Color.HOUSE, " * Jûs buvote iðkeldintas ið nuomojamo namo.");
            house.getTenants().remove(tenant.getUUID());
            eventManager.dispatchEvent(new PlayerSpawnLocationChangeEvent(player, SpawnData.DEFAULT));
            player.sendMessage(Color.HOUSE, "Nuomininkas " + tenant.getCharName() + " iðkeldintas.");
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo jûsø namà nuomuojanèiø þmoniø sàraðà")
    public boolean tenantry(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        else if(!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso");
        else if(house.getTenants().size() == 0)
            player.sendErrorMessage("Jûsø namo niekas nesinomuoja!");
        else {
            player.sendMessage(Color.HOUSE, "__________Nuomininkai__________");
            int count = 1;
            for (Integer integer : house.getTenants()) {
                player.sendMessage(Color.WHITE, count + " . " + PlayerController.get().getPlayerDao().getUsername(integer));
            }
        }
        return true;
    }

    @Command
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Location loc = player.getLocation();
        House house = House.getClosest(p.getLocation(), 8f);
        if(house == null)
            return false;
        if(house.getOwner() != player.getUUID())
            player.sendErrorMessage("Namas jums nepriklauso!");
        else if(house.getEntrance().distance(loc) > 3f && house.getExit() != null && house.getExit().distance(loc) > 3f)
            player.sendErrorMessage("Jûs per toli nuo durø!");
        else {
            house.setLocked(!house.isLocked());
            player.sendActionMessage("ákiða raktà á durø spynà, nestipriai já pasuka");
            if(house.isLocked()) {
                house.sendActionMessage("durys uþsirakina");
                player.sendGameText(8000, 1, "Namas ~r~uzrakintas");
            }
            else {
                house.sendActionMessage("durys atsirakina");
                player.sendGameText(8000, 1, "Namas ~g~atrakintas");
            }
            eventManager.dispatchEvent(new HouseLockToggleEvent(house, player, house.isLocked()));
        }
        return true;
    }

    @Command
    @CommandHelp("Pasiûlo kitam þaidëjui pirkti jûsø namà")
    public boolean sellHouse(Player p, @CommandParameter(name = "ÞaidëjoID/Dalis vardo")LtrpPlayer target,
                             @CommandParameter(name = "Verslo kaina")int price) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(player.getLocation(), 8f);
        if(target == null) {
            return false;
        } else if(house == null || !house.isOwner(player))
            player.sendErrorMessage("Jûs nestovite prie namo arba jis jums nepriklauso!");
        else if(player.getDistanceToPlayer(target) > 10f)
            player.sendErrorMessage("Þaidëjas yra per toli!");
        else if(price < 0)
            player.sendErrorMessage("Kaina negali bûti neigiama!");
        else if(player.getIp().equals(target.getIp()))
            player.sendErrorMessage("Negalite parduoti namo savo vartotojui.");
        else if(target.containsOffer(BuyHouseOffer.class))
            player.sendErrorMessage("Ðiam þaidëjui jau kaþkas siûlo pirkti verslà, palaukite.");
        else {
            BuyHouseOffer offer = new BuyHouseOffer(target, player, house, price, eventManager);
            target.getOffers().add(offer);
            player.sendMessage(Color.HOUSE, "Pasiûlymas pirkti jûsø namà uþ " + price + Currency.SYMBOL + " " + target.getName() + " iðsiøstas");
            target.sendMessage(Color.HOUSE, "Þaidëjas " + player.getName() + " siûlo jums pirkti jo namà uþ " + price + Currency.SYMBOL + ". Raðykite /accept huose norëdami já pirkti.");
        }
        return true;
    }


}
