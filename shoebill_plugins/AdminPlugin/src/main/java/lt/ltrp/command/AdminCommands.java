package lt.ltrp.command;

import lt.ltrp.*;
import lt.ltrp.constant.Currency;
import lt.ltrp.constant.ItemType;
import lt.ltrp.constant.WorldZone;
import lt.ltrp.data.*;
import lt.ltrp.dialog.*;
import lt.ltrp.dialog.item.ItemTypeListDialog;
import lt.ltrp.event.PlayerSetFactionLeaderEvent;
import lt.ltrp.event.RemoveFactionLeaderEvent;
import lt.ltrp.event.player.PlayerToggleAdminDutyEvent;
import lt.ltrp.object.*;
import lt.ltrp.util.AdminLog;
import lt.ltrp.util.Skin;
import lt.maze.fader.FaderPlugin;
import lt.maze.mapandreas.MapAndreas;
import lt.maze.shoebilleventlogger.ShoebillEventLoggerPlugin;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.*;
import net.gtaun.util.event.EventManager;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class AdminCommands {

    private static final Map<String, Integer> adminLevels = new HashMap<>();
    private static final Map<String, Location> teleportLocations = new HashMap<>();

    static {
        adminLevels.put("apos", 1);
        adminLevels.put("ahelp", 1);
        adminLevels.put("getoldcar", 1);
        adminLevels.put("rc", 1);
        adminLevels.put("goto", 1);
        adminLevels.put("gotoloc", 1);
        adminLevels.put("gethere", 1);
        adminLevels.put("gotonowhere", 1);
        adminLevels.put("rjc", 1);
        adminLevels.put("rtc", 1);
        adminLevels.put("rfc", 1);
        adminLevels.put("adminduty", 1);
        adminLevels.put("aduty", 1);
        adminLevels.put("kick", 1);
        adminLevels.put("ban", 1);
        adminLevels.put("warn", 1);
        adminLevels.put("jail", 1);
        adminLevels.put("noooc", 1);
        adminLevels.put("are", 1);
        adminLevels.put("dre", 1);
        adminLevels.put("reports", 1);
        adminLevels.put("olddriver", 1);
        adminLevels.put("check", 1);
        adminLevels.put("afrisk", 1);
        adminLevels.put("setskin", 1);
        adminLevels.put("aproperty", 1);
        adminLevels.put("freeze", 1);
        adminLevels.put("slap", 1);
        adminLevels.put("masked", 1);
        adminLevels.put("lastad", 1);
        adminLevels.put("a", 1);
        adminLevels.put("checkjail", 1);
        adminLevels.put("setvw", 1);
        adminLevels.put("setint", 1);
        adminLevels.put("ao", 1);
        adminLevels.put("ado", 1);
        adminLevels.put("checkflist", 1);
        adminLevels.put("apkills", 1);

        adminLevels.put("dtc", 2);
        adminLevels.put("gotopos", 2);
        adminLevels.put("gotocar", 2);
        adminLevels.put("mute", 2);
        adminLevels.put("aheal", 2);
        adminLevels.put("ipban", 2);
        adminLevels.put("setweather", 2);
        adminLevels.put("togglefading", 2);

        adminLevels.put("sethp", 3);
        adminLevels.put("setarmour", 3);
        adminLevels.put("checkgun", 3);
        adminLevels.put("serverwweapons", 3);
        adminLevels.put("kickall", 3);
        adminLevels.put("rac", 3);

        adminLevels.put("makefactionmanager", 4);
        adminLevels.put("makeleader", 4);
        adminLevels.put("makemoderator", 4);
        adminLevels.put("removeleader", 4);
        adminLevels.put("auninvite", 4);
        adminLevels.put("bizitems", 4);
        adminLevels.put("abiz", 4);
        adminLevels.put("ahou", 4);
        adminLevels.put("agarage", 4);
        adminLevels.put("serverstats", 4);
        adminLevels.put("biztax", 4);
        adminLevels.put("cartax", 4);
        adminLevels.put("housetax", 4);
        adminLevels.put("garagetax", 4);
        adminLevels.put("vattax", 4);
        adminLevels.put("givemoney", 4);
        adminLevels.put("amenu", 4);

        adminLevels.put("giveitem", 6);
        adminLevels.put("fly", 6);
        adminLevels.put("vehicledmg", 6);
        adminLevels.put("eventlog", 6);

        teleportLocations.put("pc", new Location(2292.1936f, 26.7535f, 25.9974f, 0, 0));
        teleportLocations.put("ls", new Location(1540.1237f, -1675.2844f, 13.5500f, 0, 0));
        teleportLocations.put("mg", new Location(1313.8589f, 314.4103f, 19.4098f, 0, 0));
        teleportLocations.put("bb", new Location(230.9343f, -146.9140f, 1.4297f, 0, 0));
        teleportLocations.put("dl", new Location(641.5609f, -559.9846f, 16.0626f, 0, 0));
        teleportLocations.put("fc", new Location(-183.3534f, 1034.6022f, 19.7422f, 0, 0));
        teleportLocations.put("lb", new Location(-837.1216f, 1537.0032f, 22.5471f, 0, 0));
    }

    private AdminController controller;
    private EventManager eventManager;
    private PlayerPlugin playerPlugin;
    private int[] lastUsedGotoNowhereTimestamp;

    public AdminCommands(AdminController controller, EventManager eventManager) {
        this.controller = controller;
        this.eventManager = eventManager;
        this.playerPlugin = PlayerPlugin.get(PlayerPlugin.class);
        this.lastUsedGotoNowhereTimestamp = new int[Server.get().getMaxPlayers()];
    }


    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        cmd = cmd.toLowerCase();
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            System.out.println("AdminCommandst :: beforeCheck. Player: "+  p.getName() + " cmd: " +cmd + " params:" + params + " required admin level:" +
                    adminLevels.get(cmd) + " player admin levle:" + player.getAdminLevel());
        } else System.out.println("that cant even happen");
        if(!adminLevels.containsKey(cmd))
            return false;
        if(player.getAdminLevel() < adminLevels.get(cmd)) {
            if(player.getAdminLevel() > 0) {
                player.sendErrorMessage("Jûsø neturite teisës naudoti ðios komandos. Komandai " + cmd + " reikalingas " + adminLevels.get(cmd) + " lygis.");
            } else {
                player.sendErrorMessage("Jûs neturite teisës naudoti ðios komandos.");
            }
            return false;
        }
        
        return true;
    }



    @CommandHelp("Parodo visø administratoriaus komandø sàraðà")
    @Command(name = "ahelp")
    public boolean ahelp(Player player) {
        LtrpPlayer p = LtrpPlayer.get(player);
        p.sendMessage(Color.LIGHTRED,  "|____________________________ADMINISTRATORIAUS SKYRIUS____________________________|");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] /kick /ban /warn /jail /noooc /adminduty /gethere /check /afrisk /fon ");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] /freeze /slap /spec /specoff /setint /setvw /intvw /masked /aheal /spawn ");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] /mark /rc  /setskin  /aproperty /apkills /fon /pos /lastad /a /checkjail /checkflist");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] PERSIKËLIMAS: /gotoloc /goto /gotomark /gotobiz /gotohouse /gotogarage /gotopos /gotonowhere");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] TR. PRIEMONËS: /getoldcar /rtc /rfc /rjc /rc /are /dre /reports /olddriver");
        if(p.getAdminLevel() >= 2)
            p.sendMessage(Color.WHITE, "[AdmLvl 2] /dtc /gotocar /mute /rac /ipban /setweather /togglefading");
        if(p.getAdminLevel() >= 3)
            p.sendMessage(Color.WHITE, "[AdmLvl 3] /sethp /setarmour /forcelogout /hideadmins /serverwweapons /checkgun /kickall ");
        if(p.getAdminLevel() >= 4)
        {
            p.sendMessage(Color.WHITE, "[AdmLvl 4] /auninvite /givemoney /giveweapon /amenu /intmenu");
            p.sendMessage(Color.WHITE, "[AdmLvl 4] /makeleader /setstat /setstatcar /gotohouse /gotobiz");
            p.sendMessage(Color.WHITE, "[AdmLvl 4] /makeadmin /makemoderator /cartax /housetax /biztax /vehicletax /vattax");
            p.sendMessage(Color.WHITE, "[AdmLvl 4] /makefactinomanager  /giveitem ");
        }
        return true;
    }

    @Command
    @Deprecated
    public boolean togq(Player p) {
        p.sendMessage("Komanda paðalinta. Naudokite /settings");
        return true;
    }

    @Command
    @CommandHelp("Iðjungia/ájungia globalø OOC chatà /o")
    public boolean noOoc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpGamemodeImpl impl = LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class);
        impl.setOocChatEnabled(impl.isOocChatEnabled());
        if(impl.isOocChatEnabled()) {
            LtrpPlayer.sendGlobalMessage("Administratorius " + player.getName() + " ájungë globalø OOC chat'à /o");
        } else {
            LtrpPlayer.sendGlobalMessage("Administratorius " + player.getName() + " iðjungë globalø OOC chat'à.");
        }
        return true;
    }

    @Command
    public boolean aDuty(Player p) {
        return adminDuty(p);
    }

    @Command
    @CommandHelp("Priima þaidëjo pateiktà reportà")
    public boolean aRe(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdminPlugin adminPlugin = AdminPlugin.get(AdminPlugin.class);
        PlayerReport report;
        if(target == null)
            return false;
        else if(!target.isOnline())
            player.sendErrorMessage("Þaidëjas atsijungë.");
        else if((report = adminPlugin.getPlayerReport(target)) == null)
            player.sendErrorMessage("Ðis þaidëjas nëra pateikæs raporto.");
        else {
            report.setAnswered(true);
            target.sendMessage(Color.GREEN, "Dëmesio, Administratorius pavirtino Jûsø praneðimà (/report) ir tuojaus susisieks su Jumis. Bûkite kantrûs.");
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) patvirtino praneðimà (/report) ið (%s) ", player.getName(), target.getName()));
            AdminLog.incrementReportAccepted(player);
            AdminLog.log(player, target, "Patvirtino /report ið veikëjo " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Atmeta þaidëjo pateiktà raportà")
    public boolean aDre(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "Raporto atmetimo prieþastis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdminPlugin adminPlugin = AdminPlugin.get(AdminPlugin.class);
        PlayerReport report;
        if(target == null || reason == null)
            return false;
        else if(!target.isOnline())
            player.sendErrorMessage("Þaidëjas atsijungë.");
        else if((report = adminPlugin.getPlayerReport(target)) == null)
            player.sendErrorMessage("Ðis þaidëjas nëra pateikæs raporto.");
        else {
            report.setAnswered(true);
            target.sendMessage(Color.GREEN, String.format("Dëmesio, Administratorius %s atmetë Jûsø praneðimà (/report) nes: %s ", player.getName(), reason));
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) atmetë praneðimà (/report) ið (%s)", player.getName(), target.getName()));
            AdminLog.incrementRepportRejected(player);
            AdminLog.log(player, "Atmetë /report ið veikëjo " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia perþiûrëti paskutinius " + AdminPlugin.REPORT_CACHE_SIZE + " raportus")
    public boolean reports(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdminPlugin adminPlugin = AdminPlugin.get(AdminPlugin.class);
        new PlayerReportListDialog(player, eventManager, adminPlugin.getReports()).show();
        return true;
    }

    @Command
    @CommandHelp("Parodo kas paskutinis sedëjo transporto priemonëje")
    public boolean oldDriver(Player p, @CommandParameter(name = "Transporto priemonës ID")LtrpVehicle vehicle) {
        LtrpPlayer player= LtrpPlayer.get(p);
        if(vehicle == null)
            return false;
        else {
            Integer uuid = VehiclePlugin.get(VehiclePlugin.class).getLastDriver(vehicle);
            if(uuid == null)
                player.sendErrorMessage("Nëra duomenø.");
            else {
                String username = PlayerController.get().getUsernameByUUID(uuid);
                LtrpPlayer target = LtrpPlayer.get(uuid);
                player.sendMessage(Color.CADETBLUE, "Paskutinis ðioje transporto priemonëje sedëjo  " + target);
                if(target != null)
                    player.sendMessage(Color.CADETBLUE, "Þaidëjas prisijungæs, jo ID " + target.getId());
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo pasirinkto þaidëjo informacijà")
    public boolean check(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        else {
            PlayerPlugin.get(PlayerPlugin.class).showStats(player, target);
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo þaidëjo daiktus ir turimus ginklus")
    public boolean aFrisk(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else if(target.getInventory().isEmpty())
            player.sendErrorMessage("Þaidëjo inventoriuje nieko nëra!");
        else {
            target.getInventory().show(player);
            player.sendMessage(Color.GREEN, "__________________TURIMI GINKLAI_____________");
            for(LtrpWeaponData weaponData : target.getWeapons()) {
                player.sendMessage(Color.WHITE, String.format("ID %d. Ginklas %s %d kulkos.", weaponData.getUUID(), weaponData.getModel().getName(), weaponData.getAmmo()));
            }
            AdminLog.log(player, target, "Checked players inventory");
        }
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia þinutæ á administratoriø chatà")
    public boolean a(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(text == null)
            return false;
        else {
            LtrpPlayer.sendAdminMessage(String.format("[Adm. level: %d] %s[ID:%d]: %s", player.getAdminLevel(), player.getName(), player.getId(), text));
        }
        return true;
    }

    @Command
    @CommandHelp("Patikrina kiek þaidëjui liko laiko sedëti kalëjime.")
    public boolean checkJail(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else {
            JailData data = PenaltyPlugin.get(PenaltyPlugin.class).getJailData(target);
            if(data == null || data.getRemainingTime() <= 0)
                player.sendErrorMessage("Ðis þaidëjas nëra kalëjime.");
            else {
                player.sendMessage(Color.GREEN, String.format("Þaidëjas %s(%d) yra kalëjime. Tipas: %s, likæs laikas: %s",
                        target.getName(),
                        target.getId(),
                        data.getType().name(),
                        new SimpleDateFormat("HH:mm:ss").format(new Date(data.getRemainingTime() * 60 * 1000))));
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo pasirinktos frakcijos prisijungusiø þaidëjø sàraðà")
    public boolean checkFList(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        JobListDialog.create(player, eventManager)
                .selectJobHandler((d, j) -> {
                    JobPlugin jobPlugin = JobPlugin.get(JobPlugin.class);
                    Collection<LtrpPlayer> onlineEmployees = LtrpPlayer.get()
                            .stream()
                            .filter(p -> {
                                PlayerJobData jd = jobPlugin.getJobData(p);
                                return jd != null && jd.getJob().equals(j);
                            })
                            .collect(Collectors.toList());
                    if(onlineEmployees.size() == 0)
                        player.sendErrorMessage("Nëra nei vieno prisijungusio þaidëjo, kuris dirba " + j.getName());
                    else {
                        onlineEmployees.forEach(p -> {
                            PlayerJobData jd = jobPlugin.getJobData(player);
                            player.sendMessage(Color.GRAY, String.format("%s(%d) [%s(rangas %d)]",
                                    p.getName(),
                                    p.getId(),
                                    jd.getJobRank().getName(),
                                    jd.getJobRank().getNumber()));
                        });
                        player.sendMessage(Color.GREEN, "Ið viso " + onlineEmployees.size() + " prisijungusiø darbuotojø.");
                    }
                })
                .build()
                .show();
        return true;
    }

    @Command
    @CommandHelp("Pakeièia þaidëjo virtualø pasaulá á pasirinktà")
    public boolean setVW(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Virtualaus pasaulio ID")int worldId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeitë jûsø virtualøjá pasaulá.");
        player.sendMessage(Color.GREEN, target.getName() + " virtualus pasaulis pakeistas á " + worldId + ".");
        target.getLocation().setWorldId(worldId);
        AdminLog.log(player, target, "Changed players " + target.getUUID() + " virtual world to " + worldId);
        return true;
    }

    @Command
    @CommandHelp("Pakeièia þaidëjo interjerà á pasirinktà")
    public boolean setInt(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Interjero ID")int interiorId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeitë jûsø interjerà.");
        player.sendMessage(Color.GREEN, target.getName() + " interjeras pakeistas á " + interiorId + ".");
        target.getLocation().setInteriorId(interiorId);
        AdminLog.log(player, target, "Changed players " + target.getUUID() + " interior to " + interiorId);
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia administratoriaus OOC þinutæ")
    public boolean aO(Player p, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(text == null)
            return false;
        else {
            LtrpPlayer.sendGlobalOocMessage(String.format("(( Adm %s[%d]: %s ))", player.getName(), player.getId(), text));
            AdminLog.log(player, "Said in AOOC:" + text);
        }
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia globalià DO þinutæ")
    public boolean aDo(Player p, @CommandParameter(name = "Veiksmas")String text) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(text == null)
            return false;
        else {
            LtrpPlayer.sendGlobalMessage(Color.ACTION, text);
            AdminLog.log(player, "Sent ado: "+ text);
        }
        return true;
    }

    @Command
    @CommandHelp("Pakeièia þaidëjo iðvaizdà")
    public boolean setSkin(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                           @CommandParameter(name = "Skino ID")int skinId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else if(!Skin.isValid(skinId))
            player.sendErrorMessage("Tokio skino nëra.");
        else {
            target.setSkin(skinId);
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeitë jûsø iðvaizdà.");
            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pakeitë þaidëjo " + target.getName() + " skin'à á " + skinId);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " skin to " + skinId);
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo þaidëjo turimà turtà ir transporto priemones")
    public boolean aProperty(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else {
            Collection<House> houses = House.get().stream().filter(h -> h.isOwner(target)).collect(Collectors.toList());
            Collection<Business> businesses = Business.get().stream().filter(b -> b.isOwner(target)).collect(Collectors.toList());
            Collection<Garage> garages = Garage.get().stream().filter(g -> g.isOwner(target)).collect(Collectors.toList());
            PlayerVehiclePlugin vehiclePlugin = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class);
            int[] vehicles = vehiclePlugin.getVehicleUUIDs(target);

            if(houses.size() > 0) {
                houses.forEach(h -> {
                    player.sendMessage(Color.HOUSE, String.format("ID [%d]. Vertë: %d Rajonas %s:",
                            h.getUUID(), h.getPrice(), WorldZone.get(h.getEntrance())));
                });
                player.sendMessage(Color.HOUSE, "Bendra namø vertë: " + houses.stream().collect(Collectors.summingInt(House::getPrice)));
            } else player.sendMessage(Color.HOUSE, "Þaidëjas namø neturi.");

            if(businesses.size() > 0) {
                businesses.forEach(b -> {
                    player.sendMessage(Color.BUSINESS, String.format("ID [%d]. Vertë: %d Rajonas %s:",
                            b.getUUID(), b.getPrice(), WorldZone.get(b.getEntrance())));
                });
                player.sendMessage(Color.BUSINESS, "Bendra verslø vertë: " + businesses.stream().collect(Collectors.summingInt(Property::getPrice)));
            } else player.sendMessage(Color.BUSINESS, "Þaidëjas verslø neturi.");

            if(garages.size() > 0) {
                garages.forEach(g -> {
                    player.sendMessage(Color.GARAGE, String.format("ID [%d]. Vertë: %d Rajonas %s:",
                            g.getUUID(), g.getPrice(), WorldZone.get(g.getEntrance())));
                });
                player.sendMessage(Color.BUSINESS, "Bendra garaþø vertë: " + garages.stream().collect(Collectors.summingInt(Property::getPrice)));
            } else player.sendMessage(Color.GARAGE, "Þaidëjas garaþø neturi.");

            if(vehicles.length > 0) {
                for(int uuid : vehicles) {
                    PlayerVehicleMetadata meta = vehiclePlugin.getMetaData(uuid);
                    player.sendMessage(Color.BEIGE, String.format("ID: [%d] Modelis: [%s] Iðspwninta: [%b]", meta.getId(), VehicleModel.getName(meta.getModelId()), vehiclePlugin.isSpawned(uuid)));
                }
            } else player.sendMessage(Color.WHITE, "Þaidëjas transporto priemoniø neturi.");
        }
        return true;
    }

    @Command
    @CommandHelp("Ájungia/iðjungia þaidëjø mirèiø þintues")
    @Deprecated
    public boolean apKills(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.sendErrorMessage("Nustatymai persikële á /settings.");
        return true;
    }

    @Command
    @CommandHelp("Uþðaldo/atðildo þaidëjà(leidþia/uþdraudþia jam judëti")
    public boolean freeze(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else if(target.isFrozen()) {
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) uþðaldë (/freeze) veikëjà (%s)", player.getName(), target.getName()));
            target.sendInfoText("~w~ UZSALDYTAS", 4000);
            target.unfreeze();
            AdminLog.log(player, target, "Unfrozen player "+ target.getUUID());
        } else {
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) atðaldë (/freeze) veikëjà (%s)", player.getName(), target.getName()));
            target.sendInfoText("~w~ ATSALDYTAS", 4000);
            target.freeze();
            AdminLog.log(player, target, "Frozen player " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Pliaukðteli þaidëjui(atima 5 hp)")
    public boolean slap(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else {
            if(player.equals(target))
                player.sendMessage(Color.TAN, "Nemuðkite saves!");
            if(target.getHealth() > 5f)
                target.setHealth(target.getHealth() - 5f);
            target.setLocation(target.getLocation());
            target.playSound(1130);
            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pliaukðtelëjo þaidëjui " + target.getName());
            AdminLog.log(player, target, "Slapped player " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo þaidëjø, uþsidëjusiø kaukes sàraðà")
    public boolean masked(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Collection<LtrpPlayer> masked = LtrpPlayer.get().stream().filter(LtrpPlayer::isMasked).collect(Collectors.toList());
        if(masked.size() == 0)
            player.sendErrorMessage("Nëra nei vieno þaidëjo uþsidëjusio kaukæ!");
        else {
            player.sendMessage(Color.GREEN, "_________Þaidëjau uþsidëjæ kaukes_____________");
            masked.forEach(m -> player.sendMessage(Color.WHITE, String.format("ID: %d, MySQL ID: %d %s(%s)", m.getId(), m.getUUID(), m.getName(), m.getMaskName())));
        }
        return true;
    }

    @Command
    @CommandHelp("Pradeda/uþbaigia administracijos budëjimà")
    public boolean adminDuty(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(controller.getAdminsOnDuty().contains(player)) {
            eventManager.dispatchEvent(new PlayerToggleAdminDutyEvent(player, true));
        } else {
            eventManager.dispatchEvent(new PlayerToggleAdminDutyEvent(player, false));
        }
        return true;
    }

    @Command
    @CommandHelp("Pasodina þaidëjà á OOC kalëjimà nurodytam laikui(minutëmis)")
    public boolean jail(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "Bausmës trukmë(minutëmis)")int minutes,
                        @CommandParameter(name = "Kalëjimo prieþastis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        if(target == null || reason == null)
            return false;
        else {
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Administratorius %s pasodino á kalëjimà %s, %d minutëms.",
                    player.getName(), target.getName(), minutes));
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Nurodytà prieþastis: %s ", reason));
            penaltyPlugin.jail(target, JailData.JailType.OutOfCharacter, minutes, player);
            AdminLog.log(player, target, "Pasodino " + target.getUUID() + " á OOC kalëjimà " + minutes + " minutëms. Prieþastis:" + reason);

        }
        return true;
    }

    @Command
    @CommandHelp("Áspëja þaidëjà(Max " + PenaltyPlugin.MAX_WARNS + " áspëjimai")
    public boolean warn(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "Áspëjimo prieþastis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        if(target == null || reason == null)
            return false;
        else if(target.getAdminLevel() > player.getAdminLevel())
            player.sendErrorMessage("Negalite áspëti aukðtesnio lygio administratoriaus.");
        else {
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Administratorius %s áspëjo þaidëja %s, prieþastis: %s",
                    player.getName(), target.getName(), reason));
            penaltyPlugin.warn(target, reason, player);
            AdminLog.log(player, target, "Áspëjo þadiëjà " + target.getUUID() +". Prieþastis: " + reason);

        }
        return true;
    }

    @Command
    @CommandHelp("Uþblokuoja þaidëjà(NE IP)")
    public boolean ban(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                       @CommandParameter(name = "Blokavimo prieþastis")String reason,
                       @CommandParameter(name = "Valandø skaièius, 0 - visam laikui")int hours) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        if(target == null || reason == null)
            return false;
        else if(target.getAdminLevel() > player.getAdminLevel())
            player.sendErrorMessage("Negalite blokuoti aukðtesnio lygio administratoriaus.");
        else {
            if(hours > 0) {
                LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s uþdraudë þaisti þaidëjui %s %d valandoms, prieþastis: %s",
                        player.getName(), target.getName(), hours, reason));
                penaltyPlugin.banPlayer(target, reason, hours, player);
                AdminLog.log(player, target, "Uþblokavo " + target.getUUID() + " vartotojà " + hours + " valandoms. Prieþastis: " + reason);
            } else {
                LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s uþdraudë þaisti þaidëjui %s, prieþastis: %s",
                        player.getName(), target.getName(), reason));
                penaltyPlugin.banPlayer(target, reason, player);
                AdminLog.log(player, target, "Uþblokavo " + target.getUUID() + " vartotojà visam laikui. Prieþastis: " + reason);
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Uþblokuoti þaidëjà ir jo IP adresà")
    public boolean ipBan(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Blokavimo prieþastis")String reason,
                         @CommandParameter(name = "Valandø skaièius, 0 - visam laikui")int hours) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        if(target == null || reason == null)
            return false;
        else if(target.getAdminLevel() > player.getAdminLevel())
            player.sendErrorMessage("Negalite blokuoti aukðtesnio lygio administratoriaus.");
        else {
            if(hours > 0) {
                LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s uþdraudë þaisti þaidëjui %s %d valandoms, prieþastis: %s",
                        player.getName(), target.getName(), hours, reason));
                penaltyPlugin.banIp(target, reason, hours, player);
                AdminLog.log(player, target, "Uþblokavo " + target.getUUID() + " vartotojo IP " + target.getIp() +"  " + hours + " valandoms. Prieþastis " + reason);
            } else {
                LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s uþdraudë þaisti þaidëjui %s, prieþastis: %s",
                        player.getName(), target.getName(), reason));
                penaltyPlugin.banIp(target, reason, player);
                AdminLog.log(player, target, "Uþblokavo " + target.getUUID() + " vartotojo IP " + target.getIp() + " visam laikui. Prieþastis: " + reason);
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Pakeièia serverio orà")
    public boolean setWeather(Player p, @CommandParameter(name = "Serverio oro ID")int weatherId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        World.get().setWeather(weatherId);
        LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pakeitë serverio orà á " + weatherId);
        AdminLog.log(player, "Changed weather to " + weatherId);
        return true;
    }

    @Command
    @CommandHelp("Ájungia/iðjungia vaizdo aptemima VISIEMS þaidëjams")
    public boolean toggleFading(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        FaderPlugin plugin = FaderPlugin.get(FaderPlugin.class);
        plugin.setDisabled(!plugin.isDisabled());
        if(plugin.isDisabled()) {
            LtrpPlayer.sendAdminMessage(player.getName() + " iðjungë ekrano aptemimà visiems þaidëjams");
        } else {
            LtrpPlayer.sendAdminMessage(player.getName() + " ájungë ekrano aptemimà visiems þaidëjams");
        }
        AdminLog.log(player, "Set player fading disabled to " + plugin.isDisabled());
        return true;
    }

    @Command
    @CommandHelp("Graþina visas nenaudojamas tr. priemones á jø atsiradimo vietà.")
    public boolean rac(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle.get().stream().filter(v -> !v.isUsed()).forEach(LtrpVehicle::respawn);
        LtrpPlayer.sendGlobalMessage("Administratorius " + player.getName() + " atstatë visas nenaudojamas transporto priemones.");
        AdminLog.log(player, "Respawned all vehicles");
        return true;
    }

    @Command
    @CommandHelp("Leidþia perþiûrëti visus serveryje esanèius tam tikro modelio ginklus")
    public boolean checkGun(Player p, @CommandParameter(name = "Ginklo modelio ID")int modelId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        WeaponModel weaponModel = WeaponModel.get(modelId);
        if(weaponModel == null)
            return false;

        ServerWeaponListDialog.create(player, eventManager, weaponModel)
                .show();
        return true;
    }

    @Command
    @CommandHelp("Iðmeta visus þaidëjus(iðskyrus administratorius) ið serverio")
    public boolean kickAll(Player pp, @CommandParameter(name = "Prieþastis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        if(reason == null)
            return false;
        else {
            LtrpPlayer.sendGlobalMessage(Color.RED, "Administratorius " + player.getName() + " iðmetë visus þaidëjus ið serverio. Prieþastis:" + reason);
            AdminLog.log(player, "Kicked all players. Reason:" + reason);
            LtrpPlayer.get().stream().filter(p -> !p.isAdmin())
                    .forEach(Player::kick);
        }
        return true;
    }

    @Command
    @CommandHelp("Nustato pasirinkto þaidëjo gyvybiø skaièiø")
    public boolean setHp(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Gyvybiø skaièius")float amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        else {
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeitë jûsø gyvybiø skaièiø.");
            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pakeitë þaidëjo " + target.getName() + " gyvybiø skaièiø á " + amount);
            target.setHealth(amount);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " health to " + amount);
        }
        return true;
    }


    @Command
    @CommandHelp("Nustato pasirinkto þaidëjo ðaarvø skaièiø")
    public boolean setArmour(Player p,
                             @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                             @CommandParameter(name = "Ðarvø skaièius")float amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        else {
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeitë jûsø ðarvø skaièiø.");
            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pakeitë þaidëjo " + target.getName() + " ðarvø skaièiø á " + amount);
            target.setArmour(amount);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " armour to " + amount);
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia perþiûrët visus serveryje esanèius ne darbinius ginklus")
    public boolean serverWeapons(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        ServerWeaponListDialog.create(player, eventManager)
                .show();
        return true;
    }

    @Command
    @CommandHelp("Iðmeta pasirinktà þaidëjà ið serverio")
    public boolean kick(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "Iðmetimo prieþastis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null || reason == null)
            return false;
        else if(target.getAdminLevel() > player.getAdminLevel())
            player.sendErrorMessage("Negalite iðmesti aukðtesnio lygio administratoriaus.");
        else {
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s iðspyrë þaidëjà  %s ið serverio, prieþastis: %s",
                    player.getName(), target.getName(), reason));
            target.kick();
            AdminLog.log(player, target, "Iðmetë vartotojà " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Atkelia nurodytà transporto priemonæ á jûsø pozicijà")
    public boolean getoldcar(Player player, @CommandParameter(name = "Transporto priemonës ID")Vehicle vehicle) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(vehicle == null)
            p.sendMessage(Color.LIGHTRED, "Transporto priemonës su tokiu ID nëra.");
        else {
            vehicle.setLocation(p.getLocation());
        }
        return true;
    }

    @Command
    @CommandHelp("Nukelia jus prie þaidëjo")
    public boolean goTo(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else {
            player.setLocation(target.getLocation());
            player.sendMessage(Color.GREEN, "Sëkmingai nusiteleportavai.");
        }
        return true;
    }

    @Command
    @CommandHelp("Atkelia þaidëjà prie jûsø")
    public boolean getHere(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else {
            if(target.isInAnyVehicle())
                target.getVehicle().setLocation(player.getLocation());
            else
                target.setLocation(player.getLocation());
            target.sendMessage(Color.GREEN, "Jûs buvote nuteleportuotas ðalia administratoriaus.");
        }
        return true;
    }


    @Command
    @CommandHelp("Nukelia jus á vienà ið galimø vietoviø, pvz.: ls, bb")
    public boolean gotoloc(Player player, @CommandParameter(name = "Vietovë. Pvz: ls, bb")String pos) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(pos == null || !teleportLocations.keySet().contains(pos)) {
            p.sendErrorMessage("Vietovë " + pos + "  neegzistuoja.");
            String msg = "Galimos vietovës: ";
            for(String s : teleportLocations.keySet()) {
                msg += s + " ";
            }
            p.sendMessage(Color.WHITE, msg);
        } else {
            if(p.getVehicle() != null) {
                p.getVehicle().setLocation(teleportLocations.get(pos));
            } else {
                p.setLocation(teleportLocations.get(pos));
            }
            p.sendMessage(Color.CHOCOLATE, "Persikëlëte á " + pos);
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo paskutinius skelbimus")
    public boolean lastAd(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Collection<Advert> ads = AdvertPlugin.get(AdvertPlugin.class).getAds();
        AdvertisementListDialog.create(player, eventManager, ads)
                .build()
                .show();
        return true;
    }

    @Command
    public boolean pos(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.sendMessage(Color.GREEN, String.format("Jûsø koordinatës - X %.2f, Y %.2f Z %.2f", player.getLocation().x, player.getLocation().y, player.getLocation().z));
        return true;
    }

    @Command
    @CommandHelp("Nukelia jus á pasirinktas koordinates")
    public boolean gotopos(Player player, Float x, Float y, Float z) {
        if(x == null || y == null || z == null) {
            player.sendMessage("Ne. ne taip");
            return false;
        }
        if(x != 0f && y != 0f && z != 0f) {
            player.setLocation(x, y, z);
        }
        return true;
    }

    @Command
    @CommandHelp("Nukelia jus.... á niekur/kaþkur")
    public boolean gotoNowhere(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Instant now = Instant.now();
        if(now.getEpochSecond() - lastUsedGotoNowhereTimestamp[p.getId()] < 5)
            player.sendErrorMessage("Ðià komandà galima naudoti tik kas 5 sekundes.");
        else {
            Random random = new Random();
            float x = random.nextFloat() * -4000 + 2000;
            float y = random.nextFloat() * -4000 + 2000;
            float z = MapAndreas.findZ(x, y);
            player.setLocation(new Location(x, y, z, 0, 0));
        }
        return true;
    }

    @Command
    @CommandHelp("Nukelia jus prie pasirinktos transporot priemonës")
    public boolean gotoCar(Player p, LtrpVehicle vehicle) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(vehicle == null) {
            player.sendErrorMessage("Tokios transporto priemonës nëra!");
        } else {
            player.setLocation(vehicle.getLocation());
            player.sendMessage(Color.NEWS, "Nusikëlëte prie " + vehicle.getModelName() + "(ID:" + vehicle.getId() + ")");
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia/nebeleidþia þaidëjui kalbëti")
    public boolean mute(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra");
        else if(target.isMuted()) {
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) leido kalbëti (/unmute) veikëjui (%s)", player.getName(), target.getName()));
            target.sendMessage(Color.GREEN, "Administratorius vël leido jums kalbëti.");
            target.unMute();
            AdminLog.log(player, target, "Unmuted player " + target.getUUID());
        } else {
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) uþdraudë kalbëti (/mute) veikëjui (%s)", player.getName(), target.getName()));
            target.sendMessage(Color.GREEN," Administratorius " + player.getName() + " uþdraudë jums kalbëti.");
            target.mute();
            AdminLog.log(player, target, "Muted player " + player.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Atstato transporto priemonæ á atsiradimo vietà")
    public boolean rc(Player player, @CommandParameter(name = "Transporto priemonës ID")Vehicle veh) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(veh == null) {
            p.sendMessage(Color.LIGHTRED, "Transporto priemonës su tokiu ID nëra.");
        }
        return true;
    }


    @Command
    @CommandHelp("Paskiria nurodytà þaidëjà frakcijø priþiûrëtoju")
    public boolean makeFactionManager(Player player, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null) {
            p.sendMessage(Color.LIGHTRED, "Tokio þaidëjo nëra.");
        } else {
            PlayerController.get().getPlayerDao().setFactionManager(p2);
            p2.setFactionManager(true);

            LtrpPlayer.sendAdminMessage("Administratorius " + p.getName() + " suteikë þaidëjui " + p2.getName() + " frakcijø priþiûrëtojo rangà.");

            p2.sendMessage(Color.NEWS, "Administratorius " + p.getName() + " paskyrë jus frakcijø priþiûrëtoju.");

            AdminLog.log(p, p2, "Paskyrë þaidëjà " + p2.getName() + " frakcijø priþirëtoju.");
        }
        return true;
    }

    @Command
    @CommandHelp("Paverèia þaidëjà moderatorium")
    public boolean makeModerator(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                                 @CommandParameter(name = "Moderatoriaus lygis")int level) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        else if(level < 0)
            player.sendErrorMessage("Neigiamas moderatoriaus lygis bûti negali.");
        else {
            target.setModLevel(level);
            playerPlugin.getPlayerDao().update(target);
            player.sendMessage(Color.GREEN, String.format(" Administratorius (%s) suteikë veikëjui (%s) moderatoriaus statusà. ", player.getName(), target.getName()));
            target.sendMessage(Color.MODERATOR, " Sveikiname, jus buvote priimtas á moderatoriø grupæ. Informacija /modhelp ");
            AdminLog.log(player, target, "Set players " + target.getUUID() + " mod level " + level);
        }
        return true;
    }

    @Command
    @CommandHelp("Paskiria þaidëjà frakcijos lyderiu")
    public boolean makeLeader(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        } else if(JobController.get().isLeader(target)) {
            player.sendErrorMessage(target.getName() + " jau yra kitos frakcijos lyderis. Paðalinti já galite su /removeLeader");
        } else {
            FactionListDialog.create(player, eventManager, JobController.get().getFactions())
                    .caption("Pasirinkite frakcijà")
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Iðeiti")
                    .onSelectFaction((d, f) -> {
                        String message = "Ar tikrai norite pridëti " + target.getName() + " á frakcijos " + f.getName() + " lyderius?" +
                                "\n\nDabartinis(-ai) lyderis(-ai):\n";
                        for(int leaderId : f.getLeaders()) {
                            LtrpPlayer leader = LtrpPlayer.get(leaderId);
                            if(leader != null) {
                                message += leader.getName() + "(ID:" + leader.getId() + ")\n";
                            } else {
                                message += PlayerController.get().getPlayerDao().getUsername(leaderId) + "\n";
                            }
                        }

                        MsgboxDialog.create(player, eventManager)
                                .caption("Dëmesio.")
                                .message(message)
                                .buttonOk("Taip")
                                .buttonCancel("Ne")
                                .onClickOk(dd -> {
                                    JobController.get().setJob(target, f, f.getRank(f.getRanks().size()-1));
                                    LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pridëjo " + target.getName() + " á frakcijos " + f.getName() + " lyderius.");
                                    target.sendMessage(Color.NEWS, "Administratorius " + player.getName() + " paskyrë jus, frakcijos " + f.getName() + " lyderiu!");
                                    f.sendMessage(Color.NEWS, target.getName() + " buvo paskirtas naujuoju frakcijos lyderiu!");
                                    eventManager.dispatchEvent(new PlayerSetFactionLeaderEvent(target, f));
                                })
                                .build()
                                .show();
                    })
                    .build()
                    .show();
        }
        return true;
    }

    @Command
    @CommandHelp("Paðalina pasirinktos frakcijos lyderá, nesvarbu prisijungæs jis ar ne")
    public boolean removeLeader(Player p, String username) {
        LtrpPlayer player = LtrpPlayer.get(p);
        int userId = PlayerController.get().getPlayerDao().getUserId(username);
        if(userId == LtrpPlayer.INVALID_USER_ID) {
            player.sendErrorMessage("Tokio vartotojo nëra!");
        } else if(!JobController.get().isLeader(userId)) {
            player.sendErrorMessage("Vartotojas " + username + " nevadovauja jokiai frakcijai.");
        } else {
            Optional<Faction> factionOp = JobController.get().getFactions().stream().filter(f -> f.getLeaders().contains(userId)).findFirst();
            if(factionOp.isPresent()) {
                Faction f = factionOp.get();
                MsgboxDialog.create(player, eventManager)
                        .caption("Frakcijos lyderio ðalinimas")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .message("Ar tikrai norite paðalinti " + username + " ið frakcijos " + f.getName() + " lyderiu?" +
                            "\nÐiuo metu frakcija turi " + f.getLeaders().size() + " lyderius.")
                        .onClickOk(d -> {
                            eventManager.dispatchEvent(new RemoveFactionLeaderEvent(userId, f));
                            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " paðalino frakcijos " + f.getName() + " lyderá " + username + " ið pareigø.");
                            f.sendMessage(Color.NEWS, "Lyderis " + username + " buvo paðalintas ið pareigø.");
                            JobController.get().getFactionDao().removeLeader(f, userId);
                        })
                        .build().show();
            }
        }

        return true;
    }

    @Command
    @CommandHelp("Suteikia nurodytà daiktà nurodytam þaidëjui")
    public boolean giveItem(Player player, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null)
            p.sendErrorMessage("Tokio þaidëjo nëra!");
        else if(p2.getInventory().isFull())
            p.sendErrorMessage("Þaidëjo inventorius pilnas.");
        else {
            List<ItemType> types = Arrays.asList(ItemType.values());
            ItemTypeListDialog.create(p, eventManager, types, (d, t) -> {
                Item item = Item.create(t, p2, eventManager);
                if(item == null)
                    p.sendErrorMessage("Ávyko klaida. Daiktas nebuvo suteiktas. Tikëtina kad ðio daikto suteikti taip paprastai negalima, sorry plz come again.");
                else {
                    p2.getInventory().add(item);

                    p2.sendMessage(Color.GREEN, "Administratorius " + p.getName() + " davë jums daiktà \"" + item.getName() + "\".");
                    LtrpPlayer.sendAdminMessage(p.getName() + " davë " + item.getName() + " þaidëjui " + p2.getName());
                    AdminLog.log(p, p2, "Gave item " + item.getUUID() + " to player " + p2.getUUID());
                }
            });
        }
        return true;
    }


    @Command
    @CommandHelp("Atstato visas nenaudojamas kontraktinio darbo transporto priemones á atsiradimo vietà")
    public boolean rjc(Player p, ContractJob job) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(job != null) {
            int count = 0;
            for(JobVehicle jobVehicle : job.getVehicles()) {
                if(!jobVehicle.isUsed()) {
                    jobVehicle.respawn();
                    count++;
                }
            }
            player.sendMessage("Atstatytos " + count + " darbo " + job.getName() + " transporto priemonës.");
            LtrpPlayer.sendGlobalMessage("Administratorius atstatë visas nenaudojamas darbo " + job.getName() + "transporto priemones.");
        } else
            player.sendErrorMessage("Darbo su tokiu ID nëra.");
        return true;
    }

    @Command
    @CommandHelp("Atstato transporto priemonæ á jos atsiradimo vietà ir pripildo jos bakà")
    public boolean rtc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 5f);
        if(vehicle == null)
            player.sendErrorMessage("Prie jûsø nëra jokios transporto priemonës.");
        else {
            vehicle.respawn();
            vehicle.getFuelTank().addFuel(vehicle.getFuelTank().getSize());
            player.sendMessage(Color.GREEN, "Transporto priemonë gràþinta á atsiradimo vietà, degalai atstatyti.");
            AdminLog.log(player, "Reseted and refueled vehicle " + vehicle.getUUID());
        }
        return true;
    }


    @Command
    @CommandHelp("Atstato visas nenaudojamas frakcinio darbo transporto priemones á atsiradimo vietà")
    public boolean rfc(Player p, Faction faction) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(faction != null) {
            int count = 0;
            for(JobVehicle jobVehicle : faction.getVehicles()) {
                if(!jobVehicle.isUsed()) {
                    jobVehicle.respawn();
                    count++;
                }
            }
            player.sendMessage("Atstatytos " + count + " darbo " + faction.getName() + " transporto priemonës.");
            LtrpPlayer.sendGlobalMessage("Administratorius atstatë visas nenaudojamas darbo " + faction.getName() + "transporto priemones.");
        } else
            player.sendErrorMessage("Frakcijos su tokiu ID nëra.");
        return true;
    }

    @Command
    @CommandHelp("Priparkuoja transporto priemonæ")
    public boolean dtc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle playerVehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        if(playerVehicle == null)
            playerVehicle = PlayerVehicle.getClosest(player, 4f);
        if(playerVehicle == null)
            player.sendErrorMessage("Turite bûti transporto priemonëje arba prie jos(tinka tik þaidëjams priklausanèios tr. priemonës). ");
        else {
            String targetUsername = PlayerController.get().getUsernameByUUID(playerVehicle.getOwnerId());
            player.sendMessage(Color.GREEN, targetUsername + " automobilis " + playerVehicle.getModelName() + " sëkmingai priparkuotas.");
            player.destroy();
            LtrpPlayer target = LtrpPlayer.get(playerVehicle.getOwnerId());
            if(target != null)
                target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " priverstinai priparkavo jûsø " + playerVehicle.getModelName() + ", vël jà iðparkuoti glaite su /v get.");
            AdminLog.log(player, "Priparkavo tr. priemonæ " + playerVehicle.getUUID());
        }
        return true;
    }

    @Command()
    @CommandHelp("Pagydo þaidëjà bei prikelia já ið komos")
    public boolean aheal(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else {
            target.setHealth(100f);
            if(target.isInAnyVehicle()) {
                target.getVehicle().repair();
            }
            if(target.isInComa()) {
                target.setInComa(false);
                if(player.getCountdown() != null)
                    player.getCountdown().forceStop();
            }
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pagydë jus.");
            player.sendMessage(Color.GREEN, "Þaidëjas " + target.getName() + "(ID:" + target.getId() + ") pagydytas");
            AdminLog.log(player, target, "Healed user " + target.getName() + " uid: " + target.getUUID());
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Dont")
    public boolean fly(Player player) {
        if(player.getSpecialAction() == SpecialAction.NONE) {
            player.setSpecialAction(SpecialAction.USE_JETPACK);
        } else
            player.setSpecialAction(SpecialAction.NONE);
        return true;
    }

    @Command
    public boolean vehicledmg(Player p, Vehicle v) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle;
        if(v == null) {
            vehicle = LtrpVehicle.getClosest(player, 5f);
        } else {
            vehicle = LtrpVehicle.getByVehicle(v);
        }
        if(vehicle != null) {
            VehicleDamage dmg = vehicle.getDamage();
            ListDialog.create(player, eventManager)
                    .caption(vehicle.getModelName())
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Iðeiti")
                    .item("Padangos " + Integer.toBinaryString(dmg.getTires()), i -> {
                        showBinaryInputDialog(player, "Padangø bûsena.",
                                "Dabartinë bûsena: " + Integer.toBinaryString(dmg.getTires()) + "(" + dmg.getTires() + ")",
                                (d, val) -> {
                                    dmg.setTires(val);
                                    player.sendMessage(Color.GREEN, "Padangø bûsena pakeista á" + Integer.toBinaryString(val));
                                }).show();
                    })
                    .item("Panelës " + Integer.toBinaryString(dmg.getPanels()), i -> {
                        showBinaryInputDialog(player, "Paneliø bûsena.",
                                "Dabartinë bûsena: " + Integer.toBinaryString(dmg.getPanels()) + "(" + dmg.getPanels() + ")",
                                (d, val) -> {
                                    dmg.setPanels(val);
                                    player.sendMessage(Color.GREEN, "Paneliø bûsena pakeista á" + Integer.toBinaryString(val));
                                }).show();
                    })
                    .item("Ðviesos " + Integer.toBinaryString(dmg.getLights()), i -> {
                        showBinaryInputDialog(player, "Ðviesø bûsena.",
                                "Dabartinë bûsena: " + Integer.toBinaryString(dmg.getLights()) + "(" + dmg.getLights() + ")",
                                (d, val) -> {
                                    dmg.setLights(val);
                                    player.sendMessage(Color.GREEN, "Ðviesø bûsena pakeista á" + Integer.toBinaryString(val));
                                }).show();
                    })
                    .item("Durys " + Integer.toBinaryString(dmg.getDoors()), i -> {
                        showBinaryInputDialog(player, "Durø bûsena.",
                                "Durø bûsena: " + Integer.toBinaryString(dmg.getDoors()) + "(" + dmg.getDoors() + ")",
                                (d, val) -> {
                                    dmg.setDoors(val);
                                    player.sendMessage(Color.GREEN, "Durø bûsena pakeista á" + Integer.toBinaryString(val));
                                }).show();
                    })
                    .build()
                    .show();
        }
        return true;
    }

    private InputDialog showBinaryInputDialog(LtrpPlayer player, String caption, String message, BinaryInputDiualogClickOkHandler inputHandler) {
        return InputDialog.create(player, eventManager)
                .caption(caption)
                .message(message)
                .onClickOk((d, s) -> {
                    s = s.trim().replaceAll(" ", "");
                    try {
                        int val = Integer.parseInt(s, 2);
                        inputHandler.onEnterBinary(d, val);
                    } catch (NumberFormatException e) {
                        player.sendErrorMessage("Praðome ávesti skaièiø, dvejetainiu formatu!");
                        d.show();
                    }
                })
                .build();
    }

    @FunctionalInterface
    private interface BinaryInputDiualogClickOkHandler {
        void onEnterBinary(InputDialog d, int val);
    }

    @Command
    @CommandHelp
    public boolean eventlog(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        ShoebillEventLoggerPlugin loggerPlugin = Shoebill.get().getResourceManager().getPlugin(ShoebillEventLoggerPlugin.class);
        if(loggerPlugin == null) {
            player.sendErrorMessage("Ðiuo metu plugin'as yra iðjungtas.");
        } else {
            ListDialog.create(p, eventManager)
                    .caption("Event logging")
                    .item("Globalus 'update' event loginimas[" + (loggerPlugin.isLogUpdateEvents() ? "+" : "-") + "]", i -> {
                        loggerPlugin.setLogUpdateEvents(!loggerPlugin.isLogUpdateEvents());
                        player.sendMessage("Update tipo event loginimas atnaujintas.");
                        i.getCurrentDialog().show();
                    })
                    .item("Mano loginimas[" + (loggerPlugin.isLoggingEnabled(p) ? "+" : "-") + "]", i -> {
                        if (loggerPlugin.isLoggingEnabled(p)) {
                            loggerPlugin.stopLogging(p);
                            player.sendMessage("Loginimas sustabdytas");
                        } else {
                            loggerPlugin.startLogging(p, false);
                            player.sendMessage("Loginimas pradëtas.");
                        }
                        i.getCurrentDialog().show();
                    })
                    .item("Mano 'update' event loginimas[" + (loggerPlugin.isUpdateLoggingEnabled(p) ? "+" : "-") + "]", i -> {
                        loggerPlugin.startLogging(p, !loggerPlugin.isUpdateLoggingEnabled(p));
                        player.sendMessage("Asmeninë 'update' tipo loginimo informacija atnaujinta.");
                        i.getCurrentDialog().show();
                    })
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Iðeiti")
                    .build()
                    .show();

        }
        return true;
    }


    @Command
    @CommandHelp("Iðmeta þaidëjà ið darbo")
    public boolean aUnInvite(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else {
            PlayerJobData jobData = JobController.get().getJobData(target);
            if(jobData.getJob() == null) {
                player.sendErrorMessage(target.getName() + " neturi darbo.");
            } else {
                Job job = jobData.getJob();
                target.removeJobWeapons();
                JobController.get().setJob(target, null);
                player.sendMessage(Color.GREEN, String.format("Þaidëjas %s(%d) iðmestas ið darbo \"%s\".", target.getName(), target.getId(), job.getName()));
                target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " iðmetë jus ið darbo.");
            }
        }
        return true;
    }


    @Command
    @CommandHelp("Atidaro serverio statistikos GUI")
    public boolean serverstats(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        ServerStatsMsgBoxDialog.create(player, eventManager)
                .show();
        return true;
    }

    @Command
    @CommandHelp("Pakeièia mokestá uþ vienà verslà")
    public boolean bizTax(Player p, @CommandParameter(name = "Naujas verslo mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali bûti maþesnis uþ 0.");
        else {
            LtrpWorld.get().getTaxes().setBusinessTax(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeitë verslø mokestá á " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed business tax to " + tax);
        }
        return true;
    }


    @Command
    @CommandHelp("Pakeièia mokestá uþ vienà garaþà")
    public boolean garageTax(Player p, @CommandParameter(name = "Naujas garaþo mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali bûti maþesnis uþ 0.");
        else {
            LtrpWorld.get().getTaxes().setGarageTax(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeitë garaþø mokestá á " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed garage tax to " + tax);
        }
        return true;
    }

    @Command
    @CommandHelp("Pakeièia mokestá uþ vienà namà")
    public boolean houseTax(Player p, @CommandParameter(name = "Naujas namø mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali bûti maþesnis uþ 0.");
        else {
            LtrpWorld.get().getTaxes().setHouseTax(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeitë namø mokestá á " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed house tax to " + tax);
        }
        return true;
    }

    @Command
    @CommandHelp("Pakeièia mokestá uþ vienà transporto priemonæ")
    public boolean carTax(Player p, @CommandParameter(name = "Naujas namø mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali bûti maþesnis uþ 0.");
        else {
            LtrpWorld.get().getTaxes().setVehicleTax(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeitë transporto priemoniø mokestá á " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed vehicle tax to " + tax);
        }
        return true;
    }

    @Command
    @CommandHelp("Pakeièia PVM mokesèio dydá")
    public boolean vatTax(Player p, @CommandParameter(name = "Naujas PVM mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali bûti maþesnis uþ 0.");
        else {
            LtrpWorld.get().getTaxes().setVAT(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeitë PVM mokestá á " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed VAT tax to " + tax);
        }
        return true;
    }

    @Command
    @CommandHelp("Duoda þaidëjui pinigø")
    public boolean giveMoney(Player p,
                             @CommandParameter(name = "Þaidëjo ID/ Dalis vardo")LtrpPlayer target,
                             @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        else {
            target.giveMoney(amount);
            LtrpPlayer.sendAdminMessage(String.format("Administratorius %s davë þaidëjui %s %d%c", player.getName(), target.getName(), amount, Currency.SYMBOL));
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " jums davë " + amount + Currency.SYMBOL);
            AdminLog.log(player, target, "Davë þaidëjui " + target.getUUID() + "  " + amount);
        }
        return true;
    }


    @Command
    @CommandHelp("Atidaro þymøjá \"amenu\"")
    public boolean aMenu(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdminServerManagementDialog.create(player, eventManager)
                .show();
        return true;
    }


    // TODO cmd:ado
    // TODO cmd:ao
}

