package lt.ltrp.player.object;


import lt.ltrp.item.object.RadioItem;
import lt.ltrp.job.data.TaxiFare;
import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.shoebill.object.Destroyable;

/**
 * @author Bebras
 *         2016.04.07.
 */
public interface PlayerInfoBox extends Destroyable {

    void showSpeedometer(LtrpVehicle vehicle);
    void hideSpeedometer();
    void setRadio(RadioItem item);
    void setCountDown(Integer seconds);
    void setCountDown(String caption, Integer seconds);
    void setJailTime(Integer seconds);
    void setTaxiFare(TaxiFare trip);
    void update();


}
