package lt.ltrp.job;

import lt.ltrp.command.CommandParam;
import lt.ltrp.command.Commands;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.dialogmenu.PoliceDatabaseMenu;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.plugin.streamer.DynamicLabel;
import lt.ltrp.plugin.streamer.DynamicSampObject;
import lt.ltrp.vehicle.JobVehicle;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModelInfoType;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @BeforeCheck
    public boolean beforeCheck(LtrpPlayer player, String cmd, String params) {
        if(player.getJob().getId() == JOB_ID) {
            if(commandToRankNumber.containsKey(cmd)) {
                if(player.getJobRank().getNumber() >= commandToRankNumber.get(cmd)) {
                    if(jobVehicleCommands.contains(cmd)) {
                        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
                        if(jobVehicle != null)
                            return true;
                        else
                            player.sendErrorMessage("�i� komand� galite naudot tik b�dami darbo tranporto priemon�je");
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
    @CommandHelp("Pa�ymi j�s� automobil� pasirinktu tekstu")
    public boolean setunit(LtrpPlayer player, @CommandParam(value = "Tekstas")String text) {
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(jobVehicle != null || jobVehicle.getJob().getId() != JOB_ID) {
            if(!text.isEmpty()) {
                DynamicLabel label = JobManager.getInstance().unitLabels.get(jobVehicle);
                if(label != null) {
                    label.update(text);
                } else {
                    Vector3D offsets = VehicleModel.getModelInfo(jobVehicle.getModelId(), VehicleModelInfoType.SIZE);
                    Location location = new Location(0.0f, (-0.0f * offsets.getY()), 0.0f);
                    label = DynamicLabel.create(text, Color.WHITE, location, 15.0f, true, null, jobVehicle);
                    JobManager.getInstance().unitLabels.put(jobVehicle, label);
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
    public boolean delunit(LtrpPlayer player) {
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(jobVehicle != null || jobVehicle.getJob().getId() != JOB_ID) {
            DynamicLabel label = JobManager.getInstance().unitLabels.get(jobVehicle);
            if(label != null) {
                JobManager.getInstance().unitLabels.remove(jobVehicle);
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
    public boolean megaphone(LtrpPlayer player, String text) {
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
    public boolean m(LtrpPlayer player, String text) {
        return megaphone(player, text);
    }

    @Command
    @CommandHelp("U�deda/nuima nuo automobilio stogo �vyur�lius")
    public boolean police(LtrpPlayer player) {
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(jobVehicle != null) {
            if(player.getVehicleSeat() == 1 || player.getVehicleSeat() == 0) {
                if((player.getVehicleSeat() == 1 && jobVehicle.getWindows().getPassenger() == 0)
                        || player.getVehicleSeat() == 0 && jobVehicle.getWindows().getDriver() == 0) {
                    DynamicSampObject siren;
                    if(!JobManager.getInstance().policeSirens.containsKey(jobVehicle)) {
                        siren = DynamicSampObject.create(18646, new Location(), 0.0f, 0.0f, 0.0f);
                        siren.attach(jobVehicle, 0.0f, 0.0f, LtrpVehicleModel.getSirenZOffset(jobVehicle.getModelId()), 0.0f, 0.0f, 0.0f);
                        JobManager.getInstance().policeSirens.put(jobVehicle, siren);
                        player.sendActionMessage("i�ki�a rank� su �vytur�li� per lang� ir u�deda j� ant automobilio stogo.");
                    } else {
                        siren = JobManager.getInstance().policeSirens.get(jobVehicle);
                        siren.destroy();
                        JobManager.getInstance().policeSirens.remove(jobVehicle);
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
    public boolean mdc(LtrpPlayer player) {
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(player.getJob().isAtWork(player) || (jobVehicle != null && jobVehicle.getJob().getId() == JOB_ID)) {
            new PoliceDatabaseMenu(player, JobManager.getInstance().getEventManager()).show();
        } else
            player.sendErrorMessage("J�s turite b�ti darboviet�je arba darbin�je transporto priemon�je.");
        return false;
    }

}