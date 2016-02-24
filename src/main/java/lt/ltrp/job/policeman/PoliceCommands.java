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
                            player.sendErrorMessage("Ðià komandà galite naudot tik bûdami darbo tranporto priemonëje");
                    } else if(jobAreaCommands.contains(cmd)) {
                        if(job.isAtWork(player)) {
                            return true;
                        }
                        player.sendErrorMessage("Ðià komandà galite naudot tik bûdami darbo bûstinëje.");
                    } else {
                        return true;
                    }
                } else {
                    player.sendErrorMessage("Ðià komandà gali naudoti darbuotojai kuriø rangas " + player.getJob().getRank(commandToRankNumber.get(cmd)).getName());
                }
            }
        }
        return false;
    }

    @Command
    @CommandHelp("Pareigûnhø komandø sàraðas")
    public boolean policeHelp(Player player) {
        player.sendMessage(Color.POLICE, "|__________________" + job.getName().toUpperCase() + "__________________|");
        player.sendMessage(Color.WHITE, "  PATIKRINIMO KOMANDOS: /frisk /checkalco /fines /vehiclefines /checkspeed /mdc /take");
        player.sendMessage(Color.LIGHTGREY, "  BUDËJIMO PRADÞIOS KOMANDOS: /duty /wepstore");
        player.sendMessage(Color.WHITE, "  SUËMIMO KOMANDOS: /tazer /cuff /drag");
        player.sendMessage(Color.LIGHTGREY, "  GAUDYNIØ/SITUACIJØ KOMANDOS: /bk /rb  /rrb /m");
        player.sendMessage(Color.WHITE, "  KOMANDOS NUBAUSTI: /fine /vehiclefine /arrest /prison /arrestcar /licwarn ");
        player.sendMessage(Color.LIGHTGREY, "  KITOS KOMANDOS: /flist /setunit /delunit /police /delarrestcar /jobid /cutdownweed");
        player.sendMessage(Color.WHITE, "  DRABUÞIAI/APRANGA: /vest /badge /rbadge /pdclothes");
        player.sendMessage(Color.POLICE, "____________________________________________________________________________");
        return true;
    }


    @Command
    @CommandHelp("Sunaikina name esanèià marihuana")
    public boolean cutDownWeed(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getProperty() == null || !(player.getProperty() instanceof House)) {
            player.sendErrorMessage("J8s ne name!");
        } else {
            House house = (House)player.getProperty();
            List<HouseWeedSapling> closestWeed = house.getWeedSaplings().stream().filter(weed -> weed.getLocation().distance(player.getLocation()) < 10.0f).collect(Collectors.toList());
            if(house.getWeedSaplings().size() == 0 || closestWeed.size() == 0) {
                player.sendActionMessage("apsidairo po namus...");
                player.sendErrorMessage("Ðiame name neauga marihuana!");
            } else  {
                closestWeed.forEach(w -> {
                    w.destroy();
                    LtrpGamemode.getDao().getHouseDao().updateWeed(w);
                });
                player.sendActionMessage("Pareigûnas " + player.getCharName() + " sunaikina " + closestWeed.size() + " marihuanos augalus.");
                return true;
            }
        }
        return false;
    }

    @Command
    @CommandHelp("Patikrina þaidëjo girtumà")
    public boolean checkalco(Player p, LtrpPlayer p2) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(p2 == null) {
            player.sendErrorMessage("Naudojimas /p [ÞaidëjoID/Dalis vardo]");
        } else if(p.getLocation().distance(p2.getLocation()) > 10.0f) {
            player.sendErrorMessage("Ðià komandà galima naudoti tik kai þaidëjas prie jûsø. " + p2.getCharName() + " yra per toli");
        } else {
            float drunkness = p2.getDrunkLevel() / 1000;
            player.sendActionMessage("prideda alkotesterá prie  " + p2.getCharName() + " lûpø, kuris pripuèia " + drunkness + " promilæ (-iø).");
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Parodo þaidëjo turimas baudas")
    public boolean fines(LtrpPlayer player, LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null) {
            p.sendErrorMessage("Naudojimas /p [ÞaidëjoID/Dalis vardo]");
        } else if(p.getLocation().distance(p2.getLocation()) > 10.0f) {
            p.sendErrorMessage("Ðià komandà galima naudoti tik kai þaidëjas prie jûsø. " + p2.getCharName() + " yra per toli");
        } else {
            List<PlayerCrime> crimes = LtrpGamemode.getDao().getPlayerDao().getCrimes(p2);
            if(crimes.size() == 0) {
                p.sendErrorMessage(p2.getCharName() + " nëra nieko padaræs.");
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
    @CommandHelp("Nustato transporto priemonës greitá")
    public boolean checkspeed(Player player) {

        return false;
    }


    @Command
    @CommandHelp("Paþymi jûsø automobilá pasirinktu tekstu")
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
                player.sendErrorMessage("Tekstas negali bûti tuðèias");
        } else
            player.sendErrorMessage("Jûs turite bûti darbinëje transporto priemonëje");
        return false;
    }

    @Command
    @CommandHelp("Panaikina automobilio tekstà nustatytà su /setunit")
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
                player.sendErrorMessage("Ant jûsø automobilio nëra jokio uþraðo.");
        } else
            player.sendErrorMessage("Jûs turite bûti darbinëje transporto priemonëje");
        return false;
    }


    @Command
    @CommandHelp("Leidþia naudotis automobilio megafonu, ðnekëjimui dideliu atstumu")
    public boolean megaphone(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getClosest(player, 3.0f);
        if(jobVehicle != null) {
            if(text != null && !text.isEmpty()) {
                player.sendMessage(Color.MEGAPHONE, text, String.format("[LSPD] %s!", text), 40.0f);
                return true;
            }
        } else
            player.sendErrorMessage("Prie jûsø nëra darbinio automobilio.");
        return false;
    }

    @Command
    public boolean m(Player player, String text) {
        return megaphone(player, text);
    }

    @Command
    @CommandHelp("Uþdeda/nuima nuo automobilio stogo ðvyurëlius")
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
                        player.sendActionMessage("iðkiða rankà su ðvyturëliø per langà ir uþdeda já ant automobilio stogo.");
                    } else {
                        siren = policeSirens.get(jobVehicle);
                        siren.destroy();
                        policeSirens.remove(jobVehicle);
                        player.sendActionMessage("iðkiða rankà pro langà ir nuiima policijos perspëjamàjá ðvyturëlá nuo stogo.");
                    }
                } else
                    player.sendErrorMessage("Langas uþdarytas.");
            } else
                player.sendErrorMessage("Jûs turite sedëti automobilio priekyje.");
        } else
            player.sendErrorMessage("Jûs turite bûti darbiniame automobilyje.");
        return false;
    }


    @Command
    @CommandHelp("Atidaro policijos duomenø bazæ")
    public boolean mdc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(player.getJob().isAtWork(player) || (jobVehicle != null && jobVehicle.getJob().getId() == JOB_ID)) {
            new PoliceDatabaseMenu(player, JobManager.getInstance().getEventManager()).show();
        } else
            player.sendErrorMessage("Jûs turite bûti darbovietëje arba darbinëje transporto priemonëje.");
        return false;
    }


    @Command
    @CommandHelp("Leidþia pasiimti/padëti darbinius ginklus")
    public boolean wepStore(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getJob().isAtWork(player)) {
            PoliceWeaponryDialog.create(player, eventManager, job);
            return true;
        } else
            player.sendErrorMessage("Ðià komandà galima naudoti tik darbovietëje.");
        return false;
    }

    @Command
    @CommandHelp("Leidþia surakinti/atrakinti þaidëjà antrankiais")
    public boolean cuff(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(player.getDistanceToPlayer(target) > 10f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else if(player.getState() != target.getState()) {
            player.sendErrorMessage("Jûs turite stovëti ðalia " + target.getCharName() + " kad galëtumëte já surakinti.");
        } else {
            if(target.isCuffed()) {
                player.sendActionMessage("nuima uþdëtus antrankius " + target.getCharName() + " ir susideda juos á savo dëklà.");
                target.sendInfoText("~w~Rankos atrakintos");
                player.setCuffed(false);
            } else {
                player.sendActionMessage("suima " + target.getCharName() + " abi rankas uþ nugaros ir uþdeda antrankius ant rankø.");
                target.sendInfoText("~w~Rankos surakintos");
                player.setCuffed(true);
            }
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Leidþia pradëti/baigti tempti surakintà þaidëjà")
    public boolean drag(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(player.getDistanceToPlayer(target) > 10f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else if(player.isInAnyVehicle()) {
            player.sendErrorMessage("Bûdamas transporto priemonëje negalite nieko tempti.");
        } else if(target.isInComa()) {
            player.sendErrorMessage(target.getCharName() + " yra komos bûsenoje, já tempti bûtø per daug pavojinga.");
        } else if(dragTimers.containsKey(player)) {
            player.sendActionMessage(player.getJobRank().getName() + " " + player.getCharName() + "nustotojo tempti/traukti" + target.getCharName());
            target.sendInfoText("~w~Tempiamas");
            target.toggleControllable(false);
            return true;
        } else {
            dragTimers.put(player, DragTimer.create(player, target));
            player.sendActionMessage(player.getJobRank().getName() + " " + player.getCharName() + " pradëjo tempti/traukti " + target.getCharName());
            target.sendInfoText("~w~Nebe tempiamas");
            target.toggleControllable(true);
            return true;
        }
        return false;
    }


    @Command
    @CommandHelp("Leidþia iðsikviesti pagalbà")
    public boolean backup(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(backupRequests.keySet().contains(player)) {
            for(LtrpPlayer backup : backupRequests.get(player)) {
                backup.getCheckpoint().disable(player);
            }
            player.getJob().sendMessage(Color.LIGHTRED, "|DIÈPEÈERINË PRANEÐA| DëMESIO, pareigûnas " + player.getCharName() + " atðaukë pastiprinimo praðymà.");
            backupRequests.remove(player);
        } else {
            backupRequests.put(player, new ArrayList<>());
            player.getJob().sendMessage(Color.LIGHTRED, "|DIÈPEÈERINË PRANEÐA| DËMESIO VISIEMS PADALINIAMS, pareigûnas " + player.getCharName() + " praðo skubaus pastiprinimo, vietos kordinatës nustatytos Jûsø GPS..");
            player.getJob().sendMessage(Color.LIGHTRED, "|DIÈPEÈERINË PRANEÐA| Jeigu galite atvykti á pastiprinimà raðykite praneðkite dipeèerinei. ((/abk " + player.getId() + "))");
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia reaguoti á pastiprinimo praðymà")
    public boolean abk(Player pp, int id) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        Optional<LtrpPlayer> target = backupRequests.keySet().stream().filter(p -> p.getId() == id).findFirst();
        if(!target.isPresent()) {
            player.sendErrorMessage("|DIÈPEÈERINË PRANEÐA| Pastiprinimo numeris neatpaþintas, praðymas neegzistuoja.");
        } else {
            LtrpPlayer t = target.get();
            t.setCheckpoint(Checkpoint.create(new Radius(t.getLocation(), 5.0f), null, null));
            backupRequests.get(t).add(player);
            return true;
        }
        return false;
    }


    @Command
    @CommandHelp("Leidþia atimti ið þaidëjo licenzijas, narkotikus arba ginklus")
    public boolean take(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")Player p2, @CommandParameter(name = "veiksmas", description = "Kà atimti: Ginklus/Licenzijas/Narkotikus")String paramString) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpPlayer target = LtrpPlayer.get(p2);
        if(target == null || paramString == null) {
            playerCommandManager.sendUsageMessage(player, "take");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage("Þaidëjas yra per toli kad galëtumëte ið jo kà nors atimti.");
        } else if(player.equals(target)) {
            player.sendErrorMessage("Negalite atimti nieko ið saves.");
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
                player.sendActionMessage("apieðko " + target.getCharName() + " ir atima visus ginklus.");
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
                                player.sendActionMessage("Paima ið " + target.getCharName() + " " + type.getName() + " licenzijà.");
                                return true;
                            } else {
                                player.sendErrorMessage(target.getCharName() + " ðios licenzijos neturi!");
                            }
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        String types = "Galimi licenzijø tipai:";
                        for(LicenseType type : LicenseType.values())
                            types += " " + type.getName();
                        player.sendErrorMessage(types);
                    }
                }
            } else if(action.equalsIgnoreCase("narkotikus")) {
                // TODO narkotikø paðalinimas
            }
        }
        return false;
    }

    @Command
    @CommandHelp("licwarn")
    public boolean licWarn(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")Player p2,
                           @CommandParameter(name = "Áspëjimo apraðymas/prieþastis")String warningText) {
        // TODO kol kas licwarn galima naudoti tik vairavimo teisëms, ateityje reikëtø pridëti ir kitø licenzijø
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpPlayer target = LtrpPlayer.get(p2);
        if(target == null || warningText == null) {
            playerCommandManager.sendUsageMessage(player, "licWarn");
        } else if(player.equals(target)) {
            player.sendErrorMessage("Sau áspëjimo duoti negalite!");
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
            target.sendMessage("Jûs gavote vairavimp áspëjimà nuo pareigûno " + player.getCharName() + ". Paþeidimas: " + warningText + ".");
            player.sendMessage(target.getCharName() + " áspëtas.");
            return true;
        }
        return false;
    }
}