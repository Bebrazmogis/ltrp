package lt.ltrp.command;


import lt.ltrp.*;
import lt.ltrp.constant.*;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.*;
import lt.ltrp.dialog.*;
import lt.ltrp.player.vehicle.data.PlayerVehicleArrest;
import lt.ltrp.player.vehicle.data.PlayerVehicleMetadata;
import lt.ltrp.player.vehicle.dialog.VehicleFineListDialog;
import lt.ltrp.player.vehicle.dialog.VehicleNewFineInputDialog;
import lt.ltrp.player.vehicle.event.PlayerVehicleArrestDeleteEvent;
import lt.ltrp.player.vehicle.event.PlayerVehicleArrestEvent;
import lt.ltrp.house.object.House;
import lt.ltrp.house.weed.HouseWeedController;
import lt.ltrp.house.weed.object.HouseWeedSapling;
import lt.ltrp.job.object.FactionRank;
import lt.ltrp.job.object.JobVehicle;
import lt.ltrp.modelpreview.SkinModelPreview;
import lt.ltrp.object.*;
import lt.ltrp.object.drug.DrugItem;
import lt.ltrp.player.BankAccount;
import lt.ltrp.player.fine.PlayerFineController;
import lt.ltrp.player.fine.data.PlayerFine;
import lt.ltrp.player.fine.data.PlayerFineOffer;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.player.licenses.PlayerLicenseController;
import lt.ltrp.player.licenses.constant.LicenseType;
import lt.ltrp.player.licenses.data.PlayerLicense;
import lt.ltrp.player.vehicle.PlayerVehiclePlugin;
import lt.ltrp.player.vehicle.object.PlayerVehicle;
import lt.ltrp.util.PawnFunc;
import lt.ltrp.player.util.PlayerUtils;
import lt.ltrp.util.StringUtils;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.common.command.*;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModelInfoType;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerAttach;
import net.gtaun.util.event.EventManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.12.
 */
public class PoliceCommands extends Commands {

    private static final Map<String, Integer> commandToRankNumber;
    private static final List<String> jobVehicleCommands;
    private static final List<String> jobAreaCommands;

    static {
        // Experimental but SHOULD work
        PoliceFaction faction = PoliceJobPlugin.get(PoliceJobPlugin.class).getPoliceFaction();
        commandToRankNumber = new HashMap<>();

        Optional<? extends FactionRank> opMaxRank = faction.getRanks().stream().max((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber()));

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
        commandToRankNumber.put("pgear", 1);
        commandToRankNumber.put("duty", 1);
        commandToRankNumber.put("pdclothes", 1);
        commandToRankNumber.put("arrestcar", 1);
        commandToRankNumber.put("delarrestcar", 1);
        commandToRankNumber.put("vest", 1);
        commandToRankNumber.put("jobid", 1);
        commandToRankNumber.put("killcheckpoint", 1);
        commandToRankNumber.put("jobid", 1);
        commandToRankNumber.put("taser", 1);
        commandToRankNumber.put("tazer", 1); // alt spelling
        commandToRankNumber.put("prison", 1);
        commandToRankNumber.put("arrest", 1);
        commandToRankNumber.put("badge", 1);
        commandToRankNumber.put("rbadge", 1);
        if(opMaxRank.isPresent()) {
            FactionRank maxRank = opMaxRank.get();
            commandToRankNumber.put("setswat", maxRank.getNumber());
            commandToRankNumber.put("unsetswat", maxRank.getNumber());
        }


        commandToRankNumber.put("wepstore", 2);
        commandToRankNumber.put("ramcar", 2);
        commandToRankNumber.put("ram", 2);

        jobVehicleCommands = new ArrayList<>();
        jobVehicleCommands.add("setunit");
        jobVehicleCommands.add("delunit");
        jobVehicleCommands.add("police");

        jobAreaCommands = new ArrayList<>();
        jobAreaCommands.add("wepstore");
        jobAreaCommands.add("pgear");
        jobAreaCommands.add("duty");
        jobAreaCommands.add("pdclothes");
        jobAreaCommands.add("vest");
        jobAreaCommands.add("setswat");
        jobAreaCommands.add("unsetswat");


    }

    private PoliceFaction job;
    private EventManager eventManager;
    private Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    private Map<JobVehicle, DynamicObject> policeSirens = new HashMap<>();
    private Map<LtrpPlayer, DragTimer> dragTimers;
    private Map<LtrpPlayer, List<LtrpPlayer>> backupRequests;
    private PoliceJobPlugin policePlugin;


    public PoliceCommands(PoliceFaction job, EventManager eventManager,
                          Map<JobVehicle, DynamicLabel> unitLabels, Map<JobVehicle, DynamicObject> sirends, Map<LtrpPlayer,DragTimer> dragtimers) {
        this.job = job;
        this.backupRequests = new HashMap<>();
        this.eventManager = eventManager;
        this.unitLabels = unitLabels;
        this.policeSirens = sirends;
        this.dragTimers = dragtimers;
        this.policePlugin = PoliceJobPlugin.get(PoliceJobPlugin.class);

    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = player.getJobData();
        if(jobData != null && jobData.getJob().equals(job)) {
            if(commandToRankNumber.containsKey(cmd)) {
                if(jobData.getRank().getNumber() >= commandToRankNumber.get(cmd)) {
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
                    player.sendErrorMessage("�i� komand� gali naudoti darbuotojai kuri� rangas " + jobData.getJob().getRankByNumber(commandToRankNumber.get(cmd)).getName());
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
        player.sendMessage(Color.WHITE, "  SU�MIMO KOMANDOS: /taser /cuff /drag");
        player.sendMessage(Color.LIGHTGREY, "  GAUDYNI�/SITUACIJ� KOMANDOS: /bk /rb  /rrb /m");
        player.sendMessage(Color.WHITE, "  KOMANDOS NUBAUSTI: /fine /vehiclefine /arrest /prison /arrestcar /licwarn ");
        player.sendMessage(Color.LIGHTGREY, "  KITOS KOMANDOS: /flist /setunit /delunit /police /delarrestcar /jobid /cutdownweed");
        player.sendMessage(Color.WHITE, "  DRABU�IAI/APRANGA: /vest /badge /rbadge /pdclothes");
        Optional<? extends FactionRank> opMaxRank = job.getRanks().stream().max((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber()));
        if(opMaxRank.isPresent()) {
            player.sendMessage(Color.LIGHTGREY, "AUK�TO RANGO KOMANDOS: /setswat /unsetswat");
        }
        player.sendMessage(Color.POLICE, "____________________________________________________________________________");
        return true;
    }




    @Command
    @CommandHelp("Parodo j�s� pareig�no pa�ym�jim� kitam �aid�jui")
    public boolean jobId(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        if(player.getLocation().distance(target.getLocation()) > 10f)
            player.sendErrorMessage(target.getName() + " yra per toli kad gal�tum�te parodyti jam savo pa�ym�jim�.");
        else {
            PlayerJobData jobData = target.getJobData();
            target.sendMessage(Color.GREEN, "|______________LOS SANTOS DEPARTAMENTAS______________|");
            target.sendMessage(Color.GREEN, "|______________  PAREIG�NO PA�YM�JIMAS ______________|");
            target.sendMessage(Color.WHITE, String.format("Pareig�no vardas: %s     Pavard�: %s", target.getFirstName(), target.getLastName()));
            target.sendMessage(Color.WHITE, String.format("Pareig�no pareigos/rangas: %s     Am�ius: %d", jobData.getRank().getName(), target.getAge()));
        }
        return true;
    }

    @Command
    @CommandHelp("Sunaikina name esan�i� marihuana")
    public boolean cutDownWeed(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        House house = House.get(player);
        if(house == null) {
            player.sendErrorMessage("J8s ne name!");
        } else {
            List<HouseWeedSapling> closestWeed = house.getWeedSaplings().stream().filter(weed -> weed.getLocation().distance(player.getLocation()) < 10.0f).collect(Collectors.toList());
            if(house.getWeedSaplings().size() == 0 || closestWeed.size() == 0) {
                player.sendActionMessage("apsidairo po namus...");
                player.sendErrorMessage("�iame name neauga marihuana!");
            } else  {
                closestWeed.forEach(w -> {
                    w.destroy();
                    HouseWeedController.instance.destroyWeed(w);
                });
                player.sendActionMessage("Pareig�nas " + player.getCharName() + " sunaikina " + closestWeed.size() + " marihuanos augalus.");
                return true;
            }
        }
        return true;
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
        return true;
    }

    @Command
    @CommandHelp("Parodo �aid�jo turimas baudas")
    public boolean fines(Player player, LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null) {
            p.sendErrorMessage("Naudojimas /p [�aid�joID/Dalis vardo]");
        } else if(p.getLocation().distance(p2.getLocation()) > 10.0f) {
            p.sendErrorMessage("�i� komand� galima naudoti tik kai �aid�jas prie j�s�. " + p2.getCharName() + " yra per toli");
        } else {
            Collection<PlayerFine> crimes = PlayerFineController.Companion.get().get(p2);
            if(crimes.size() == 0) {
                p.sendErrorMessage(p2.getCharName() + " n�ra nieko padar�s.");
            } else {
                new CrimeListDialog(p, eventManager, crimes).show();
            }
        }
        return true;
    }

    @Command
    @CommandHelp("I�ra�o kitam �aid�jui baud�")
    public boolean fine(Player p,
                        @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "Baudos suma")int amount,
                        @CommandParameter(name = "Prie�astis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null || reason == null)
            return false;

        PlayerFineOffer offer = target.getOffer(PlayerFineOffer.class);
        if(amount <= 0)
            player.sendErrorMessage("Bauda negali b�ti ma�esn� nei 0" + Currency.SYMBOL);
        else if(player.getDistanceToPlayer(target) > 9f)
            player.sendErrorMessage(target.getCharName() + " yra per toli kad gal�tum�te jam i�ra�yti baud�");
        else if(offer != null)
            player.sendErrorMessage("�iam �aid�jui jau ka�kas si�lo priimti baud�, pra�ome palaukti.");
        else {
            offer = new PlayerFineOffer(target, player, eventManager, reason, amount);
            target.getOffers().add(offer);
            player.sendMessage(Color.POLICE, String.format("[LSPD] I�ra��t� baud� asmeniui: %s, kurios dydys yra %d$, dabar �is asmuo privalo sutikti su J�s� bauda.", target.getCharName(), amount));
            target.sendMessage(Color.POLICE, String.format("[LSPD] Pareig�nas %s i�ra�� Jums baud�, kurios suma yra %d$. Jei sutinkate su bauda turite ra�yti: /accept fine", player.getCharName(), amount));
            target.sendMessage(Color.POLICE, "Baudos prie�astis:" + reason);
        }
        return true;
    }

    public boolean vehicleFines(Player p, @CommandParameter(name = "Automobilio numeriai")String license) {
        LtrpPlayer player = LtrpPlayer.get(p);
        final PlayerVehicle vehicle;
        if(license != null)
            vehicle = PlayerVehicle.Companion.getByLicense(license);
        else
            vehicle = player.isInAnyVehicle() ? PlayerVehicle.Companion.getByVehicle(player.getVehicle()) : PlayerVehicle.Companion.getClosest(player, 5f);
        if(vehicle == null)
            player.sendErrorMessage("Nenurod�te automobilio numeri�, automobilio su tokiais numeriais n�ra bei pie j�s� n�ra jokio automobilio.");
        else {
            VehicleFineListDialog.create(player, eventManager, vehicle, PlayerVehiclePlugin.get(PlayerVehiclePlugin.class).getFineDao().get(vehicle))
                    .item("Prid�ti nauj�", i -> VehicleNewFineInputDialog.create(player, eventManager, i.getCurrentDialog(), vehicle).show())
                    .build()
                    .show();
        }
        return true;
    }

    @Command
    @CommandHelp("Atidaro automobilio baud� valdym�")
    public boolean vehicleFines(Player p) {
        return vehicleFines(p, null);
    }

    @Command
    @CommandHelp("Nustato transporto priemon�s greit�")
    public boolean checkspeed(Player player) {

        return true;
    }


    @Command
    @CommandHelp("Pa�ymi j�s� automobil� pasirinktu tekstu")
    public boolean setunit(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(jobVehicle != null || !jobVehicle.getJob().equals(job)) {
            if(!text.isEmpty()) {
                DynamicLabel label = unitLabels.get(jobVehicle);
                if(label != null) {
                    label.update(Color.WHITE, text);
                } else {
                    Vector3D offsets = VehicleModel.getModelInfo(jobVehicle.getModelId(), VehicleModelInfoType.SIZE);
                    Location location = new Location(0.0f, (-0.0f * offsets.getY()), 0.0f);
                    label = DynamicLabel.create(text, Color.WHITE, location, 15.0f, jobVehicle, true);
                    unitLabels.put(jobVehicle, label);
                }
            } else
                player.sendErrorMessage("Tekstas negali b�ti tu��ias");
        } else
            player.sendErrorMessage("J�s turite b�ti darbin�je transporto priemon�je");
        return true;
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
        return true;
    }


    @Command
    @CommandHelp("Leid�ia naudotis automobilio megafonu, �nek�jimui dideliu atstumu")
    public boolean megaphone(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getClosest(player, 3.0f);
        if(jobVehicle != null) {
            if(text != null && !text.isEmpty()) {
                player.sendMessage(Color.MEGAPHONE, text, String.format("[LSPD] %s!", text), 40.0f);
            }
        } else
            player.sendErrorMessage("Prie j�s� n�ra darbinio automobilio.");
        return true;
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
                    DynamicObject siren;
                    if(!policeSirens.containsKey(jobVehicle)) {
                        siren = DynamicObject.create(18646, new Location(), new Vector3D(0.0f, 0.0f, 0.0f));
                        siren.attachToVehicle(jobVehicle, new Vector3D(0.0f, 0.0f, LtrpVehicleModel.getSirenZOffset(jobVehicle.getModelId())), new Vector3D());
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
        return true;
    }


    @Command
    @CommandHelp("Atidaro policijos duomen� baz�")
    public boolean mdc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobVehicle jobVehicle = JobVehicle.getById(player.getVehicle().getId());
        if(player.getJobData() != null && player.getJobData().getJob().isAtWork(player) || (jobVehicle != null && jobVehicle.getJob().equals(job))) {
            new PoliceDatabaseMenu(player, eventManager).show();
        } else
            player.sendErrorMessage("J�s turite b�ti darboviet�je arba darbin�je transporto priemon�je.");
        return true;
    }


    @Command
    @CommandHelp("Leid�ia pasiimti/pad�ti darbinius ginklus")
    public boolean wepStore(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(job.isAtWork(player)) {
            PoliceWeaponryDialog.create(player, eventManager, job);
            return true;
        } else
            player.sendErrorMessage("�i� komand� galima naudoti tik darboviet�je.");
        return true;
    }

    @Command
    @CommandHelp("I�lau�ia civilin�s transporto priemon�s spyn�")
    public boolean ramCar(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.Companion.getClosest(player, 5f);
        if(vehicle == null)
            player.sendErrorMessage("Prie j�s� n�ra civilin�s transporto priemon�s.");
        else if(!vehicle.isLocked())
            player.sendErrorMessage("Transporto priemon� neu�rakinta, n�ra prasm�s lau�yti jos spyn�.");
        else {
            player.sendInfoText("~w~ Tr. priemones dureles islauztos", 5000);
            vehicle.setLocked(false);
        }
        return true;
    }

    @Command
    @CommandHelp("I�lau�ia dur� spyn� � nekilnojam� turt�")
    public boolean ram(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Property property = Property.getClosest(player.getLocation(), 5f);
        if(property == null)
            player.sendErrorMessage("Prie j�s� n�ra joki� dur� kurias gal�tum�te i�lau�ti.");
        else if(!property.isLocked())
            player.sendErrorMessage("Durys neu�rakintos..");
        else {
            player.sendInfoText("~w~ Durys islauztos", 5000);
            property.setLocked(false);
        }
        return true;
    }

    @Command
    @CommandHelp("�jungia/i�jungia tazer�")
    public boolean taser(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!job.isTaserEnabled())
            player.sendErrorMessage("Direktorius �iuo metu yra i�jung�s tazerio naudojim�.");
        else if(player.isInAnyVehicle())
            player.sendErrorMessage("Negalite tazerio naudoti b�damas transporto priemon�je.");
        else {
            if(policePlugin.isUsingTaser(player))
                player.sendActionMessage("ideda � d�kla tazer�.");
            else
                player.sendActionMessage("i�traukia i� d�klo tazer�. ");
            policePlugin.setTaser(player, !policePlugin.isUsingTaser(player));
        }
        return true;
    }

    @Command
    @CommandHelp("U�daro nusikalt�l� � kal�jim�")
    public boolean prison(Player p, @CommandParameter(name = "�aid�jo ID/ Dalis vardo")LtrpPlayer target,
                          @CommandParameter(name = "Laikas minut�mis")int minutes,
                          @CommandParameter(name = "Bauda")int fine) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penalties = PenaltyPlugin.get(PenaltyPlugin.class);
        BankPlugin bank = BankPlugin.get(BankPlugin.class);
        if(player.getLocation().distance(penalties.getJailEntrance(JailData.JailType.InCharacterPrison)) > 10f)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami kal�jime.");
        else if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else if(penalties.getJailData(target) != null)
            player.sendErrorMessage("�is �aid�jas jau s�di kal�jime!");
        else if(minutes < 60)
            player.sendErrorMessage("Minimalus laikas 60 minu�i�, trumpoms bausm�ms u�darykite � are�tin�.");
        else if(fine < 1000 || fine > 50000)
            player.sendErrorMessage("Bauda negali b�ti ma�esn� u� 1000 ar didesn� u� 50000" + Currency.SYMBOL);
        else {
            LtrpPlayer.sendGlobalMessage(String.format("[LSPD] Pareig�nas %s pasodino � kal�jim� asmen� %s, %d minut�ms.", player.getCharName(), target.getCharName(), minutes));
            target.sendMessage(Color.LIGHTRED, String.format("[LSPD] J�s buvote u�darytas � kal�jim� %d minut�ms, bei tur�site susimok�ti pareig�no nustaty� baud�: %d%c",
                    minutes, fine, Currency.SYMBOL));

            BankAccount account = bank.getBankController().getAccount(target);
            if(account != null && account.getMoney() >= fine) {
                account.addMoney(-fine);
                bank.getBankController().update(account);
                LtrpWorld.get().addMoney(fine);
                target.sendMessage(Color.LIGHTRED, "Pinigai baudai buvo paimti i� j�s� banko s�skaitos.");
            } else if(target.getMoney() >= fine)
                target.sendMessage(Color.LIGHTRED, "Pinigai buvo paimti i� j�s� laikom�.");
            else
                target.sendMessage(Color.LIGHTRED, "D�l J�s� ma�� pajam�, buvote atleistas nuo baudos.");
            penalties.jail(target, JailData.JailType.InCharacterPrison, minutes, player);

        }
        return true;
    }


    @Command
    @CommandHelp("U�daro nusikalt�l� � kal�jim�")
    public boolean arrest(Player p, @CommandParameter(name = "�aid�jo ID/ Dalis vardo")LtrpPlayer target,
                          @CommandParameter(name = "Laikas minut�mis")int minutes,
                          @CommandParameter(name = "Bauda")int fine) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penalties = PenaltyPlugin.get(PenaltyPlugin.class);
        BankPlugin bank = BankPlugin.get(BankPlugin.class);
        if(player.getLocation().distance(penalties.getJailEntrance(JailData.JailType.InCharacter)) > 10f)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami are�in�je.");
        else if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else if(penalties.getJailData(target) != null)
            player.sendErrorMessage("�is �aid�jas jau s�di kal�jime!");
        else if(minutes < 1)
            player.sendErrorMessage("Minimalus laikas 1 minut�.");
        else if(fine < 1000 || fine > 50000)
            player.sendErrorMessage("Bauda negali b�ti ma�esn� u� 1000 ar didesn� u� 50000" + Currency.SYMBOL);
        else {
            LtrpPlayer.sendGlobalMessage(String.format("[LSPD] Pareig�nas %s pasodino � are�tin� asmen�  %s, %d minut�ms.", player.getCharName(), target.getCharName(), minutes));
            target.sendMessage(Color.LIGHTRED, String.format("[LSPD] J�s buvote u�darytas � kal�jim� %d minut�ms, bei tur�site susimok�ti pareig�no nustaty� baud�: %d%c",
                    minutes, fine, Currency.SYMBOL));

            BankAccount account = bank.getBankController().getAccount(target);
            if(account != null && account.getMoney() >= fine) {
                account.addMoney(-fine);
                bank.getBankController().update(account);
                LtrpWorld.get().addMoney(fine);
                target.sendMessage(Color.LIGHTRED, "Pinigai baudai buvo paimti i� j�s� banko s�skaitos.");
            } else if(target.getMoney() >= fine)
                target.sendMessage(Color.LIGHTRED, "Pinigai buvo paimti i� j�s� laikom�.");
            else
                target.sendMessage(Color.LIGHTRED, "D�l J�s� ma�� pajam�, buvote atleistas nuo baudos.");
            penalties.jail(target, JailData.JailType.InCharacter, minutes, player);
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia u�sid�ti policijos �enklel�")
    public boolean badge(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerAttach.PlayerAttachSlot slot = PlayerUtils.getSlotByBone(player, PlayerAttachBone.CLAVICLE_RIGHT);
        if(slot == null || slot.isUsed())
            player.sendErrorMessage("Jau esate ka�k� u�sid�j�s �ioje vietoje.");
        else {
            slot.set(PlayerAttachBone.CLAVICLE_RIGHT, 19347, new Vector3D(0.071999f, -0.112999f, 0.036999f), new Vector3D(115.699981f, -2.099976f ,-36.599925f), new Vector3D(), 0, 0);
            slot.edit();
            player.sendMessage(Color.POLICE, "[LSPD] Dabar nusistatykite norim� pozicij� �enkleliui,");
            player.sendMessage(Color.LIGHTRED, "[LSPD] Nor�dami pa�alinti �enklel� naudokita komand�: /rbadge.");
        }
        return true;
    }

    @Command
    @CommandHelp("Nuima policijos �enklel�")
    public boolean rBadge(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerAttach.PlayerAttachSlot slot = PlayerUtils.getSlotByBone(player, PlayerAttachBone.CLAVICLE_RIGHT);
        if(slot == null || !slot.isUsed() || slot.getModelId() != 19347)
            player.sendErrorMessage("J�s neesate u�sid�j�s policijos �enklelio.");
        else {
            slot.remove();
            player.sendMessage(Color.POLICE, "[LSPD] �enklelis buvo pa�alintas");
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia surakinti/atrakinti �aid�j� antrankiais")
    public boolean cuff(Player p, @CommandParameter(name = "�aid�jo ID/ Dalis vardo")LtrpPlayer target) {
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
        PlayerJobData jobData = player.getJobData();
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(player.getDistanceToPlayer(target) > 10f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else if(player.isInAnyVehicle()) {
            player.sendErrorMessage("B�damas transporto priemon�je negalite nieko tempti.");
        } else if(target.isInComa()) {
            player.sendErrorMessage(target.getCharName() + " yra komos b�senoje, j� tempti b�t� per daug pavojinga.");
        } else if(dragTimers.containsKey(player)) {
            player.sendActionMessage(jobData.getRank().getName() + " " + player.getCharName() + "nustotojo tempti/traukti" + target.getCharName());
            target.sendInfoText("~w~Tempiamas");
            target.toggleControllable(false);
            return true;
        } else {
            dragTimers.put(player, DragTimer.create(player, target));
            player.sendActionMessage(jobData.getRank().getName() + " " + player.getCharName() + " prad�jo tempti/traukti " + target.getCharName());
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
        PlayerJobData jobData = player.getJobData();
        if(backupRequests.keySet().contains(player)) {
            for(LtrpPlayer backup : backupRequests.get(player)) {
                backup.getCheckpoint().disable(player);
            }
            jobData.getJob().sendMessage(Color.LIGHTRED, "|DI�PE�ERIN� PRANE�A| D�MESIO, pareig�nas " + player.getCharName() + " at�auk� pastiprinimo pra�ym�.");
            backupRequests.remove(player);
        } else {
            backupRequests.put(player, new ArrayList<>());
            jobData.getJob().sendMessage(Color.LIGHTRED, "|DI�PE�ERIN� PRANE�A| D�MESIO VISIEMS PADALINIAMS, pareig�nas " + player.getCharName() + " pra�o skubaus pastiprinimo, vietos kordinat�s nustatytos J�s� GPS..");
            jobData.getJob().sendMessage(Color.LIGHTRED, "|DI�PE�ERIN� PRANE�A| Jeigu galite atvykti � pastiprinim� ra�ykite prane�kite dipe�erinei. ((/abk " + player.getId() + "))");
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
            t.setCheckpoint(Checkpoint.create(new Radius(t.getLocation(), 5.0f), (ppp) -> ppp.disableCheckpoint(), null));
            backupRequests.get(t).add(player);
            return true;
        }
        return true;
    }

    @Command
    public boolean killCheckpoint(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        Optional<List<LtrpPlayer>> backup = backupRequests.values().stream().filter(l -> l.contains(player)).findFirst();
        if(backup.isPresent()) {
            player.disableCheckpoint();
            backup.get().remove(player);
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
            return false;
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
                    // TODO
                    //ItemController.get().getItemDao().delete(weapon);
                    target.getInventory().remove(weapon);
                    weapon.destroy();
                }
                player.sendActionMessage("apie�ko " + target.getCharName() + " ir atima visus ginklus.");
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
                                PlayerLicenseController.instance.remove(license);
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
                DrugItem[] drugs = target.getInventory().getItems(DrugItem.class);
                if(drugs.length == 0)
                    player.sendErrorMessage(target.getName() + " neturi narkotik�!");
                else {
                    for(DrugItem drug : drugs) {
                        // TODO
                        //ItemController.get().getItemDao().delete(drug);
                        drug.destroy();
                        target.getInventory().remove(drug);
                    }
                    player.sendActionMessage("apie�ko " + target.getCharName() + " ir atima visus narkotikus.");
                }
            }
        }
        return true;
    }

    @Command
    @CommandHelp("licwarn")
    public boolean licWarn(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")Player p2,
                           @CommandParameter(name = "�sp�jimo apra�ymas/prie�astis")String warningText) {
        // TODO kol kas licwarn galima naudoti tik vairavimo teis�ms, ateityje reik�t� prid�ti ir kit� licenzij�
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpPlayer target = LtrpPlayer.get(p2);
        if(target == null || warningText == null) {
            return false;
        } else if(player.equals(target)) {
            player.sendErrorMessage("Sau �sp�jimo duoti negalite!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli!");
        } else if(!target.getLicenses().contains(LicenseType.Car)) {
            player.sendErrorMessage(target.getCharName() + " neturi vairavimo licenzijos!");
        } else {
            PlayerLicenseController.instance.insertWarning(target.getLicenses().get(LicenseType.Car), warningText, player);
            target.sendMessage("J�s gavote vairavimp �sp�jim� nuo pareig�no " + player.getCharName() + ". Pa�eidimas: " + warningText + ".");
            player.sendMessage(target.getCharName() + " �sp�tas.");
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia persirengti policijos drabu�iais")
    public boolean pGear(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PoliceGearListDialog.create(player, eventManager)
                .show();
        return true;
    }

    @Command
    public boolean duty(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(policePlugin.isOnDuty(player)) {
            player.sendActionMessage("pasid�jo savo turimus ginklus � savo ginkl� saugykl�.");
            player.removeJobWeapons();
            player.setColor(Color.WHITE);
        } else {
            player.sendActionMessage("atsidaro savo ginkl� saugykl�.");
            player.setArmour(100f);
            player.setColor(new Color(0x8d8dffAA));
            PoliceWeaponryDialog.create(player, eventManager, job);
        }
        policePlugin.setOnDuty(player, !policePlugin.isOnDuty(player));
        return true;
    }

    @Command
    @CommandHelp("Leid�ia persirengti b�nant b�stin�je")
    public boolean pdClothes(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        SkinModelPreview.create(player, eventManager, (preview, skindId) -> {
            player.setSkin(skindId);
        });
        return true;
    }

    @Command
    @CommandHelp("Are�tuoja artimiausi� transporto priemon�")
    public boolean arrestCar(Player p, @CommandParameter(name = "Are�to prie�astis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle vehicle = PlayerVehicle.Companion.getClosest(player, 8f);
        if(vehicle == null) {
            player.sendErrorMessage("Prie j�s� n�ra jokios transporto priemon�s.");
        } else if(PawnFunc.isPlayerInRangeOfCoords(player, 40f, "job_police_confiscated_garage")) {
            player.sendErrorMessage("Turite b�ti policijos konfiskuot� transporto priemoni� gara�e.");
        } else {
            LtrpPlayer vehicleOwner = LtrpPlayer.get(vehicle.getOwnerId());
            if(vehicleOwner != null) {
                player.sendMessage(Color.NEWS, " Policijos pareig�nas " + player.getCharName() + "  are�tavo J�s� automobil�" + vehicle.getModelName() + ", kurio numeriai: " + vehicle.getLicense());
            }
            job.sendMessage(Color.POLICE, String.format("%s %s are�tavo %s, valstybiniai numeriai %s. Prie�astis: %s.",
                    player.getJobData().getRank().getName(),
                    player.getCharName(),
                    vehicle.getModelName(),
                    vehicle.getLicense(),
                    reason));
            PlayerVehiclePlugin.get(PlayerVehiclePlugin.class).getVehicleArrestDao().insert(vehicle.getUUID(), player.getUUID(), reason);
            vehicle.destroy();
            eventManager.dispatchEvent(new PlayerVehicleArrestEvent(player, vehicle, reason));
        }
        return true;
    }

    @Command
    @CommandHelp("Pa�alina are�tuota transporto priemon�")
    public boolean delArrestCar(Player p, @CommandParameter(name = "Auto numeriai ARBA �aid�jo ID")String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehiclePlugin plugin = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class);
        if(!StringUtils.isNumeric(params)) {
            PlayerVehicleArrest arrest = plugin.getArrest(params);
            if(arrest != null) {
                MsgboxDialog dialog = ConfirmDelArrestMsgboxDialog.create(player, eventManager, arrest);
                dialog.setClickOkHandler(d -> {
                    job.sendMessage(Color.POLICE, "Policijos pareig�nas " + player.getCharName() + " nutrauk� are�t� tr. priemonei");
                    plugin.getVehicleArrestDao().remove(arrest);
                    eventManager.dispatchEvent(new PlayerVehicleArrestDeleteEvent(player, arrest));
                });
                dialog.show();
            } else {
                player.sendErrorMessage("Transporto priemon� neberasta arba ji neare�tuota.");
            }
        } else {
            LtrpPlayer target = LtrpPlayer.get(Integer.parseInt(params));
            if(target == null) {
                player.sendErrorMessage("Tokio �aid�jo n�ra!");
            } else if(target.getDistanceToPlayer(player) > 10f) {
                player.sendErrorMessage(target.getCharName() + " per toli.");
            } else {
                Collection<ListDialogItem> items = new ArrayList<>();
                for(int vehicleId : plugin.getArrestedVehicles(target)) {
                    PlayerVehicleMetadata meta = plugin.getMetaData(vehicleId);
                    items.add(new ListDialogItem(meta, "", (d, i) -> {
                        PlayerVehicleArrest arrest = plugin.getVehicleArrestDao().get(meta.getId());
                        MsgboxDialog dialog = ConfirmDelArrestMsgboxDialog.create(player, eventManager, arrest);
                        dialog.setClickOkHandler(dd -> {
                            job.sendMessage(Color.POLICE, "Policijos pareig�nas " + player.getCharName() + " nutrauk� are�t� tr. priemonei");
                            plugin.getVehicleArrestDao().remove(arrest);
                            eventManager.dispatchEvent(new PlayerVehicleArrestDeleteEvent(player, arrest));
                        });
                        dialog.show();
                    }));
                }
                if(items.size() == 0) {
                    player.sendErrorMessage(target.getCharName() + " neturi nei vienos are�tuotos transporto priemon�s.");
                } else {
                    ListDialog.create(player, eventManager)
                            .caption(target.getCharName() + " are�tuoti atuomobiliai")
                            .buttonOk("Pasirinkti")
                            .buttonCancel("At�aukti")
                            .items(items)
                            .build()
                            .show();
                }

            }
        }
        return true;
    }

    @Command
    @CommandHelp("Paima/padeda neper�aunama liemen�")
    public boolean vest(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getArmour() > 0f) {
            player.sendMessage(Color.POLICE, "[LSPD] Neper�aunama liemen� buvo nuimta.");
            player.setArmour(0f);
            player.getAttach().getSlotByBone(PlayerAttachBone.NECK).remove();
        } else {
            player.sendMessage(Color.POLICE, "[LSPD] Neper�aunama liemen� buvo u�d�ta.");
            player.setArmour(100f);
            player.getAttach().getSlotByBone(PlayerAttachBone.NECK).set(PlayerAttachBone.NECK, 19142, new Vector3D(1f, 0.1f, 0.05f), new Vector3D(), new Vector3D(), 0, 0);
            player.getAttach().getSlotByBone(PlayerAttachBone.NECK).edit();
        }
        return true;
    }


    @Command
    @CommandHelp("Laikinai paskiria �aid�j� � SWAT b�r�")
    public boolean setSwat(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                           @CommandParameter(name = "Tipas [0-2]")int type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else if(policePlugin.isSwat(target))
            player.sendErrorMessage(target.getName() + " jau yra SWAT b�ryje, nor�dami pakeisti jo rol� naudokite /unsetSwat");
        else if(type < 0 || type >= SwatType.values().length)
            player.sendErrorMessage("Galimi tipai 0 - " + SwatType.values().length);
        else if(player.getDistanceToPlayer(target) > 5f)
            player.sendErrorMessage("�aid�jas yra per toli.");
        else {
            policePlugin.setSwat(target, SwatType.values()[type]);
            player.sendMessage(Color.POLICE, target.getName() + " paskirtas � SWAT b�r�.");
            target.sendMessage(Color.POLICE, player.getName() + " paskyr� � SWAT b�r� kaip " + SwatType.values()[type].name());
        }
        return true;
    }

    @Command
    @CommandHelp("Laikinai paskiria �aid�j� � SWAT b�r�")
    public boolean unsetSwat(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else if(policePlugin.isSwat(target))
            player.sendErrorMessage("�aid�jas n�ra SWAT b�ryje.");
        else if(player.getDistanceToPlayer(target) > 5f)
            player.sendErrorMessage("�aid�jas yra per toli.");
        else {
            policePlugin.unsetSwat(target);
            player.sendMessage(Color.POLICE, target.getName() + " pa�alintas i� SWAT b�rio");
            target.sendMessage(Color.POLICE, player.getName() + " pa�alino jus i� SWAT b�rio");
        }
        return true;
    }
}