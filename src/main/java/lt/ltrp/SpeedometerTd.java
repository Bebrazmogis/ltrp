package lt.ltrp;

import lt.ltrp.common.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.data.FuelTank;
import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.PlayerTextdraw;

/**
 * @author Bebras
 *         2015.12.14.
 */
public class SpeedometerTd implements Destroyable {

    private PlayerTextdraw textdraw;
    private LtrpVehicle vehicle;
    private boolean isDestroyed;


    public SpeedometerTd(LtrpPlayer player) {
        textdraw = PlayerTextdraw.create(player, 535.0f, 350.0f, "_" );
        textdraw.setFont(TextDrawFont.FONT2); //2 );
        textdraw.setLetterSize(0.2f, 1.3f);
        textdraw.setShadowSize(0);
        textdraw.setUseBox(true);
        textdraw.setBoxColor(new Color(0x00000044));
        textdraw.setOutlineSize(0);
    }



    public void show(LtrpVehicle vehicle) {
        this.vehicle = vehicle;
        textdraw.show();
    }

    public void update() {
        if(vehicle != null) {
            StringBuilder speedoTextBuilder = new StringBuilder();
            speedoTextBuilder.append(String.format("Greitis: %d km/h~n~~w~Rida: %.0f km", vehicle.getSpeed(), vehicle.getMileage()));
            if(LtrpVehicleModel.isMotorVehicle(vehicle.getModelId())) {
                FuelTank fueltank = vehicle.getFuelTank();
                int percent = (int)fueltank.getSize() / 100 * (int)fueltank.getFuel();
                String color;
                if(percent > 66)
                    color = "~w~";
                else if(percent > 33)
                    color = "~y~";
                else
                    color = "~w~";

                int barCount = percent/5;
                StringBuilder bars = new StringBuilder();
                for(int i = 0; i < barCount; i++)
                    bars.append("|");

                speedoTextBuilder.append("~n~~w~Degalai: " + bars.toString());
            }
            textdraw.setText(speedoTextBuilder.toString());
        }
    }

    public void update(int speed, float mileage) {
        String s = String.format("Greitis: %d km/h~n~~w~Rida: %.0f km", speed, mileage);
        textdraw.setText(s);
    }


    public boolean isShown() {
        return textdraw.isShowed();
    }

    public void hide() {

    }

    @Override
    public void destroy() {
        isDestroyed = true;
        textdraw.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

}
