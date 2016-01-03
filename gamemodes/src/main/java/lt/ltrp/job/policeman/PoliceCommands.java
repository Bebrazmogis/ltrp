package lt.ltrp.job.policeman;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.CommandParam;
import lt.ltrp.command.Commands;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.CrimeListDialog;
import lt.ltrp.dialogmenu.PoliceDatabaseMenu;
import lt.ltrp.job.JobManager;
import lt.ltrp.job.policeman.OfficerJob;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerCrime;
import lt.ltrp.plugin.streamer.DynamicLabel;
import lt.ltrp.plugin.streamer.DynamicSampObject;
import lt.ltrp.property.House;
import lt.ltrp.property.HouseWeedSapling;
import lt.ltrp.vehicle.JobVehicle;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModelInfoType;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.12.
 */
public class PoliceCommands extends Commands{

    private static final Map<String, Integer> commandToRankNumber;
    private static final List<String> jobVehicleCommands;
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

        jobVehicleCommands = new ArrayList<>();
        jobVehicleCommands.add("setunit");
        jobVehicleCommands.add("delunit");
        jobVehicleCommands.add("police");

    }

    private OfficerJob job;
    private EventManager eventManager;
    private Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    private Map<JobVehicle, DynamicSampObject> policeSirens = new HashMap<>();


    public PoliceCommands(OfficerJob job, EventManager eventManager, Map<JobVehicle, DynamicLabel> unitLabels, Map<JobVehicle, DynamicSampObject> sirends) {
        this.job = job;
        this.eventManager = eventManager;
        this.unitLabels = unitLabels;
        this.policeSirens = sirends;
    }

    @BeforeCheck
    public boolean beforeCheck(LtrpPlayer player, String cmd, String params) {
        if(player.getJob().equals(job)) {
            if(commandToRankNumber.containsKey(cmd)) {
                if(player.getJobRank().getNumber() >= commandToRankNumber.get(cmd)) {
                    if(jobVehicleCommands.contains(cmd)) {
                        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
                        if(jobVehicle != null)
                            return true;
                        else
                            player.sendErrorMessage("Ðià komandà galite naudot tik bûdami darbo tranporto priemonëje");
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
    public boolean policeHelp(LtrpPlayer player) {
        player.sendMessage(Color.POLICE, "|__________________" + job.getName().toUpperCase() + "__________________|");
        player.sendMessage(Color.WHITE, "  PATIKRINIMO KOMANDOS: /frisk /checkalco /fines /vehiclefines /checkspeed /mdc /take");
        player.sendMessage(Color.LIGHTGREY, "  BUDËJIMO PRADÞIOS KOMANDOS: /duty /wepstore");
        player.sendMessage(Color.WHITE, "  SUËMIMO KOMANDOS: /tazer /cuff /drag");
        player.sendMessage(Color.LIGHTGREY, "  GAUDYNIØ/SITUACIJØ KOMANDOS: /bk /rb  /rrb /m /tlc /ram");
        player.sendMessage(Color.WHITE, "  KOMANDOS NUBAUSTI: /fine /vehiclefine /arrest /prison /arrestcar /licwarn ");
        player.sendMessage(Color.LIGHTGREY, "  KITOS KOMANDOS: /flist /setunit /delunit /police /delarrestcar /jobid /cutdownweed");
        player.sendMessage(Color.WHITE, "  DRABUÞIAI/APRANGA: /vest /badge /rbadge /pdclothes");
        player.sendMessage(Color.POLICE, "____________________________________________________________________________");
        return true;
    }


    @Command
    @CommandHelp("Sunaikina name esanèià marihuana")
    public boolean cutDownWeed(LtrpPlayer player) {
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
    public boolean checkalco(LtrpPlayer p, LtrpPlayer p2) {
        if(p2 == null) {
            p.sendErrorMessage("Naudojimas /p [ÞaidëjoID/Dalis vardo]");
        } else if(p.getLocation().distance(p2.getLocation()) > 10.0f) {
            p.sendErrorMessage("Ðià komandà galima naudoti tik kai þaidëjas prie jûsø. " + p2.getCharName() + " yra per toli");
        } else {
            float drunkness = p2.getDrunkLevel() / 1000;
            p.sendActionMessage("prideda alkotesterá prie  " + p2.getCharName() + " lûpø, kuris pripuèia " + drunkness + " promilæ (-iø).");
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Parodo þaidëjo turimas baudas")
    public boolean fines(LtrpPlayer p, LtrpPlayer p2) {
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
    public boolean vehiclefines(LtrpPlayer player) {

        return false;
    }

    @Command
    @CommandHelp("Nustato transporto priemonës greitá")
    public boolean checkspeed(LtrpPlayer player) {

        return false;
    }


    @Command
    @CommandHelp("Paþymi jûsø automobilá pasirinktu tekstu")
    public boolean setunit(LtrpPlayer player, @CommandParam(value = "Tekstas")String text) {
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
    public boolean delunit(LtrpPlayer player) {
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
    public boolean megaphone(LtrpPlayer player, String text) {
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
    public boolean m(LtrpPlayer player, String text) {
        return megaphone(player, text);
    }

    @Command
    @CommandHelp("Uþdeda/nuima nuo automobilio stogo ðvyurëlius")
    public boolean police(LtrpPlayer player) {
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
    public boolean mdc(LtrpPlayer player) {
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(player.getJob().isAtWork(player) || (jobVehicle != null && jobVehicle.getJob().getId() == JOB_ID)) {
            new PoliceDatabaseMenu(player, JobManager.getInstance().getEventManager()).show();
        } else
            player.sendErrorMessage("Jûs turite bûti darbovietëje arba darbinëje transporto priemonëje.");
        return false;
    }

}