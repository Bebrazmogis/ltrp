package lt.ltrp.command;

import lt.ltrp.house.HouseController;
import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.data.Color;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

public class HouseUpgradeCommands extends Commands {

    private EventManager eventManager;
    private CommandGroup group;

    public HouseUpgradeCommands(EventManager eventManager) {
        this.eventManager = eventManager;
        this.group = new CommandGroup();
        group.registerCommands(this);
        group.setUsageMessageSupplier((p, s, c) -> {
            p.sendMessage(Color.GREEN, "__________ Namo patobulinimas __________" );
            p.sendMessage(Color.LIGHTGREY, "Maistas, kaina: $8000 (( /eat ))");
            p.sendMessage(Color.LIGHTRED, "Teisingas komandos naudojimas: /hu maistas");
            return "";
        });
        group.setNotFoundHandler((p, group, cmd) -> {
            p.sendMessage(Color.GREEN, "__________ Namo patobulinimas __________" );
            p.sendMessage(Color.LIGHTGREY, "Maistas, kaina: $8000 (( /eat ))");
            p.sendMessage(Color.LIGHTRED, "Teisingas komandos naudojimas: /hu maistas");
            return true;
        });
    }

    public CommandGroup getGroup() {
        return group;
    }

    @Command
    @CommandHelp("Leid�ia pirkti atnaujinimus � nam�")
    public boolean maistas(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas name.");
        else if(!house.isOwner(player))
            player.sendErrorMessage("�is namas jums nepriklauso");

        else if(house.isUpgradeInstalled(HouseUpgradeType.Refrigerator))
            player.sendErrorMessage("�iame name jau yra �is patobulinimas.");
        else if(player.getMoney() < 8000)
            player.sendErrorMessage("Jums neu�tenka pinig�. ");
        else {
            player.giveMoney(-8000);
            int vat = LtrpWorld.get().getTaxes().getVAT(8000);
            LtrpWorld.get().addMoney(vat);
            house.addUpgrade(HouseUpgradeType.Refrigerator);
            HouseController.get().getHouseDao().update(house);
            player.sendMessage(Color.HOUSE, "� nama s�kimngai buvo �rengta maisto ruo�imo galimyb�.");
        }
        return true;
    }
}
