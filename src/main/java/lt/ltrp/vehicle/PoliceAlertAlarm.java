package lt.ltrp.vehicle;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.LocationZone;

/**
 * @author Bebras
 *         2015.12.18.
 *
 * An alarm that sends a message to all online officers aswell as the effects of {@link lt.ltrp.vehicle.SimpleAlarm}
 */
public class PoliceAlertAlarm extends SimpleAlarm {

    public PoliceAlertAlarm(String name, PlayerVehicle vehicle) {
        super(name, vehicle);
    }

    public PoliceAlertAlarm(PlayerVehicle vehicle) {
        this("Profesonali signalizacija su GPS ir PD ryðiu", vehicle);
    }

    @Override
    public int getLevel() {
        return 2;
    }


    @Override
    public void activate() {
        super.activate();
        LtrpPlayer.get().stream().filter(p -> p.getJob().getId() == 1).forEach(p -> {
            p.sendMessage(Color.LIGHTRED, "|________________Ávykio praneðimas________________|");
            p.sendMessage(Color.WHITE, "|Dispeèerinë: Automobilio signalizacija praneða apie ásilauþimà.");
            p.sendMessage(Color.WHITE, "|Vieta: Automobilio GPS imtuvas praneða, kad automobilis yra rajone " + LocationZone.getZone(getVehicle().getLocation()));
        });
    }
}
