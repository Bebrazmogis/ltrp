package lt.ltrp.job.policeman;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.StringUtils;
import lt.ltrp.command.CommandParam;
import lt.ltrp.command.Commands;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.CrimeListDialog;
import lt.ltrp.dialog.PoliceWeaponryDialog;
import lt.ltrp.dialogmenu.PoliceDatabaseMenu;
import lt.ltrp.item.Item;
import lt.ltrp.item.ItemType;
import lt.ltrp.job.JobManager;
import lt.ltrp.job.policeman.OfficerJob;
import lt.ltrp.player.LicenseWarning;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerCrime;
import lt.ltrp.player.PlayerLicense;
import lt.ltrp.plugin.streamer.DynamicLabel;
import lt.ltrp.plugin.streamer.DynamicSampObject;
import lt.ltrp.property.House;
import lt.ltrp.property.HouseWeedSapling;
import lt.ltrp.vehicle.JobVehicle;
import net.gtaun.shoebill.common.command.*;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModelInfoType;
import net.gtaun.shoebill.data.Area3D;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.12.
 */
public class PoliceCommands extends Commands{

    private static final Map<String, Integer> commandToRankNumber;
    private static final List<String> jobVehicleCommands;
    private static final List<String> jobAreaCommands;
    private static final int JOB_ID = 2;

    static {
        commandToRankNumber = new HashMap<>();

        commandToRankNumber.put("policehelp", 1);
        commandToRankNumber.put("cutdownweed", 1);
        commandToRankNumber.put("checkalco", 1);
        commandToRankNumber.put("setunit", 1);
        commandToRankNumber.put("delunit", 1);
        commandToRankNumber.put("megaphone", 1);
        commandToRankNumber.put("m", 1);
        commandToRankNumber.put("police" ,1);
        commandToRankNumber.put("mdc", 1);
        commandToRankNumber.put("cuff", 1);
        commandToRankNumber.put("drag", 1);
        commandToRankNumber.put("bk", 1);
        commandToRankNumber.put("backup", 1);
        commandToRankNumber.put("abk", 1);
        commandToRankNumber.put("take", 1);
        commandToRankNumber.put("licwarn", 1);

        commandToRankNumber.put("wepstore", 2);

        jobVehicleCommands = new ArrayList<>();
        jobVehicleCommands.add("setunit");
        jobVehicleCommands.add("delunit");
        jobVehicleCommands.add("police");

        jobAreaCommands = new ArrayList<>();
        jobAreaCommands.add("wepstore");


    }

    private OfficerJob job;
    private EventManager eventManager;
    private Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    private Map<JobVehicle, DynamicSampObject> policeSirens = new HashMap<>();
    private Map<LtrpPlayer, DragTimer> dragTimers;
    private Map<LtrpPlayer, List<LtrpPlayer>> backupRequests;
    private PlayerCommandManager playerCommandManager;


    public PoliceCommands(PlayerCommandManager cmdmanger, OfficerJob job, EventManager eventManager, Map<JobVehicle, DynamicLabel> unitLabels, Map<JobVehicle, DynamicSampObject> sirends, Map<LtrpPlayer,DragTimer> dragtimers) {
        this.playerCommandManager = cmdmanger;
        this.job = job;
        this.backupRequests = new HashMap<>();
        this.eventManager = eventManager;
        this.unitLabels = unitLabels;
        this.policeSirens = sirends;
        this.dragTimers = dragtimers;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getJob().equals(job)) {
            if(commandToRankNumber.containsKey(cmd)) {
                if(player.getJobRank().getNumber() >= commandToRankNumber.get(cmd)) {
                    if(jobVehicleCommands.contains(cmd)) {
                        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
                        if(jobVehicle != null)
                            return true;
                        else
                            player.sendErrorMessage("�i� komand� galite naudot tik b�dami darbo tranporto priemon�je");
                    } else if(jobAreaCommands.contains(cmd)) {
                        if(job.isAtWork(player)) {
                            return true;
                        }
                        player.sendErrorMessage("�i� komand� galite naudot tik b�dami darbo b�stin�je.");
                    } else {
                        return true;
                    }
                } else {
                    player.sendErrorMessage("�i� komand� gali naudoti darbuotojai kuri� rangas " + player.getJob().getRank(commandToRankNumber.get(cmd)).getName());
                }
            }
        }
        return false;
    }

    @Command
    @CommandHelp("Pareig�nh� komand� s�ra�as")
    public boolean policeHelp(Player player) {
        player.sendMessage(Color.POLICE, "|__________________" + job.getName().toUpperCase() + "__________________|");
        player.sendMessage(Color.WHITE, "  PATIKRINIMO KOMANDOS: /frisk /checkalco /fines /vehiclefines /checkspeed /mdc /take");
        player.sendMessage(Color.LIGHTGREY, "  BUD�JIMO PRAD�IOS KOMANDOS: /duty /wepstore");
        player.sendMessage(Color.WHITE, "  SU�MIMO KOMANDOS: /tazer /cuff /drag");
        player.sendMessage(Color.LIGHTGREY, "  GAUDYNI�/SITUACIJ� KOMANDOS: /bk /rb  /rrb /m");
        player.sendMessage(Color.WHITE, "  KOMANDOS NUBAUSTI: /fine /vehiclefine /arrest /prison /arrestcar /licwarn ");
        player.sendMessage(Color.LIGHTGREY, "  KITOS KOMANDOS: /flist /setunit /delunit /police /delarrestcar /jobid /cutdownweed");
        player.sendMessage(Color.WHITE, "  DRABU�IAI/APRANGA: /vest /badge /rbadge /pdclothes");
        player.sendMessage(Color.POLICE, "____________________________________________________________________________");
        return true;
    }


    @Command
    @CommandHelp("Sunaikina name esan�i� marihuana")
    public boolean cutDownWeed(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getProperty() == null || !(player.getProperty() instanceof House)) {
            player.sendErrorMessage("J8s ne name!");
        } else {
            House house = (House)player.getProperty();
            List<HouseWeedSapling> closestWeed = house.getWeedSaplings().stream().filter(weed -> weed.getLocation().distance(player.getLocation()) < 10.0f).collect(Collectors.toList());
            if(house.getWeedSaplings().size() == 0 || closestWeed.size() == 0) {
                player.sendActionMessage("apsidairo po namus...");
                player.sendErrorMessage("�iame name neauga marihuana!");
            } else  {
                closestWeed.forEach(w -> {
                    w.destroy();
                    LtrpGamemode.getDao().getHouseDao().updateWeed(w);
                });
                player.sendActionMessage("Pareig�nas " + player.getCharName() + " sunaikina " + closestWeed.size() + " marihuanos augalus.");
                return true;
            }
        }
        return false;
    }

    @Command
    @CommandHelp("Patikrina �aid�jo girtum�")
    public boolean checkalco(Player p, LtrpPlayer p2) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(p2 == null) {
            player.sendErrorMessage("Naudojimas /p [�aid�joID/Dalis vardo]");
        } else if(p.getLocation().distance(p2.getLocation()) > 10.0f) {
            player.sendErrorMessage("�i� komand� galima naudoti tik kai �aid�jas prie j�s�. " + p2.getCharName() + " yra per toli");
        } else {
            float drunkness = p2.getDrunkLevel() / 1000;
            player.sendActionMessage("prideda alkotester� prie  " + p2.getCharName() + " l�p�, kuris pripu�ia " + drunkness + " promil� (-i�).");
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Parodo �aid�jo turimas baudas")
    public boolean fines(LtrpPlayer player, LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null) {
            p.sendErrorMessage("Naudojimas /p [�aid�joID/Dalis vardo]");
        } else if(p.getLocation().distance(p2.getLocation()) > 10.0f) {
            p.sendErrorMessage("�i� komand� galima naudoti tik kai �aid�jas prie j�s�. " + p2.getCharName() + " yra per toli");
        } else {
            List<PlayerCrime> crimes = LtrpGamemode.getDao().getPlayerDao().getCrimes(p2);
            if(crimes.size() == 0) {
                p.sendErrorMessage(p2.getCharName() + " n�ra nieko padar�s.");
            } else {
                new CrimeListDialog(p, eventManager, crimes).show();
            }
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Parodo automobilio baudas")
    public boolean vehiclefines(Player player) {

        return false;
    }

    @Command
    @CommandHelp("Nustato transporto priemon�s greit�")
    public boolean checkspeed(Player player) {

        return false;
    }


    @Command
    @CommandHelp("Pa�ymi j�s� automobil� pasirinktu tekstu")
    public boolean setunit(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(jobVehicle != null || jobVehicle.getJob().getId() != JOB_ID) {
            if(!text.isEmpty()) {
                DynamicLabel label = unitLabels.get(jobVehicle);
                if(label != null) {
                    label.update(text);
                } else {
                    Vector3D offsets = VehicleModel.getModelInfo(jobVehicle.getModelId(), VehicleModelInfoType.SIZE);
                    Location location = new Location(0.0f, (-0.0f * offsets.getY()), 0.0f);
                    label = DynamicLabel.create(text, Color.WHITE, location, 15.0f, true, null, jobVehicle);
                    unitLabels.put(jobVehicle, label);
                }
                return true;
            } else
                player.sendErrorMessage("Tekstas negali b�ti tu��ias");
        } else
            player.sendErrorMessage("J�s turite b�ti darbin�je transporto priemon�je");
        return false;
    }

    @Command
    @CommandHelp("Panaikina automobilio tekst� nustatyt� su /setunit")
    public boolean delunit(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(jobVehicle != null && jobVehicle.getJob().equals(job)) {
            DynamicLabel label = unitLabels.get(jobVehicle);
            if(label != null) {
                unitLabels.remove(jobVehicle);
                player.sendMessage("Tekstas " + label.getText() + " panaikintas.");
                label.destroy();
            } else
                player.sendErrorMessage("Ant j�s� automobilio n�ra jokio u�ra�o.");
        } else
            player.sendErrorMessage("J�s turite b�ti darbin�je transporto priemon�je");
        return false;
    }


    @Command
    @CommandHelp("Leid�ia naudotis automobilio megafonu, �nek�jimui dideliu atstumu")
    public boolean megaphone(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getClosest(player, 3.0f);
        if(jobVehicle != null) {
            if(text != null && !text.isEmpty()) {
                player.sendMessage(Color.MEGAPHONE, text, String.format("[LSPD] %s!", text), 40.0f);
                return true;
            }
        } else
            player.sendErrorMessage("Prie j�s� n�ra darbinio automobilio.");
        return false;
    }

    @Command
    public boolean m(Player player, String text) {
        return megaphone(player, text);
    }

    @Command
    @CommandHelp("U�deda/nuima nuo automobilio stogo �vyur�lius")
    public boolean police(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(jobVehicle != null) {
            if(player.getVehicleSeat() == 1 || player.getVehicleSeat() == 0) {
                if((player.getVehicleSeat() == 1 && jobVehicle.getWindows().getPassenger() == 0)
                        || player.getVehicleSeat() == 0 && jobVehicle.getWindows().getDriver() == 0) {
                    DynamicSampObject siren;
                    if(!policeSirens.containsKey(jobVehicle)) {
                        siren = DynamicSampObject.create(18646, new Location(), 0.0f, 0.0f, 0.0f);
                        siren.attach(jobVehicle, 0.0f, 0.0f, LtrpVehicleModel.getSirenZOffset(jobVehicle.getModelId()), 0.0f, 0.0f, 0.0f);
                        policeSirens.put(jobVehicle, siren);
                        player.sendActionMessage("i�ki�a rank� su �vytur�li� per lang� ir u�deda j� ant automobilio stogo.");
                    } else {
                        siren = policeSirens.get(jobVehicle);
                        siren.destroy();
                        policeSirens.remove(jobVehicle);
                        player.sendActionMessage("i�ki�a rank� pro lang� ir nuiima policijos persp�jam�j� �vytur�l� nuo stogo.");
                    }
                } else
                    player.sendErrorMessage("Langas u�darytas.");
            } else
                player.sendErrorMessage("J�s turite sed�ti automobilio priekyje.");
        } else
            player.sendErrorMessage("J�s turite b�ti darbiniame automobilyje.");
        return false;
    }


    @Command
    @CommandHelp("Atidaro policijos duomen� baz�")
    public boolean mdc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(player.getJob().isAtWork(player) || (jobVehicle != null && jobVehicle.getJob().getId() == JOB_ID)) {
            new PoliceDatabaseMenu(player, JobManager.getInstance().getEventManager()).show();
        } else
            player.sendErrorMessage("J�s turite b�ti darboviet�je arba darbin�je transporto priemon�je.");
        return false;
    }


    @Command
    @CommandHelp("Leid�ia pasiimti/pad�ti darbinius ginklus")
    public boolean wepStore(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getJob().isAtWork(player)) {
            PoliceWeaponryDialog.create(player, eventManager, job);
            return true;
        } else
            player.sendErrorMessage("�i� komand� galima naudoti tik darboviet�je.");
        return false;
    }

    @Command
    @CommandHelp("Leid�ia surakinti/atrakinti �aid�j� antrankiais")
    public boolean cuff(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(player.getDistanceToPlayer(target) > 10f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else if(player.getState() != target.getState()) {
            player.sendErrorMessage("J�s turite stov�ti �alia " + target.getCharName() + " kad gal�tum�te j� surakinti.");
        } else {
            if(target.isCuffed()) {
                player.sendActionMessage("nuima u�d�tus antrankius " + target.getCharName() + " ir susideda juos � savo d�kl�.");
                target.sendInfoText("~w~Rankos atrakintos");
                player.setCuffed(false);
            } else {
                player.sendActionMessage("suima " + target.getCharName() + " abi rankas u� nugaros ir u�deda antrankius ant rank�.");
                target.sendInfoText("~w~Rankos surakintos");
                player.setCuffed(true);
            }
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Leid�ia prad�ti/baigti tempti surakint� �aid�j�")
    public boolean drag(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(player.getDistanceToPlayer(target) > 10f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else if(player.isInAnyVehicle()) {
            player.sendErrorMessage("B�damas transporto priemon�je negalite nieko tempti.");
        } else if(target.isInComa()) {
            player.sendErrorMessage(target.getCharName() + " yra komos b�senoje, j� tempti b�t� per daug pavojinga.");
        } else if(dragTimers.containsKey(player)) {
            player.sendActionMessage(player.getJobRank().getName() + " " + player.getCharName() + "nustotojo tempti/traukti" + target.getCharName());
            target.sendInfoText("~w~Tempiamas");
            target.toggleControllable(false);
            return true;
        } else {
            dragTimers.put(player, DragTimer.create(player, target));
            player.sendActionMessage(player.getJobRank().getName() + " " + player.getCharName() + " prad�jo tempti/traukti " + target.getCharName());
            target.sendInfoText("~w~Nebe tempiamas");
            target.toggleControllable(true);
            return true;
        }
        return false;
    }


    @Command
    @CommandHelp("Leid�ia i�sikviesti pagalb�")
    public boolean backup(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(backupRequests.keySet().contains(player)) {
            for(LtrpPlayer backup : backupRequests.get(player)) {
                backup.getCheckpoint().disable(player);
            }
            player.getJob().sendMessage(Color.LIGHTRED, "|DI�PE�ERIN� PRANE�A| D�MESIO, pareig�nas " + player.getCharName() + " at�auk� pastiprinimo pra�ym�.");
            backupRequests.remove(player);
        } else {
            backupRequests.put(player, new ArrayList<>());
            player.getJob().sendMessage(Color.LIGHTRED, "|DI�PE�ERIN� PRANE�A| D�MESIO VISIEMS PADALINIAMS, pareig�nas " + player.getCharName() + " pra�o skubaus pastiprinimo, vietos kordinat�s nustatytos J�s� GPS..");
            player.getJob().sendMessage(Color.LIGHTRED, "|DI�PE�ERIN� PRANE�A| Jeigu galite atvykti � pastiprinim� ra�ykite prane�kite dipe�erinei. ((/abk " + player.getId() + "))");
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia reaguoti � pastiprinimo pra�ym�")
    public boolean abk(Player pp, int id) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        Optional<LtrpPlayer> target = backupRequests.keySet().stream().filter(p -> p.getId() == id).findFirst();
        if(!target.isPresent()) {
            player.sendErrorMessage("|DI�PE�ERIN� PRANE�A| Pastiprinimo numeris neatpa�intas, pra�ymas neegzistuoja.");
        } else {
            LtrpPlayer t = target.get();
            t.setCheckpoint(Checkpoint.create(new Radius(t.getLocation(), 5.0f), null, null));
            backupRequests.get(t).add(player);
            return true;
        }
        return false;
    }


    @Command
    @CommandHelp("Leid�ia atimti i� �aid�jo licenzijas, narkotikus arba ginklus")
    public boolean take(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")Player p2, @CommandParameter(name = "veiksmas", description = "K� atimti: Ginklus/Licenzijas/Narkotikus")String paramString) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpPlayer target = LtrpPlayer.get(p2);
        if(target == null || paramString == null) {
            playerCommandManager.sendUsageMessage(player, "take");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage("�aid�jas yra per toli kad gal�tum�te i� jo k� nors atimti.");
        } else if(player.equals(target)) {
            player.sendErrorMessage("Negalite atimti nieko i� saves.");
        } else {
            String[] params = paramString.split(" ");
            String action = params[0];
            if(action.equalsIgnoreCase("ginklus")) {
                target.resetWeapons();
                Item[] weapons = target.getInventory().getItems(ItemType.Weapon);
                for(Item weapon : weapons) {
                    LtrpGamemode.getDao().getItemDao().delete(weapon);
                    target.getInventory().remove(weapon);
                }
                player.sendActionMessage("apie�ko " + target.getCharName() + " ir atima visus ginklus.");
                LtrpGamemode.getDao().getPlayerDao().update(target);
                return true;
            } else if(action.equalsIgnoreCase("licenzijas")) {
                if(params.length != 2) {
                    player.sendMessage("Naudojimas /take licenzijas [licenzijos tipas]");
                } else {
                    String licenseType = params[1];
                    // We need to check if it's a valid license type
                    boolean found = false;
                    for(LicenseType type : LicenseType.values()) {
                        if(StringUtils.equalsIgnoreLtCharsAndCase(licenseType, type.getName())) {
                            if(target.getLicenses().contains(type)) {
                                PlayerLicense license = target.getLicenses().get(type);
                                target.getLicenses().remove(license);
                                LtrpGamemode.getDao().getPlayerDao().delete(license);
                                player.sendActionMessage("Paima i� " + target.getCharName() + " " + type.getName() + " licenzij�.");
                                return true;
                            } else {
                                player.sendErrorMessage(target.getCharName() + " �ios licenzijos neturi!");
                            }
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        String types = "Galimi licenzij� tipai:";
                        for(LicenseType type : LicenseType.values())
                            types += " " + type.getName();
                        player.sendErrorMessage(types);
                    }
                }
            } else if(action.equalsIgnoreCase("narkotikus")) {
                // TODO narkotik� pa�alinimas
            }
        }
        return false;
    }

    @Command
    @CommandHelp("licwarn")
    public boolean licWarn(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")Player p2,
                           @CommandParameter(name = "�sp�jimo apra�ymas/prie�astis")String warningText) {
        // TODO kol kas licwarn galima naudoti tik vairavimo teis�ms, ateityje reik�t� prid�ti ir kit� licenzij�
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpPlayer target = LtrpPlayer.get(p2);
        if(target == null || warningText == null) {
            playerCommandManager.sendUsageMessage(player, "licWarn");
        } else if(player.equals(target)) {
            player.sendErrorMessage("Sau �sp�jimo duoti negalite!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli!");
        } else if(!target.getLicenses().contains(LicenseType.Car)) {
            player.sendErrorMessage(target.getCharName() + " neturi vairavimo licenzijos!");
        } else {
            LicenseWarning warning = new LicenseWarning();
            warning.setLicense(target.getLicenses().get(LicenseType.Car));
            warning.setDate(new Timestamp(new Date().getTime()));
            warning.setIssuedBy(player.getName());
            warning.setBody(warningText);
            target.getLicenses().get(LicenseType.Car).addWarning(warning);
            LtrpGamemode.getDao().getPlayerDao().insert(warning);
            target.sendMessage("J�s gavote vairavimp �sp�jim� nuo pareig�no " + player.getCharName() + ". Pa�eidimas: " + warningText + ".");
            player.sendMessage(target.getCharName() + " �sp�tas.");
            return true;
        }
        return false;
    }
}