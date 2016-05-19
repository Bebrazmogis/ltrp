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
    @CommandHelp("Atidaro namo inventori�")
    public boolean hInv(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house.getInventory() == null) {
            player.sendErrorMessage("ummm.. klaida?");
        } else if(!house.isOwner(player)) {
            player.sendErrorMessage("�is namas jums nepriklauso");
        } else {
            house.getInventory().show(player);
        }
        return true;
    }

    @Command()
    @CommandHelp("Nuima u�augint� �ol� namuose")
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
                    player.sendMessage(Color.FORESTGREEN, "S�kmingai nu�m�te derli�. I� viso pavyko u�auginti " + totalYield + "gramus i� " + grownSaplings.size() + " augal�.");
                    return true;
                } else
                    player.sendErrorMessage("J�s� inventorius pilnas.");
            } else
                player.sendErrorMessage("J�s� namusoe neauga �ol�.");
        } else
            player.sendErrorMessage("Tai ne j�s� namas.");
        return true;
    }

    @Command
    @CommandHelp("Atidaro nam� radijo valdymo meniu")
    public boolean hradio(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null || !house.isOwner(player)) {
            player.sendErrorMessage("Tik savininkas gali valdyti radij�!");
        } else if(!house.isUpgradeInstalled(HouseUpgradeType.Radio)) {
            player.sendErrorMessage("�iame name n�ra audio sistemos!");
        } else {
            final HouseRadio radio = house.getRadio();
            RadioOptionListDialog.create(player, eventManager,
                    (d, vol) -> {
                        radio.setVolume(vol);
                        player.sendActionMessage("Priena prie radijos ir pareguliuoja jos gars�");
                    },
                    (d, station) -> {
                        radio.play(station);
                        player.sendActionMessage("prieina prie radijos ir pakei�ia radijo stot� � " + station.getName());
                    },
                    (d) -> {
                        radio.stop();
                        player.sendActionMessage("prieina prie radijos ir j� i�jungia.");
                    })
                    .show();
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia nusiimti pinig� i� namo banko")
    public boolean houseWithdraw(Player p, @CommandParameter(name = "Suma kuri� norite paimti")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null || house.getOwner() != player.getUUID())
            player.sendErrorMessage("�is namas jums nepriklausote, tod�l negalite paimti pinig�.");
        else if(amount < 0 || amount > house.getMoney())
            player.sendErrorMessage("Tiek pinig� j�s� namo seife n�ra.");
        else {
            int oldmoney = house.getMoney();
            player.giveMoney(amount);
            house.addMoney(-amount);
            player.sendMessage(Color.HOUSE, "Pa�m�te " + amount + Currency.NAME_SHORT + " i� namo seifo, jame liko " + house.getMoney() + Currency.SYMBOL);
            eventManager.dispatchEvent(new HouseMoneyEvent(house, player, oldmoney, house.getMoney()));
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia pad�ti pinig� � nam� seif�")
    public boolean houseDeposit(Player p, @CommandParameter(name = "Suma kuri� norite pad�ti")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house.getOwner() != player.getUUID())
            player.sendErrorMessage("�is namas jums nepriklausote, tod�l negalite pad�ti pinig�.");
        else if(amount < 0 || amount > player.getMoney())
            player.sendErrorMessage("Tiek pinig� j�s neturite.");
        else {
            int oldmoney = house.getMoney();
            player.giveMoney(-amount);
            house.addMoney(+amount);
            player.sendMessage(Color.HOUSE, "Pad�jote " + amount + Currency.NAME_SHORT + " � namo seifo, dabar jame yra " + house.getMoney() + Currency.SYMBOL);
            eventManager.dispatchEvent(new HouseMoneyEvent(house, player, oldmoney, house.getMoney()));
        }
        return true;
    }

    @Command
    @CommandHelp("Patikrina namo seif�")
    public boolean houseInfo(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house.getOwner() != player.getUUID())
            player.sendErrorMessage("�is namas jums nepriklausote, tod�l negalite pad�ti pinig�.");
        else {
            player.sendActionMessage("atidaro namo seif�, suskai�iuoja jame esan�ius pinigus ir v�l j� u�daro.");
            player.sendMessage(Color.HOUSE, "Namo seife yra " + house.getMoney() + " " + Currency.NAME);
        }
        return true;
    }

    @Command
    @CommandHelp("Nustato nuomoti nam� ar ne, jei taip nuomos kain�")
    public boolean setRent(Player p, @CommandParameter(name = "Nuomos kaina, 0 - nenumuoti") int price) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        if(!house.isOwner(player))
            player.sendErrorMessage("�is namas jums nepriklauso");
        else if(price < 0)
            player.sendErrorMessage("Nuomos kaina negali b�ti neigiama, ar norite mok�ti nuomininkams?");
        else {
            house.setRentPrice(price);
            eventManager.dispatchEvent(new HouseEditEvent(house, player));
            player.sendMessage(Color.HOUSE, "Nuomos mokestis pakeistas � " + price + Currency.SYMBOL);
            house.sendTenantMessage("J�s� nuomuojamo namo savininkas " + player.getCharName() + " pakeit� nuomos mokesti � " + price + Currency.SYMBOL);
            player.playSound(1052);
        }
        return true;
    }

    @Command
    @CommandHelp("Pa�alina visu j�s� namo nuomininkus")
    public boolean evictAll(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        else if(!house.isOwner(player))
            player.sendErrorMessage("�is namas jums nepriklauso");
        else {
            house.getTenants().forEach(i -> {
                LtrpPlayer tenant = LtrpPlayer.get(i);
                if(tenant != null) {
                    tenant.sendMessage(Color.HOUSE, " * J�s buvote i�keldintas i� nuomojamo namo.");
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
            player.sendMessage(Color.HOUSE, "S�kmingai i�keldinti " + house.getTenants().size() + " nuomininkai.");
        }
        return true;
    }

    @Command
    @CommandHelp("Pa�alina nuominink� i� j�s� namo")
    public boolean evict(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer tenant) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        else if(!house.isOwner(player))
            player.sendErrorMessage("�is namas jums nepriklauso");
        else if(tenant == null || tenant.equals(player))
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        else if(!house.getTenants().contains(tenant.getUUID()))
            player.sendErrorMessage("�is �aid�jas nesinomuoja j�s� namo.");
        else {
            tenant.sendMessage(Color.HOUSE, " * J�s buvote i�keldintas i� nuomojamo namo.");
            house.getTenants().remove(tenant.getUUID());
            eventManager.dispatchEvent(new PlayerSpawnLocationChangeEvent(player, SpawnData.DEFAULT));
            player.sendMessage(Color.HOUSE, "Nuomininkas " + tenant.getCharName() + " i�keldintas.");
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo j�s� nam� nuomuojan�i� �moni� s�ra��")
    public boolean tenantry(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            return false;
        else if(!house.isOwner(player))
            player.sendErrorMessage("�is namas jums nepriklauso");
        else if(house.getTenants().size() == 0)
            player.sendErrorMessage("J�s� namo niekas nesinomuoja!");
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
            player.sendErrorMessage("J�s per toli nuo dur�!");
        else {
            house.setLocked(!house.isLocked());
            player.sendActionMessage("�ki�a rakt� � dur� spyn�, nestipriai j� pasuka");
            if(house.isLocked()) {
                house.sendActionMessage("durys u�sirakina");
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
    @CommandHelp("Pasi�lo kitam �aid�jui pirkti j�s� nam�")
    public boolean sellHouse(Player p, @CommandParameter(name = "�aid�joID/Dalis vardo")LtrpPlayer target,
                             @CommandParameter(name = "Verslo kaina")int price) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.getClosest(player.getLocation(), 8f);
        if(target == null) {
            return false;
        } else if(house == null || !house.isOwner(player))
            player.sendErrorMessage("J�s nestovite prie namo arba jis jums nepriklauso!");
        else if(player.getDistanceToPlayer(target) > 10f)
            player.sendErrorMessage("�aid�jas yra per toli!");
        else if(price < 0)
            player.sendErrorMessage("Kaina negali b�ti neigiama!");
        else if(player.getIp().equals(target.getIp()))
            player.sendErrorMessage("Negalite parduoti namo savo vartotojui.");
        else if(target.containsOffer(BuyHouseOffer.class))
            player.sendErrorMessage("�iam �aid�jui jau ka�kas si�lo pirkti versl�, palaukite.");
        else {
            BuyHouseOffer offer = new BuyHouseOffer(target, player, house, price, eventManager);
            target.getOffers().add(offer);
            player.sendMessage(Color.HOUSE, "Pasi�lymas pirkti j�s� nam� u� " + price + Currency.SYMBOL + " " + target.getName() + " i�si�stas");
            target.sendMessage(Color.HOUSE, "�aid�jas " + player.getName() + " si�lo jums pirkti jo nam� u� " + price + Currency.SYMBOL + ". Ra�ykite /accept huose nor�dami j� pirkti.");
        }
        return true;
    }


}
