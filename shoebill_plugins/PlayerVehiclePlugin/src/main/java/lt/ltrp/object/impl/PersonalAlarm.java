package lt.ltrp.object.impl;

import lt.ltrp.data.Color;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;

/**
 * @author Bebras
 *         2015.12.18.
 */
public class PersonalAlarm extends PoliceAlertAlarm {

    public PersonalAlarm(String name, PlayerVehicle vehicle) {
        super(name, vehicle);
    }

    public PersonalAlarm(PlayerVehicle vehicle) {
        this("Pro. Signalizacija su GPS, policijos ir asmeniniu prane�ikliu", vehicle);
    }

    @Override
    public int getLevel() {
        return 3;
    }

    @Override
    public void activate() {
        super.activate();
        LtrpPlayer player = LtrpPlayer.get(getVehicle().getOwnerId());
        if(player != null) {
            player.sendMessage(Color.WHITE, "SMS: � J�s� automobil� bando ka�kas �silau�ti, siunt�jas: J�su Automobilis");
            player.playSound(1052);
        }
    }
}
