package lt.ltrp.item;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerCountdown;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class ItemCommands {



    @Command
    @CommandHelp("Sukuria molotov kokteilá - sprogstantá objektà")
    public boolean makemolotov(LtrpPlayer player) {
        Item newspaper = player.getInventory().getItem(ItemType.Newspaper);
        FuelTankItem fueltank = (FuelTankItem)player.getInventory().getItem(ItemType.Fueltank);
        if(newspaper != null && fueltank != null) {
            if(!newspaper.isStackable() || newspaper.getAmount() == 1 || fueltank.getItemCount() == 1) {
                if(player.getCountdown() == null) {
                    player.sendActionMessage(" iðsitraukia kuro bakelá, laikraðtá ir butelá....");
                    player.applyAnimation("", "");
                    PlayerCountdown playerCountdown = new PlayerCountdown(player, 2, true, new PlayerCountdown.PlayerCountdownCallback() {
                        @Override
                        public void onStop(LtrpPlayer player) {
                            player.sendActionMessage("baigia gaminti molotov");
                            fueltank.setItemCount(fueltank.getItemCount()-1);
                            player.getInventory().remove(newspaper);

                            int itemid = LtrpGamemode.getDao().getPlayerDao().obtainItemId(player, "Molotov", MolotovItem.class.getCanonicalName(), ItemType.Molotov);
                            MolotovItem item = new MolotovItem("Molotov", itemid, ItemType.Molotov);
                            player.getInventory().add(item);
                            player.clearAnimations(1);
                        }
                        @Override
                        public void onTick(LtrpPlayer player, int timeremaining) {
                            switch(timeremaining) {
                                case 1:
                                    player.sendActionMessage("paima kuro bakelá, ápyla kuro á butelá...");
                                    break;
                                case 2:
                                    player.sendActionMessage("susuka laikraðtá á rulonà");
                                    break;
                            }
                        }
                    });
                    player.setCountdown(playerCountdown);
                } else
                    player.sendErrorMessage("Jûs jau kaþkà darote.");
            } else
                player.sendErrorMessage("Jûs neturite vietos inventoriuje.");
        } else
            player.sendErrorMessage("Jûs neturite kuro arba laikraðèio.");
        return false;
    }

}
