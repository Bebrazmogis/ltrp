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
                            player.sendActionMessage("prpildo " + vehicle.getModelName() + " kuro bak�.");
                        } else {
                            this.setItemCount(this.getItemCount() - (int)(vehicle.getFuelTank().getSize() - vehicle.getFuelTank().getFuel()));
                            vehicle.getFuelTank().setFuel(vehicle.getFuelTank().getSize());
                            player.sendActionMessage("papildo " + vehicle.getModelName() + " kuro bak�.");
                        }
                    } else
                        player.sendErrorMessage("Transporto priemon�s bakas jau yra pilnas");
                } else
                    player.sendErrorMessage("Transporto priemon� u�rakinta");
            } else
                player.sendErrorMessage("Negalite b�ti transporto priemon�je � kuri� norite �pilti kuro.");
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
