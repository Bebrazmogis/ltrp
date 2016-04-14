package lt.ltrp.vehicle;

import lt.ltrp.vehicle.object.PlayerVehicle;

/**
 * @author Bebras
 *         2015.12.18.
 *
 * An alarm that sends a message to all online officers aswell as the effects of {@link lt.ltrp.vehicle.SimpleAlarm}
 */
public class PoliceAlertAlarm extends SimpleAlarm {

    protected PoliceAlertAlarm(String name, PlayerVehicle vehicle) {
        super(name, vehicle);
    }

    protected PoliceAlertAlarm(PlayerVehicle vehicle) {
        this("Profesonali signalizacija su GPS ir PD ry�iu", vehicle);
    }

    @Override
    public int getLevel() {
        return 2;
    }


    @Override
    public void activate() {
        super.activate();
        // TODO
       /* LtrpPlayer.get().stream().filter(p -> p.getJob().getId() == 1).forEach(p -> {
            p.sendMessage(Color.LIGHTRED, "|________________�vykio prane�imas________________|");
            p.sendMessage(Color.WHITE, "|Dispe�erin�: Automobilio signalizacija prane�a apie �silau�im�.");
            p.sendMessage(Color.WHITE, "|Vieta: Automobilio GPS imtuvas prane�a, kad automobilis yra rajone " + LocationZone.getZone(getVehicle().getLocation()));
        });*/
    }
}
