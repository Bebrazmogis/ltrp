package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class FuelTankItem extends ContainerItem {



    public FuelTankItem(String name, int id, int durability, int maxdurability) {
        super(name, id, ItemType.Fueltank, false, durability, maxdurability);
    }

    @ItemUsageOption(name = "Naudoti")
    public boolean use(LtrpPlayer player, Inventory inventory) {
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 3.0f);
        if(vehicle != null) {
            if(player.getVehicle() == vehicle) {
                if(!vehicle.isLocked()) {
                    if(!vehicle.getFuelTank().isFull()) {
                        // Jeigu bus sunaudotas visas bakelis kuro
                        if(vehicle.getFuelTank().getFuel() + this.getItemCount() <= vehicle.getFuelTank().getSize()) {
                            vehicle.getFuelTank().addFuel(this.getItemCount());
                            this.setItemCount(0);
                            player.sendActionMessage("prpildo " + vehicle.getModelName() + " kuro bakà.");
                        } else {
                            this.setItemCount(this.getItemCount() - (int)(vehicle.getFuelTank().getSize() - vehicle.getFuelTank().getFuel()));
                            vehicle.getFuelTank().setFuel(vehicle.getFuelTank().getSize());
                            player.sendActionMessage("papildo " + vehicle.getModelName() + " kuro bakà.");
                        }
                    } else
                        player.sendErrorMessage("Transporto priemonës bakas jau yra pilnas");
                } else
                    player.sendErrorMessage("Transporto priemonë uþrakinta");
            } else
                player.sendErrorMessage("Negalite bûti transporto priemonëje á kurià norite ápilti kuro.");
        }
        return false;
    }

    @Override
    public void setItemCount(int count) {
        super.setItemCount(count);
        if(count == 0) {
            this.destroy();
        }
    }

}
