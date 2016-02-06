package lt.ltrp.vehicle;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.PawnFunc;
import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.VehicleParam;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author Bebras
 *         2015.12.19.
 */
public class PlayerVehicleCommands {

    private static final int PARKING_SPACE_PRICE = 300;

    private static final VehicleLock[] locks = {
      new VehicleLock("Ne�inomos firm. spynos u�raktas", 120, 200),
            new VehicleLock("Originalus spynos u�raktas", 240, 500),
            new VehicleLock("Sustiprintas spynos u�raktas", 360, 1100),
            new VehicleLock("Titaninis spynos u�raktas", 480, 1600),
            new VehicleLock("Titaninis spynos u�raktas su el. rakteliu ", 600, 2100)
    };



    @Command
    public boolean v(LtrpPlayer player, String paramText) {
        if(paramText == null || paramText.isEmpty()) {
            player.sendMessage(Color.GREEN, "|______________________Tr. Priemoniu komandos ir naudojimas__________________________|");
            player.sendMessage(Color.LIGHTRED, "  KOMANDOS NAUDOJIMAS: /v [KOMANDA], pavyzd�iui: /v list");
            player.sendMessage(Color.WHITE, "  PAGRINDIN�S KOMANDOS: list, get, park, buypark, lock, find, documents ");
            //player.sendMessage(Color.WHITESMOKE, "  TR. PRIEMON�S SKOLINIMAS: dubkey takedubkey removedubs getdub ");
            player.sendMessage(Color.WHITE, "  TOBULINIMAS/TVARKYMAS: register buy buyalarm buylock buyinsurance");
            player.sendMessage(Color.WHITESMOKE, "  VALDYMAS: /trunk /trunko /bonnet /windows /seatbelt /maxspeed /vradio ");
          //  player.sendMessage(Color.WHITE, "  KITA: destroy scrap payticket faction buy ");
            player.sendMessage(Color.GREEN, "|__________________________________________________________________________________|");
        } else {
            String[] params = paramText.split(" ");
            if(params[0].equalsIgnoreCase("list")) {

            } else if(params[0].equalsIgnoreCase("get")) {
                if(params.length == 2) {
                    int number = 0;
                    try {
                        number = Integer.parseInt(params[1]);

                    } catch(NumberFormatException ignored) {}
                    if(number < 1 || number > player.getVehicleMetadata().size()) {
                        if(player.getVehicleMetadata().containsKey(number)) {
                            PlayerVehicle vehicle = LtrpGamemode.getDao().getVehicleDao().getPlayerVehicle(player.getVehicleMetadata().get(number).getKey());
                            vehicle.spawn();
                        }
                    } else
                        player.sendErrorMessage("Eil�s numeris negali b�ti ma�esnis u� 0 ar didesnis u� " + player.getVehicleMetadata().size());
                } else
                    player.sendErrorMessage("Naudojimas /v get [Eil�s numeris]");
            } else if(params[0].equalsIgnoreCase("park")) {
                PlayerVehicle vehicle;
                if(player.getVehicle() != null) {
                    vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                } else
                    vehicle = PlayerVehicle.getClosest(player, 4.0f);

                if(vehicle != null) {
                    if(player.getLoadedVehicles().containsKey(vehicle) && player.getLoadedVehicles().get(vehicle).contains(PlayerVehiclePermission.Park)) {
                        if(vehicle.getLocation().distance(vehicle.getSpawnLocation()) <= 10.0f) {
                            vehicle.despawn();
                            LtrpGamemode.getDao().getVehicleDao().update(vehicle);
                            player.sendMessage(Color.LIGHTRED, "J�s� tr. priemon� buvo s�kmingai priparkuota. Nor�dami gauti ra�ykite /v get.");
                            player.playSound(1057);
                        } else
                            player.sendErrorMessage("J�s neesate transporto priemon�s parkavimo vietoje.");
                    } else
                        player.sendErrorMessage("J�s neturite savininko leidimo priparkuoti �ios transporto priemon�s!");
                } else
                    player.sendErrorMessage("J�s neesate transporto priemon�je!");
            } else if(params[0].equalsIgnoreCase("buypark")) {
                PlayerVehicle vehicle;
                if(player.getVehicle() != null) {
                    vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                } else
                    vehicle = PlayerVehicle.getClosest(player, 4.0f);

                if(vehicle != null) {
                    if(player.getLoadedVehicles().containsKey(vehicle) && player.getLoadedVehicles().get(vehicle).contains(PlayerVehiclePermission.SetParkingSpace)) {
                        if(player.getMoney() >= PARKING_SPACE_PRICE) {
                            vehicle.setSpawnLocation(vehicle.getLocation());
                            LtrpGamemode.getDao().getVehicleDao().update(vehicle);
                            player.playSound(1057);
                            player.sendMessage(Color.LIGHTRED, "Nauja tr. priemon�s parkavimo vieta s�kmingai nustatyta. Dabar naudodami /v get, tr. priemon� gausite �ia.");
                        } else
                            player.sendErrorMessage("Parakvimo vietos keitimas kainuoja $" + PARKING_SPACE_PRICE);
                    } else
                        player.sendErrorMessage("J�s neturite savininko leidimo keisti transporto priemon�s parkavimo viet�!");
                } else
                    player.sendErrorMessage("J�s neesate transporto priemon�je!");
            } else if(params[0].equalsIgnoreCase("lock")) {
                PlayerVehicle vehicle;
                if(player.getVehicle() != null) {
                    vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                } else
                    vehicle = PlayerVehicle.getClosest(player, 4.0f);

                if(vehicle != null) {
                    if(player.getLoadedVehicles().containsKey(vehicle) && player.getLoadedVehicles().get(vehicle).contains(PlayerVehiclePermission.Lock)) {
                        if(vehicle.getState().getDoors() != VehicleParam.PARAM_ON) {
                            vehicle.getState().setDoors(VehicleParam.PARAM_OFF);
                            player.sendGameText(1, 1000, "~w~AUTOMOBILIS ~r~UZRAKINTAS");
                        } else {
                            vehicle.getState().setDoors(VehicleParam.PARAM_ON);
                            player.sendGameText(1, 1000, "~w~AUTOMOBILIS ~g~ATRAKINTAS");
                        }
                        // Send alarm sound if you ever find one
                    } else
                        player.sendErrorMessage("J�s neturite savininko leidimo keisti transporto priemon�s parkavimo viet�!");
                } else
                    player.sendErrorMessage("J�s neesate transporto priemon�je!");
            } else if(params[0].equalsIgnoreCase("find")) {
                if(params.length == 2) {
                    int number = 0;
                    try {
                        number = Integer.parseInt(params[1]);

                    } catch(NumberFormatException ignored) {}
                    if(number < 1 || number > player.getVehicleMetadata().size()) {
                        if(player.getVehicleMetadata().containsKey(number)) {
                            PlayerVehicle vehicle = null;
                            for(PlayerVehicle veh : player.getLoadedVehicles().keySet()) {
                                if(veh.getId() == player.getVehicleMetadata().get(number).getKey() && veh.isSpawned()) {
                                    vehicle = veh;
                                }
                            }

                            if(vehicle != null) {
                                if(vehicle.getAlarm() != null && vehicle.getAlarm().getClass() == PoliceAlertAlarm.class) {
                                    if(vehicle.getWorld() > 0) {

                                    } else {
                                        player.sendMessage(Color.LIGHTRED, "[GPS] J�s� tr. priemon� yra pastatyt� gara�e, kurio kordinates pa�ym�jome raudonu ta�ku");
                                        player.setCheckpoint(Checkpoint.create(new Radius(vehicle.getLocation(), 6.7f), e -> {
                                            player.sendMessage(Color.LIGHTCYAN, "Radote savo transporto priemon�");
                                            e.getCheckpoint().disable(e);
                                        }, null));
                                    }
                                } else
                                    player.sendErrorMessage("Negalite naudotis �ia galimyb�, kadangi J�s� tr.priemon�je n�ra �montuoto GPS si�stuvo.");
                            } else
                                player.sendErrorMessage("�i transporto priemon� n�ra i�spawninta.");
                        }
                    } else
                        player.sendErrorMessage("Eil�s numeris negali b�ti ma�esnis u� 0 ar didesnis u� " + player.getVehicleMetadata().size());
                } else
                    player.sendErrorMessage("Naudojimas /v find [Eil�s numeris]");
            } else if(params[0].equalsIgnoreCase("documents")) {
                if(params.length == 2) {
                    if(player.getVehicle() != null) {
                        PlayerVehicle vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                        if(vehicle != null && player.getLoadedVehicles().get(vehicle).contains(PlayerVehiclePermission.Spawn)) {
                            LtrpPlayer target;
                            int number;
                            try {
                                number = Integer.parseInt(params[1]);
                                target = LtrpPlayer.get(number);
                            } catch (NumberFormatException ignored) {
                                target = LtrpPlayer.getByPartName(params[1]);
                            }
                            if(target != null) {
                                if(player.getDistanceToPlayer(target) <= 10.0f) {
                                    player.sendActionMessage("Parodo automobilio dokumentus " + target.getCharName());
                                    target.sendMessage(Color.GREEN, "|___________________Tr. priemon�s dokumentai______________________|");
                                    target.sendMessage(Color.WHITE, "| Tr. priemon�s savininkas: %s | Tr. priemon�s modelis: " + vehicle.getModelName());
                                    target.sendMessage(Color.WHITE, "| U�rakto lygis: " + vehicle.getLock().getLevel() + " | Signalicazija: " + vehicle.getAlarm().getName());
                                    target.sendMessage(Color.WHITE, "| Draudimas: " + vehicle.getInsurance());
                                    target.sendMessage(Color.WHITE, "| Numeriai: " + vehicle.getLicense());
                                    target.sendMessage(Color.WHITE, "| Pa�eidimai: " + vehicle.getDeaths());
                                    target.sendMessage(Color.WHITE, "| Visa rida: " + vehicle.getMileage());
                                    return true;
                                } else
                                    player.sendErrorMessage("�aid�jas per toli!");
                            } else
                                player.sendErrorMessage("Tokio �aid�jo n�ra!");
                        } else
                            player.sendErrorMessage("J�s neturite teis�s naudoti �ios komandos.");
                    } else
                        player.sendErrorMessage("J�s neesate transporto priemon�je.");
                } else
                    player.sendErrorMessage("Naudojimas /v documents [�aid�jo ID/Dalis vardo]");
            } else if(params[0].equalsIgnoreCase("register")) {
                if(player.getVehicle() != null) {
                    PlayerVehicle vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                    if (vehicle != null && player.getLoadedVehicles().get(vehicle).contains(PlayerVehiclePermission.Register)) {
                        if(vehicle.getLicense() == null) {
                            String licensePlate = LtrpGamemode.getDao().getVehicleDao().generateLicensePlate();
                            vehicle.setLicense(licensePlate);
                            LtrpGamemode.getDao().getVehicleDao().update(vehicle);
                            player.sendMessage(Color.PLUM, "S�kmingai u�registravote tr. priemon� Los Santos miesto automobili� registre, J�s� tr. priemon�s numeriai: " + licensePlate);
                        } else
                            player.sendErrorMessage("�i transporto priemon� jau �registruota!");
                    } else
                        player.sendErrorMessage("J�s neturite teis�s naudoti �ios komandos.");
                }  else
                    player.sendErrorMessage("J�s neesate transporto priemon�je.");
            } else if(params[0].equalsIgnoreCase("buy")) {
                throw new NotImplementedException();
                /*AmxCallable function = PawnFunc.getNativeMethod("ShowVehicleBuyMenu");
                if(function != null) {
                    function.call(player.getId());
                }*/
            } else if(params[0].equalsIgnoreCase("buyalarm")) {
                if(player.getVehicle() != null) {
                    PlayerVehicle vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                    if (vehicle != null && player.getLoadedVehicles().get(vehicle).contains(PlayerVehiclePermission.Upgrade)) {
                        if(params.length != 2) {
                            player.sendMessage(Color.LIGHTRED, "Teisingas komandos naudojimas: /v buyalarm [id]");
                            player.sendMessage(Color.WHITE,"Pasirinkite norima signalizacijos lygi.");
                            player.sendMessage(Color.WHITE,"1. Paprasta signalizacija - $400");
                            player.sendMessage(Color.WHITE,"2. Profesonali signalizacija su GPS ir PD ry�iu - $2100");
                            player.sendMessage(Color.WHITE,"3. Pro. Signalizacija su GPS, policijos ir asmeniniu prane�ikliu - $3000");
                        } else {
                            int number = Integer.parseInt(params[1]);
                            if(number > 0 && number < 4) {
                                if(vehicle.getAlarm().getLevel() != number) {
                                    int price = 0;
                                    VehicleAlarm alarm = null;
                                    switch(number) {
                                        case 1:
                                            price = 400;
                                            alarm = new SimpleAlarm("Paprasta signalizacija", vehicle);
                                            break;
                                        case 2:
                                            price = 2100;
                                            alarm = new PersonalAlarm("Profesionali signalizacija su GPS ir PD ry�iu", vehicle);
                                            break;
                                        case 3:
                                            price = 3000;
                                            alarm = new PoliceAlertAlarm("Pro. Signalizacija su GPS, policijos ir asmeniniu prane�ikliu", vehicle);
                                            break;
                                    }
                                    if(player.getMoney() >= price) {
                                        player.giveMoney(-price);
                                        vehicle.setAlarm(alarm);
                                        LtrpGamemode.getDao().getVehicleDao().update(vehicle);
                                        player.sendMessage(Color.SIENNA, "Signalizacija s�kmingai �diegta!");
                                        return true;
                                    } else
                                        player.sendErrorMessage("Jums neu�tenka pinig�!");
                                } else
                                    player.sendErrorMessage("J�s jau turite �i� signalizacij�!");
                            } else
                                player.sendErrorMessage("Galimi signalizacijos variantai yra 1-3");
                        }
                    } else
                        player.sendErrorMessage("J�s neturite teis�s naudoti �ios komandos.");
                } else
                    player.sendErrorMessage("J�s neesate transporto priemon�je.");
            } else if(params[0].equalsIgnoreCase("buylock")) {
                if(player.getVehicle() != null) {
                    PlayerVehicle vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                    if (vehicle != null && player.getLoadedVehicles().get(vehicle).contains(PlayerVehiclePermission.Upgrade)) {
                        if (params.length != 2) {
                            int count = 1;
                            for(VehicleLock lock : locks) {
                                player.sendMessage(Color.LIGHTCYAN, String.format("%d. %s - %d", count++, lock.getName(), lock.getPrice()));
                            }
                        } else {
                            int number = Integer.parseInt(params[1]);
                            if (number > 0 && number <= locks.length) {
                                if(vehicle.getLock() != null && vehicle.getLock().getLevel() == number) {
                                    VehicleLock lock = locks[number - 1];
                                    if(player.getMoney() >= lock.getPrice()) {
                                        vehicle.setLock(lock);
                                        player.giveMoney(-lock.getPrice());
                                        LtrpGamemode.getDao().getVehicleDao().update(vehicle);
                                        player.sendMessage(Color.SIENNA, "U�raktas s�kmingai �diegtas!");
                                        return true;
                                    } else
                                        player.sendErrorMessage("Jums neu�tenka pinig�!");
                                } else
                                    player.sendErrorMessage("J�s jau turite �i� spyn�.");
                            } else
                                player.sendErrorMessage("Eil�s numeris negali b�ti ma�esnis u� 1 ar didesnis u� " + locks.length);
                        }
                    } else
                        player.sendErrorMessage("J�s neturite teis�s naudoti �ios komandos.");
                } else
                    player.sendErrorMessage("J�s neesate transporto priemon�je.");
            } else if(params[0].equalsIgnoreCase("buyinsurance")) {
                if(player.getVehicle() != null) {
                    PlayerVehicle vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                    if(vehicle != null && player.getLoadedVehicles().get(vehicle).contains(PlayerVehiclePermission.Upgrade)) {
                        int price = vehicle.getDeaths() * 300 + vehicle.getInsurance() * 100;
                        if(player.getMoney() >= price) {
                            vehicle.setInsurance(vehicle.getInsurance()+1);
                            player.giveMoney(-price);
                            LtrpGamemode.getDao().getVehicleDao().update(vehicle);
                            player.sendMessage(Color.SIENNA, "Draudimo prat�simas vienieriems metams Jums kainavo $" + price);
                            return true;
                        } else
                            player.sendErrorMessage("Jums neu�tenka pinig�!");
                    } else
                        player.sendErrorMessage("J�s neturite teis�s naudoti �ios komandos.");
                } else
                    player.sendErrorMessage("J�s neesate transporto priemon�je.");
            }
        }
        return false;
    }

}
