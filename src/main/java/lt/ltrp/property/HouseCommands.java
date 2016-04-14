package lt.ltrp.property;


import lt.ltrp.LtrpGamemodeImpl;
import lt.ltrp.item.constant.ItemType;
import lt.ltrp.property.constant.HouseUpgradeType;
import lt.ltrp.property.data.HouseRadio;
import lt.ltrp.property.data.HouseWeedSapling;
import lt.ltrp.property.object.House;
import lt.ltrp.radio.dialog.RadioOptionListDialog;
import lt.ltrp.item.WeedItem;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class HouseCommands {


    private EventManager eventManager;

    public HouseCommands(EventManager e) {
        this.eventManager = e;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            if(player.getProperty() == null || !(player.getProperty() instanceof House)) {
                System.out.println("HouseCommands :: beforeChcek. Cmd " + cmd + " returning false");
                return false;
            }
        }

        return true;
    }


    @Command()
    @CommandHelp("Nuima u�augint� �ol� namuose")
    public boolean cutWeed(LtrpPlayer player) {
        House house = (House)player.getProperty();
        if(house.isOwner(player)) {
            if(house.getWeedSaplings().size() != 0) {
                if(!player.getInventory().isFull() || player.getInventory().containsType(ItemType.Weed)) {
                    int totalYield = 0;
                    List<HouseWeedSapling> grownSaplings = new ArrayList<>();
                    for(HouseWeedSapling sapling : house.getWeedSaplings()) {
                        totalYield += sapling.getYield();
                        sapling.setHarvestedByUser(player.getUUID());
                        grownSaplings.add(sapling);
                    }
                    house.getWeedSaplings().removeAll(grownSaplings);
                    grownSaplings.forEach(sapling -> LtrpGamemodeImpl.getDao().getHouseDao().updateWeed(sapling));

                    WeedItem weed = new WeedItem(eventManager, 1);
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
        return false;
    }

    @Command
    @CommandHelp("Atidaro nam� radijo valdymo meniu")
    public boolean hradio(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = (House)player.getProperty();
        if(!house.isOwner(player)) {
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
}

