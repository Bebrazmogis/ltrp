package lt.ltrp.object.impl;


import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.object.RadioItem;
import lt.ltrp.data.TaxiFare;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerInfoBox;
import lt.ltrp.data.FuelTank;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.object.PlayerTextdraw;

/**
 * @author Bebras
 *         2015.12.14.
 */
public class PlayerInfoBoxImpl implements PlayerInfoBox {

    private static final String DEFAULT_COUNTDOWN_CAPTION = "~w~Atliekama";

    private PlayerTextdraw textdraw;
    private boolean destroyed, isShown;
    private LtrpPlayer player;
    private RadioItem radioItem;
    private LtrpVehicle vehicle;
    private Integer jailSeconds, countdownSeconds;
    private TaxiFare taxiTrip;
    private String countdownCaption;


    public PlayerInfoBoxImpl(LtrpPlayer player) {
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
        this.setCountDown(DEFAULT_COUNTDOWN_CAPTION, seconds);
    }

    public void setCountDown(String caption, Integer seconds) {
        this.countdownSeconds = seconds;
        this.countdownCaption = caption;
        update();
    }

    public void setJailTime(Integer seconds) {
        this.jailSeconds = seconds;
        update();
    }

    public void setTaxiFare(TaxiFare trip) {
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
                int percent = Math.round(fueltank.getFuel() * 100 / fueltank.getSize());
                String color;
                if(percent > 66)
                    color = "~w~";
                else if(percent > 33)
                    color = "~y~";
                else
                    color = "~r~";

                int barCount = percent/5;
                StringBuilder bars = new StringBuilder();
                for(int i = 0; i < 20; i++) {
                    if(i < barCount) {
                        bars.append("I");
                    } else {
                        bars.append(".");
                    }
                }
                infoText.append("~n~~w~Degalai: " + color + "" + bars.toString());

                if(vehicle.getTaxi() != null) {
                    TaxiFare taxi = vehicle.getTaxi();
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
            infoText.append(String.format("~n~%s: ", countdownCaption));
            infoText.append(getTimeString(countdownSeconds));
        }
        if(jailSeconds != null) {
            infoText.append("~n~~r~Sedeti liko: ~w~");
            infoText.append(getTimeString(jailSeconds));
        }


        // Now that we gather all the possiible text, if there is any we show it else we hide the infobox.
        if(infoText.length() == 0) {
            if(isShown) {
                textdraw.hide();
                isShown = false;
            }
        } else {
            textdraw.setText(infoText.toString());
            if(!isShown) {
                textdraw.show();
                isShown = true;
            }
        }
    }

    private String getTimeString(int seconds) {
        int hours = seconds / 3600;
        seconds -= hours * 3600;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        if(hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if(minutes > 0){
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d", seconds);
        }
    }


    @Override
    public void destroy() {
        textdraw.hide();
        textdraw.destroy();
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
