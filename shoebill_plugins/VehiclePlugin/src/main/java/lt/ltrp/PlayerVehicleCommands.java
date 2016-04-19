package lt.ltrp;

import lt.ltrp.command.Commands;
import lt.ltrp.constant.Currency;
import lt.ltrp.constant.PlayerVehiclePermission;
import lt.ltrp.data.*;
import lt.ltrp.dialog.VehicleUserPermissionDialog;
import lt.ltrp.event.vehicle.*;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.object.PlayerVehicle;
import lt.ltrp.object.VehicleAlarm;
import lt.ltrp.shopplugin.VehicleShop;
import lt.ltrp.shopplugin.VehicleShopPlugin;
import lt.ltrp.shopplugin.dialog.VehicleShopListDialog;
import lt.ltrp.util.ErrorCode;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.19.
 */
public class PlayerVehicleCommands extends Commands {

    private static final int PARKING_SPACE_PRICE = 300;

    protected static final VehicleLock[] LOCKS = {
      new VehicleLock("Ne�inomos firm. spynos u�raktas", 120, 200),
            new VehicleLock("Originalus spynos u�raktas", 240, 500),
            new VehicleLock("Sustiprintas spynos u�raktas", 360, 1100),
            new VehicleLock("Titaninis spynos u�raktas", 480, 1600),
            new VehicleLock("Titaninis spynos u�raktas su el. rakteliu ", 600, 2100)
    };
    protected static final Pair<VehicleAlarm, Integer>[] ALARMS = new Pair[]{
            new ImmutablePair<VehicleAlarm, Integer>(VehicleAlarm.get(null, 1), 400),
            new ImmutablePair<VehicleAlarm, Integer>(VehicleAlarm.get(null, 2), 2100),
            new ImmutablePair<VehicleAlarm, Integer>(VehicleAlarm.get(null, 3), 4000)

    };

    private PlayerVehicleManager playerVehicleManager;

    public PlayerVehicleCommands(PlayerVehicleManager playerVehicleManager, CommandGroup vehicleCommandGroup) {
        logger = LoggerFactory.getLogger(PlayerVehicleCommands.class);
        this.playerVehicleManager = playerVehicleManager;
        vehicleCommandGroup.setUsageMessageSupplier((p, prefix, cmd) -> {
            logger.debug("usage message supplier cmd:" + cmd.getCommand() + " prefix:" + prefix);
            if(cmd.getCommand().equalsIgnoreCase("v buyLock")) {
                p.sendMessage(Color.GREEN, "____________________Galimos spynos___________________________");
                int i = 0;
                for(VehicleLock lock : LOCKS) {
                    p.sendMessage(Color.WHITE, String.format("%2d. %s %c%d", i++, lock.getName(), Currency.SYMBOL, lock.getPrice()));
                }
                return null;
            }
            return PlayerCommandManager.DEFAULT_USAGE_MESSAGE_SUPPLIER.get(p, prefix, cmd);
        });
    }


    @Command
    public boolean list(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        List<PlayerVehicleMetadata> metadata = new ArrayList<>();
        for(int vehicleId : playerVehicleManager.getVehicles(player)) {
            metadata.add(playerVehicleManager.getMetaData(vehicleId));
        }
        int number = 1;
        player.sendMessage(Color.GREEN, "|______________________JUMS PRIKLAUSANTIS TRANSPORTAS_____________________|");
        for(PlayerVehicleMetadata m : metadata.stream().filter(mm -> mm.getOwnerId() == player.getUUID()).collect(Collectors.toList())) {
            player.sendMessage(Color.WHITE, String.format("%d. Modelis[%s] Pa�eidimai[%d] Degal� bake[%.1f.] Numeriai[%s] Signalizacija[lvl:%d] U�raktas[lvl:%d] Draudimas[%d] I�kviesta[%s]",
                    number++,
                    VehicleModel.getName(m.getModelId()),
                    m.getDeaths(),
                    m.getFuel(),
                    m.getLicense() == null ? "neregistruota" : m.getLicense(),
                    m.getAlarm() == null ? 0 : m.getAlarm().getLevel(),
                    m.getLock() == null ? 0 : m.getLock().getLevel(),
                    m.getInsurance(),
                    playerVehicleManager.isSpawned(m.getId()) ? "TAIP" : "NE"));
        }

        player.sendMessage(Color.GREEN, "|______________________GALIMAS KT. TRANSPORTAS_____________________|");
        for(PlayerVehicleMetadata m : metadata.stream().filter(mm -> mm.getOwnerId() != player.getUUID()).collect(Collectors.toList())) {
            player.sendMessage(Color.WHITE, String.format("%d. Modelis[%s] Pa�eidimai[%d] Degal� bake[%.1f.] Numeriai[%s] Signalizacija[lvl:%d] U�raktas[lvl:%d] Draudimas[%d] I�kviesta[%s]",
                    number++,
                    VehicleModel.getName(m.getModelId()),
                    m.getDeaths(),
                    m.getFuel(),
                    m.getLicense() == null ? "neregistruota" : m.getLicense(),
                    m.getAlarm() == null ? 0 : m.getAlarm().getLevel(),
                    m.getLock() == null ? 0 : m.getLock().getLevel(),
                    m.getInsurance(),
                    playerVehicleManager.isSpawned(m.getId()) ? "TAIP" : "NE"));
        }
        return true;
    }

    @Command
    public boolean get(Player pp, @CommandParameter(name = "Eil�s Numeris")int number) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        int[] vehicles = playerVehicleManager.getVehicles(player);
        if(vehicles.length == 0) {
            player.sendErrorMessage("J�s neturite nei vienos transporto priemon�s!");
        } else if(number < 1 || number > vehicles.length) {
            player.sendErrorMessage("�iame indekse j�s neturite automobilio. Galimi numeriai: 1 - " + vehicles.length);
        } else if(PlayerVehicle.getByUniqueId(vehicles[number - 1]) != null) {
            player.sendErrorMessage("�i transporto priemon� jau i�spawninta.");
        } // TODO fines
        else if(!playerVehicleManager.getPermissions(vehicles[number-1], player).contains(PlayerVehiclePermission.Get)) {
            player.sendErrorMessage("J�s neturite teis�s atlikti �io veiksmo!");
        } else {
            PlayerVehicleArrest arrest = playerVehicleManager.getArrest(vehicles[number-1]);
            if(arrest != null) {
                player.sendErrorMessage("Negalite gauti �ios transporto priemon�s nes ji are�tuota.");
                player.sendErrorMessage("Are�to data: " + arrest.getDate() + " Prie�astis: " + arrest.getReason());
            } else if(playerVehicleManager.getSpawnedVehicles(player).size() >= playerVehicleManager.getMaxOwnedVehicles(player)) {
                player.sendErrorMessage("Negalite i�spawninti daugiau nei " + playerVehicleManager.getMaxOwnedVehicles(player) + " transporto priemoni�.");
            } else {
                PlayerVehicle vehicle = playerVehicleManager.loadVehicle(vehicles[number-1]);
                if(vehicle != null) {
                    player.setCheckpoint(Checkpoint.create(new Radius(vehicle.getSpawnLocation(), 3f), p -> {
                        player.sendMessage("Radote savo transporto priemon�.");
                        player.disableCheckpoint();
                    }, null));
                    player.sendMessage("J�s� tr. priemon� s�kmingai i�parkuota ir vieta pa�ym�ta raudonu ta�ku.");
                } else {
                    player.sendErrorMessage("�vyko klaida " + ErrorCode.PVEHICLE_LOAD_FAILD + ". Atsipra�ome u� nepatogumus.");
                    logger.error(String.format("Vehicle uid %d load failed. Shoebill vehicle count: %d. LtrpVehicle count: %d",
                            vehicles[number-1], Vehicle.get().size(), LtrpVehicle.get().size()));
                }
            }
        }
        return true;
    }

    @Command
    public boolean park(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null)
            vehicle = PlayerVehicle.getClosest(player, 5f);

        if(vehicle == null || !vehicle.getPermissions(player.getUUID()).contains(PlayerVehiclePermission.Park)) {
            player.sendErrorMessage("Prie j�s� n�ra jokios transporto priemon�s arba j�s negalite jos priparkuoti!");
        } else if(vehicle.getSpawnLocation().distance(vehicle.getLocation()) > 7f) {
            player.sendErrorMessage("Automobilis per toli nuo jo parkavimo vietos. Pakeisti parkavimo viet� galite su /v buypark");
        } else {
            playerVehicleManager.destroyVehicle(vehicle);
            player.sendMessage(" J�s� tr. priemon� buvo s�kmingai priparkuota. Nor�dami gauti ra�ykite /v get.");
            player.playSound(1057);
            playerVehicleManager.getEventManager().dispatchEvent(new PlayerVehicleParkEvent(player, vehicle));
        }
        return true;
    }

    @Command
    public boolean buyPark(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null)
            vehicle = PlayerVehicle.getClosest(player, 5f);
        if(vehicle == null || !vehicle.getPermissions(player.getUUID()).contains(PlayerVehiclePermission.SetParkingSpace)) {
            player.sendErrorMessage("Prie j�s� n�ra jokios transporto priemon�s arba j�s negalite keisti jos parkavimo vietos!");
        } else if(player.getMoney() < playerVehicleManager.getParkingSpaceCost(vehicle.getLocation())) {
            player.sendErrorMessage("Jums neu�tenka pinig�. Parkavimo vietos kaina " + Currency.SYMBOL + playerVehicleManager.getParkingSpaceCost(vehicle.getLocation()));
        } else {
            player.sendMessage("Nauja tr. priemon�s parkavimo vieta s�kmingai nustatyta. Dabar naudodami /v get, tr. priemon� gausite �ia.");
            playerVehicleManager.getEventManager().dispatchEvent(new PlayerVehicleUpdateParkEvent(player, vehicle, vehicle.getLocation()));
        }
        return true;
    }

    @Command
    public boolean scrap(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.isInAnyVehicle()) {
            player.sendErrorMessage("J�s neesate transporto priemon�je!");
        } else if(!(player.getVehicle() instanceof PlayerVehicle) ||
                !((PlayerVehicle) player.getVehicle()).getPermissions(player.getUUID()).contains(PlayerVehiclePermission.Scrap)) {
            player.sendErrorMessage("Automobilis jums nepriklauso arba neturite teis�s to daryti!");
        } else {
            PlayerVehicle vehicle = (PlayerVehicle)player.getVehicle();
            int price = playerVehicleManager.getScrapPrice(vehicle);
            MsgboxDialog.create(player, playerVehicleManager.getEventManager())
                    .caption("{FF0000}\t\tD�mesio!")
                    .message("{FFFFFF}�is veiksmas sunaikins j�s� automobil� " + vehicle.getName() +
                        "\nU� tai gausite " + Currency.SYMBOL + price +
                        "\n\n{AA1111}�io veiksmo atstatyti ne�manoma. " +
                        "\n{FFFFFF}Ar tikrai norite t�sti?")
                    .buttonOk("Taip")
                    .buttonCancel("Ne")
                    .onClickOk(d -> {
                        player.giveMoney(price);
                        player.sendMessage("J�s� pasirinkta tr. priemon� buvo sunaikinta negr��inamai.");
                        playerVehicleManager.getEventManager().dispatchEvent(new PlayerVehicleScrapEvent(player, vehicle));
                    })
                    .build()
                    .show();
        }
        return true;
    }

    @Command
    public boolean sellto(Player p, @CommandParameter(name = "�aid�jo ID/Dalis Vardo")LtrpPlayer target, @CommandParameter(name = "Kaina")int price) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.isInAnyVehicle()) {
            player.sendErrorMessage("J�s turite sed�ti transporto priemon�je!");
        } else if(!(player.getVehicle() instanceof PlayerVehicle) ||
                !((PlayerVehicle) player.getVehicle()).getPermissions(player.getUUID()).contains(PlayerVehiclePermission.Sell)) {
            player.sendErrorMessage("J�s neturite teis�s parduoti �io automobilio!");
        } else if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(target.containsOffer(BuyVehicleOffer.class)) {
            player.sendErrorMessage("�iam �aid�jui jau ka�kas si�lo transporto priemon�.");
        } else if(price <= 0) {
            player.sendErrorMessage("Kaina turi b�ti didesn� u� 0.");
        } else {
            PlayerVehicle vehicle = (PlayerVehicle)player.getVehicle();
            BuyVehicleOffer offer = new BuyVehicleOffer(target, player, playerVehicleManager.getEventManager(), vehicle, price);
            target.getOffers().add(offer);

            player.sendMessage("Pasi�lymas i�si�stas " + target.getCharName() + " laukite atsakymo.");
            target.sendMessage(player.getCharName() + " jums si�lo pirkti jo transporto priemon� \"" + vehicle.getName() + "\" u� " + Currency.SYMBOL + price + ". Naudokite /accept car arba /decline car");
        }
        return true;
    }

    @Command
    public boolean find(Player p, @CommandParameter(name = "Eil�s numeris")int number) {
        LtrpPlayer player = LtrpPlayer.get(p);
        int index = number -1;
        int[] vehicles = playerVehicleManager.getVehicles(player);
        if(index < 0 || index >= vehicles.length) {
            player.sendErrorMessage("�iame indekse j�s neturite automobilio. Galimi numeriai: 1 - " + vehicles.length);
        } else if(PlayerVehicle.getByUniqueId(vehicles[number-1]) == null) {
            player.sendErrorMessage("�i transporto priemon� n�ra i�spawninta.");
        } else {
            PlayerVehicle vehicle = PlayerVehicle.getByUniqueId(vehicles[index]);
            if(vehicle.getAlarm() == null || !vehicle.getAlarm().isFindable()) {
                player.sendErrorMessage("J�s� automobilyje n�ra GPS si�stuvo.");
            } else {
                player.setCheckpoint(Checkpoint.create(new Radius(vehicle.getLocation(), 3f), pp -> {
                    player.sendMessage("Radote savo transporto priemon�.");
                    player.disableCheckpoint();
                }, null));
                player.sendMessage("J�s� tr. priemon� pa�ym�ta raudonu ta�ku.");
            }
        }
        return true;
    }

    @Command
    public boolean lock(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null)
            vehicle = PlayerVehicle.getClosest(player, 5f);
        if(vehicle == null || !vehicle.getPermissions(player.getUUID()).contains(PlayerVehiclePermission.Lock)) {
            player.sendErrorMessage("Prie j�s� n�ra jokios transporto priemon�s arba j�s negalite keisti jos rakinti!");
        } else {
            if(vehicle.isLocked()) {
                player.sendGameText(1000, 4, "~w~AUTOMOBILIS ~g~ATRAKINTAS");
            } else {
                player.sendGameText(1000, 4, "~w~AUTOMOBILIS ~r~UZRAKINTAS");
            }
            player.playSound(1052);
            vehicle.setLocked(!vehicle.isLocked());
        }
        return true;
    }

    @Command
    public boolean documents(Player p, @CommandParameter(name = "�aid�jo ID/Dalis Vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null) {
            player.sendErrorMessage("Nor�dami atlikti �� veiksm� privalote sed�ti tr. priemon�je.");
        } else if(vehicle.getPermissions(player.getUUID()).size() == 0) {
            player.sendErrorMessage("Tai ne j�s� transporto priemon�.");
        } else if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " per toli kad jam parodytum�te automobilio dokumentus.");
        } else {
            if(!player.equals(target)) {
                player.sendActionMessage("parodo savo tr. priemon�s dokumentus " + target.getCharName());
            }
            player.sendMessage(Color.GREEN, "|___________________Tr. priemon�s dokumentai______________________|");
            player.sendMessage(Color.WHITE, "| Tr. priemon�s savininkas: " + LtrpPlayer.getPlayerDao().getUsername(vehicle.getOwnerId()) + " | Tr. priemon�s modelis: " + vehicle.getName());
            player.sendMessage(Color.WHITE, String.format("| U�rakto lygis: %s | Signalicazijos lygis: %s",
                    vehicle.getLock() == null ? "n�ra" : vehicle.getLock().getLevel(),
                    vehicle.getAlarm() == null ? "n�ra" : vehicle.getAlarm().getLevel()));
            player.sendMessage(Color.WHITE, "| Draudimas: " + vehicle.getInsurance());
            player.sendMessage(Color.WHITE, "| Numeriai: " + vehicle.getLicense());
            player.sendMessage(Color.WHITE, "| Pa�eidimai: " + vehicle.getDeaths());
            player.sendMessage(Color.WHITE, "| Visa rida: " + vehicle.getMileage());
        }
        return true;
    }

    @Command
    public boolean setpermission(Player p, @CommandParameter(name = "�aid�jo ID/Dalis Vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.isInAnyVehicle()) {
            player.sendErrorMessage("Turite b�ti transporto priemon�je kurios teises norite valdyti.");
        } else {
            PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
            if(vehicle == null || vehicle.getOwnerId() != player.getUUID()) {
                player.sendErrorMessage("�is automobilis jums nepriklauso!");
            } else if(target == null) {
                player.sendErrorMessage("Tokio �aid�jo n�ra!");
            } else if(player.getDistanceToPlayer(target) > 8f) {
                player.sendErrorMessage("�is �aid�jas yra per toli.");
            } else if(player.equals(target)) {
                player.sendErrorMessage("Savo teisi� valdyti negalite.");
            } else {
                new VehicleUserPermissionDialog(player, playerVehicleManager.getEventManager(), vehicle, target.getUUID(), target.getName())
                        .show();
            }
        }
        return true;
    }

    @Command
    public boolean managePerms(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.isInAnyVehicle()) {
            player.sendErrorMessage("Turite b�ti transporto priemon�je kurios teises norite valdyti.");
        } else {
            PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
            if(vehicle == null || vehicle.getOwnerId() != player.getUUID()) {
                player.sendErrorMessage("�is automobilis jums nepriklauso!");
            } else {
                Collection<ListDialogItem> items = new ArrayList<>();
                vehicle.getPermissions().keySet().forEach(userId -> {
                    if(userId != player.getUUID()) {
                        items.add(new ListDialogItem(LtrpPlayer.getPlayerDao().getUsername(userId), i -> {
                            new VehicleUserPermissionDialog(player, playerVehicleManager.getEventManager(), vehicle, userId, PlayerController.get().getPlayerDao().getUsername(userId)).show();
                        }));
                    }
                });
                if(items.size() == 0) {
                    player.sendErrorMessage("Niekas neturi joki� teisi� prie j�s� tr. priemon�s! Pasidalinti transporto priemone galite su /setpermission");
                } else {
                    ListDialog.create(player, playerVehicleManager.getEventManager())
                            .caption(vehicle.getName() + " vartotojai.")
                            .items(items)
                            .buttonOk("Pasirinkti")
                            .buttonCancel("U�daryti")
                            .build()
                            .show();
                }
            }
        }
        return true;
    }

    @Command
    public boolean buy(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        VehicleShopPlugin shopPlugin = playerVehicleManager.getShopPlugin();
        if(shopPlugin == null) {
            player.sendErrorMessage("Atsipra�ome, bet �iuo metu �io veiksmo �vykdyti negalima. Klaida #" + ErrorCode.SHOP_PLUGIN_DOWN);
            LtrpPlayer.sendAdminMessage(player.getName() + " klaida. #" + ErrorCode.SHOP_PLUGIN_DOWN);
        } else {
            VehicleShop shop  = shopPlugin.getClosestVehicleShop(player.getLocation(), 6f);
            if(shop == null) {
                player.sendErrorMessage("Prie j�s� n�ra transporto priemoni� parduotuv�s!");
            } else {
                VehicleShopListDialog dialog = new VehicleShopListDialog(player, playerVehicleManager.getEventManager(), shop);
                dialog.setSelectVehicleHandler((d, v) -> {
                    if(player.getMoney() < v.getPrice()) {
                        player.sendErrorMessage("Jums neu�tenka pinig�." + VehicleModel.getName(v.getModelId()) +  " kainuoja " + Currency.SYMBOL + v.getPrice());
                    } else if(playerVehicleManager.getPlayerOwnedVehicleCount(player) == playerVehicleManager.getMaxOwnedVehicles(player)) {
                        player.sendErrorMessage("J�s nebegalite tur�ti daugiau transporto priemoni�, pirmiausia parduokite senas.");
                    } else {
                        AngledLocation spawnLocation = shop.getRandomSpawnLocation();
                        Random random = new Random();
                        int color1 = random.nextInt(255);
                        int color2 = random.nextInt(255);
                        playerVehicleManager.getEventManager().dispatchEvent(new PlayerBuyNewVehicleEvent(player, v.getModelId(), spawnLocation, color1, color2, v.getPrice()));
                        player.giveMoney(-v.getPrice());
                        player.sendMessage(Color.NEWS, "S�kmingai �sigijote " + VehicleModel.getName(v.getModelId()) + " u� "  + Currency.SYMBOL + v.getPrice() + ". Per�i�r�ti transporto priemones galite /v list, gauti j� su /v get");
                    }
                });
                dialog.show();
            }
        }
        return true;
    }

    @Command
    public boolean register(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null) {
            player.sendErrorMessage("J�s turite b�ti savo transporto priemon�je!");
        } else if(!vehicle.getPermissions(player.getUUID()).contains(PlayerVehiclePermission.Register)) {
            player.sendErrorMessage("J�s neturite teis�s �registruoti �ios transporto priemon�s!");
        } else if(player.getMoney() < playerVehicleManager.getLicensePrice()) {
            player.sendErrorMessage("Jums neu�tenka pinig�. Numeri� kaina " + Currency.SYMBOL + playerVehicleManager.getLicensePrice());
        } else {
            player.giveMoney(-playerVehicleManager.getLicensePrice());
            playerVehicleManager.setLicensePlate(vehicle);
            player.sendMessage(Color.NEWS, "Automobilis �registruotas. Registracijos kaina " + Currency.SYMBOL + playerVehicleManager.getLicensePrice() + ". Automobilio numeriai: " + vehicle.getLicense());
        }
        return true;
    }

    @Command
    public boolean buyLock(Player p, @CommandParameter(name = "Spynos numeris")int number) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        int index = number -1;
        if(vehicle == null) {
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas transporto priemon�je!");
        } else if(!vehicle.getPermissions(player.getUUID()).contains(PlayerVehiclePermission.Upgrade)) {
            player.sendErrorMessage("J�s neturite teis�s to daryti!");
        } else if(index < 0 || index >= LOCKS.length) {
            player.sendErrorMessage("Galimi numeriai 1 - " + LOCKS.length);
        } else if(player.getMoney() < LOCKS[index].getPrice()) {
            player.sendErrorMessage("Jums neu�tenka pinig� �iai spynai!");
        } else if(vehicle.getLock() != null && vehicle.getLock().getLevel() > LOCKS[index].getLevel()) {
            player.sendErrorMessage("�iame automobilyje jau yra auk�tesnio lygio spyna!");
        } else {
            VehicleLock newLock = LOCKS[index];
            player.giveMoney(-newLock.getPrice());
            vehicle.setLock(newLock);
            playerVehicleManager.getEventManager().dispatchEvent(new PlayerVehicleBuyLockEvent(player, vehicle, newLock));
            player.sendMessage("� j�s� automobil� s�kmingai �diegtas \"" + newLock.getName() + "\" u�raktas. Jis kainavo " + Currency.SYMBOL + newLock.getPrice());
        }
        return true;
    }

    @Command
    public boolean buyAlarm(Player p, @CommandParameter(name = "Signalizacijos numeris")int number) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        int index = number -1;
        if(vehicle == null) {
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas transporto priemon�je!");
        } else if(!vehicle.getPermissions(player.getUUID()).contains(PlayerVehiclePermission.Upgrade)) {
            player.sendErrorMessage("J�s neturite teis�s to daryti!");
        } else if(index < 0 || index >= ALARMS.length) {
            player.sendErrorMessage("Galimi numeriai 1 - " + ALARMS.length);
        } else if(player.getMoney() < ALARMS[index].getValue()) {
            player.sendErrorMessage("Jums neu�tenka pinig� �iai signalizacijai!");
        } else if(vehicle.getAlarm() != null && vehicle.getAlarm().getLevel() > ALARMS[index].getKey().getLevel()) {
            player.sendErrorMessage("�iame automobilyje jau yra auk�tesnio lygio signalizacija!");
        } else {
            VehicleAlarm alarm = ALARMS[index].getKey();
            player.giveMoney(-ALARMS[index].getValue());
            vehicle.setAlarm(alarm);
            playerVehicleManager.getEventManager().dispatchEvent(new PlayerVehicleBuyAlarmEvent(player, vehicle, alarm));
            player.sendMessage("� j�s� automobil� s�kmingai �diegta \"" + alarm.getName() + "\" signalizacija. Ji kainavo " + Currency.SYMBOL + ALARMS[index].getValue());
        }
        return true;
    }

    @Command
    public boolean buyInsurance(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null) {
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas transporto priemon�je!");
        } else {
            int price = playerVehicleManager.getInsurancePrice(vehicle);
            if(!vehicle.getPermissions(player.getUUID()).contains(PlayerVehiclePermission.Upgrade)) {
                player.sendErrorMessage("J�s neturite teis�s to daryti!");
            } else if(player.getMoney() < price) {
                player.sendErrorMessage("Jums neu�tenka pinig�. Draudimo kaina " + Currency.SYMBOL + price);
            } else {
                player.giveMoney(-price);
                vehicle.setInsurance(vehicle.getInsurance() + 1);
                player.sendMessage(Color.NEWS, "Draudimo prat�simas vienieriems metams Jums kainavo " + Currency.SYMBOL + price);
                playerVehicleManager.getEventManager().dispatchEvent(new PlayerVehicleBuyInsuranceEvent(player, vehicle));
            }
        }
        return true;
    }
/*
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
                            PlayerVehicle vehicle = LtrpGamemodeImpl.getDao().getVehicleDao().getPlayerVehicle(player.getVehicleMetadata().get(number).getKey());
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
                            LtrpGamemodeImpl.getDao().getVehicleDao().update(vehicle);
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
                            LtrpGamemodeImpl.getDao().getVehicleDao().update(vehicle);
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
                            String licensePlate = LtrpGamemodeImpl.getDao().getVehicleDao().generateLicensePlate();
                            vehicle.setLicense(licensePlate);
                            LtrpGamemodeImpl.getDao().getVehicleDao().update(vehicle);
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
                }
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
                                        LtrpGamemodeImpl.getDao().getVehicleDao().update(vehicle);
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
                                        LtrpGamemodeImpl.getDao().getVehicleDao().update(vehicle);
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
                            LtrpGamemodeImpl.getDao().getVehicleDao().update(vehicle);
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
*/
}
