package lt.ltrp.player;

import lt.ltrp.SpeedometerTd;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.item.RadioItem;
import lt.ltrp.data.Color;
import lt.ltrp.job.Taxi;
import lt.ltrp.vehicle.FuelTank;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.PlayerTextdraw;

/**
 * @author Bebras
 *         2015.12.14.
 */
public class PlayerInfoBox implements Destroyable{


    private PlayerTextdraw textdraw;
    private boolean destroyed, isShown;
    private LtrpPlayer player;
    private RadioItem radioItem;
    private LtrpVehicle vehicle;
    private Integer countdownSeconds, deathCountdownSeconds, jailSeconds;
    private Taxi taxiTrip;


    public PlayerInfoBox(LtrpPlayer player) {
        this.player = player;
        this.isShown = false;
        textdraw = PlayerTextdraw.create(player, 535.0f, 350.0f, "_");
        textdraw.setFont(TextDrawFont.FONT2); //2 );
        textdraw.setLetterSize(0.2f, 1.3f);
        textdraw.setShadowSize(0);
        textdraw.setUseBox(true);
        textdraw.setBoxColor(new Color(0x00000044));
        textdraw.setOutlineSize(0);
    }

    public void showSpeedometer(LtrpVehicle vehicle) {
        this.vehicle = vehicle;
        update();
    }

    public void hideSpeedometer() {
        this.vehicle = null;
        update();
    }

    public void setRadio(RadioItem item) {
        this.radioItem = item;
        update();
    }

    public void setCountDown(Integer seconds) {
        this.countdownSeconds = seconds;
        update();
    }

    public void setDeathTime(Integer seconds) {
        this.deathCountdownSeconds = seconds;
        update();
    }

    public void setJailTime(Integer seconds) {
        this.jailSeconds = seconds;
        update();
    }

    public void setTaxiFare(Taxi trip) {
        this.taxiTrip = trip;
        update();
    }

    public void update() {
        StringBuilder infoText = new StringBuilder();
        if(radioItem != null) {
            infoText.append(String.format("~w~R.kanalas: %.1f~n~", radioItem.getFrequency()));
        }
        if(vehicle != null) {
            infoText.append(String.format("Greitis: %d km/h~n~~w~Rida: %.0f km", vehicle.getSpeed(), vehicle.getMileage()));
            if(LtrpVehicleModel.isMotorVehicle(vehicle.getModelId())) {
                FuelTank fueltank = vehicle.getFuelTank();
                int percent = (int)fueltank.getSize() / 100 * (int)fueltank.getFuel();
                String color;
                if(percent > 66)
                    color = "~w~";
                else if(percent > 33)
                    color = "~y~";
                else
                    color = "~r~";

                int barCount = percent/5;
                StringBuilder bars = new StringBuilder();
                for(int i = 0; i < barCount; i++)
                    bars.append("|");

                infoText.append("~n~~w~Degalai: " + color + "" + bars.toString());

                if(vehicle.getTaxi() != null) {
                    Taxi taxi = vehicle.getTaxi();
                    if(player.equals(taxi.getDriver())) {
                        infoText.append("~~b~___Taksometras___");
                        int count = 0;
                        for(LtrpPlayer passenger : taxi.getPassengers()) {
                            if(passenger != null) {
                                infoText.append(String.format("s~n~~g~Keleivis #%d: $%d", count++, taxi.getPrice(passenger)));
                            }
                        }
                    } else if(taxi.isPassenger(player)) {
                        infoText.append(String.format("~n~~g~Taksometras: $%d", taxi.getPrice(player)));
                    } // else player not in vehicle. error?
                }
            }
        }
        if(countdownSeconds != null) {
            infoText.append("~n~~w~Atliekama: ");
            infoText.append(getTimeString(countdownSeconds));
        }
        if(deathCountdownSeconds != null) {
            infoText.append("~n~~w~Iki mirties: ");
            infoText.append(getTimeString(deathCountdownSeconds));
        }
        if(jailSeconds != null) {
            infoText.append("~n~~r~Sedeti liko: ~w~");
            infoText.append(getTimeString(jailSeconds));
        }


        // Now that we gather all the possiible text, if there is any we show it else we hide the infobox.
        if(infoText.length() == 0) {
            if(isShown) {
                textdraw.hide();
            }
        } else {
            textdraw.setText(infoText.toString());
            if(!isShown) {
                textdraw.show();
            }
        }
    }

    private String getTimeString(int seconds) {
        int hours = seconds / 3600;
        seconds -= hours * 3600;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        if(hours > 0) {
            return String.format("%2d:%2d:%2d", hours, minutes, seconds);
        } else if(minutes > 0){
            return String.format("%2d:%2d", minutes, seconds);
        } else {
            return String.format("%2d", seconds);
        }
    }


    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
