package lt.ltrp.vehicle.speedometer.`object`.impl

import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.data.FuelTank
import lt.ltrp.vehicle.speedometer.`object`.Speedometer
import lt.ltrp.vehicle.speedometer.`object`.SpeedometerDisplay
import lt.ltrp.vehicle.speedometer.textdraw.LtrpClassicSpeedometerTextdraw
import net.gtaun.shoebill.entities.Player

/**
 * @author Bebras
 *         2015.12.14.
 *         The classic black transparent box with white text speedometer display
 */
class LtrpClassicSpeedometerDisplay(override val player: Player) : SpeedometerDisplay {

    private val textdraw = LtrpClassicSpeedometerTextdraw.create(player)
    private var speed = "0"
    private var mileage = "0.0"
    private var fuel = "|"

    override val isDestroyed: Boolean
        get() = textdraw.isDestroyed

    override fun destroy() {
        textdraw.destroy()
    }

    override fun show() {
        textdraw.show()
    }

    override fun hide() {
        textdraw.hide()
    }

    override fun updateSpeed(speed: Float) {
        this.speed = "$speed"
        update()
    }

    override fun updateMileage(mileage: Float) {
        this.mileage = String.format("%.1f", mileage)
        update()
    }

    override fun updateFuel(fuelTank: FuelTank) {
        val percent = fuelTank.size / 100 * fuelTank.fuel
        val color: String
        if(percent > 66)
            color = "~w~"
        else if(percent > 33)
            color = "~y~"
        else
            color = "~w~"

        fuel = color
        val barCount = percent / 5
        var i = 0
        while(i < barCount) {
            fuel += "|"
            i++
        }
    }

    private fun update() {
        val s = "Greitis: $speed km/h~n~~w~Rida: $mileage km~n~~w~Degalai: $fuel"
        textdraw.text = s
    }


}
    /*

    public void show(LtrpVehicle vehicle) {
        this.vehicle = vehicle;
        textdraw.show();
    }

    public void update() {
        if(vehicle != null) {
            StringBuilder speedoTextBuilder = new StringBuilder();
            speedoTextBuilder.append(String.format("Greitis: %d km/h~n~~w~Rida: %.0f km", vehicle.getSpeed(), vehicle.getMileage()));
            if(LtrpVehicleModel.isMotorVehicle(vehicle.getVehicle().getModelId())) {
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
        return textdraw.isShown();
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

}*/