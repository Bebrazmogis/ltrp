package lt.ltrp.property;


import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.CommandParam;
import lt.ltrp.item.ItemType;
import lt.ltrp.item.WeedItem;
import lt.ltrp.player.LtrpPlayer;
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
    @CommandHelp("Nuima uþaugintà þolæ namuose")
    public boolean cutWeed(LtrpPlayer player) {
        House house = (House)player.getProperty();
        if(house.isOwner(player)) {
            if(house.getWeedSaplings().size() != 0) {
                if(!player.getInventory().isFull() || player.getInventory().containsType(ItemType.Weed)) {
                    int totalYield = 0;
                    List<HouseWeedSapling> grownSaplings = new ArrayList<>();
                    for(HouseWeedSapling sapling : house.getWeedSaplings()) {
                        totalYield += sapling.getYield();
                        sapling.setHarvestedByUser(player.getUserId());
                        grownSaplings.add(sapling);
                    }
                    house.getWeedSaplings().removeAll(grownSaplings);
                    grownSaplings.forEach(sapling -> LtrpGamemode.getDao().getHouseDao().updateWeed(sapling));

                    WeedItem weed = new WeedItem(eventManager, 1);
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
        return false;
    }

}

