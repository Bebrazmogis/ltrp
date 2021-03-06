package lt.ltrp.object;


import net.gtaun.shoebill.entities.Destroyable;

/**
 * @author Bebras
 *         2016.04.07.
 */
public interface PlayerInfoBox extends Destroyable {

    //void showSpeedometer(LtrpVehicle vehicle);
    void hideSpeedometer();
    //void setRadio(RadioItem item);
    void setCountDown(Integer seconds);
    void setCountDown(String caption, Integer seconds);
    void setJailTime(Integer seconds);
   // void setTaxiFare(TaxiFare trip);
    void update();


}
