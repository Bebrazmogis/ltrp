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
                player.sendErrorMessage("J�s� neturite teis�s naudoti �ios komandos. Komandai " + cmd + " reikalingas " + adminLevels.get(cmd) + " lygis.");
            } else {
                player.sendErrorMessage("J�s neturite teis�s naudoti �ios komandos.");
            }
            return false;
        }
        
        return true;
    }



    @CommandHelp("Parodo vis� administratoriaus komand� s�ra��")
    @Command(name = "ahelp")
    public boolean ahelp(Player player) {
        LtrpPlayer p = LtrpPlayer.get(player);
        p.sendMessage(Color.LIGHTRED,  "|____________________________ADMINISTRATORIAUS SKYRIUS____________________________|");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] /kick /ban /warn /jail /noooc /adminduty /gethere /check /afrisk /fon ");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] /freeze /slap /spec /specoff /setint /setvw /intvw /masked /aheal /spawn ");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] /mark /rc  /setskin  /aproperty /apkills /fon /pos /lastad /a /checkjail /checkflist");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] PERSIK�LIMAS: /gotoloc /goto /gotomark /gotobiz /gotohouse /gotogarage /gotopos /gotonowhere");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] TR. PRIEMON�S: /getoldcar /rtc /rfc /rjc /rc /are /dre /reports /olddriver");
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
        p.sendMessage("Komanda pa�alinta. Naudokite /settings");
        return true;
    }

    @Command
    @CommandHelp("I�jungia/�jungia global� OOC chat� /o")
    public boolean noOoc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpGamemodeImpl impl = LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class);
        impl.setOocChatEnabled(impl.isOocChatEnabled());
        if(impl.isOocChatEnabled()) {
            LtrpPlayer.sendGlobalMessage("Administratorius " + player.getName() + " �jung� global� OOC chat'� /o");
        } else {
            LtrpPlayer.sendGlobalMessage("Administratorius " + player.getName() + " i�jung� global� OOC chat'�.");
        }
        return true;
    }

    @Command
    public boolean aDuty(Player p) {
        return adminDuty(p);
    }

    @Command
    @CommandHelp("Priima �aid�jo pateikt� report�")
    public boolean aRe(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdminPlugin adminPlugin = AdminPlugin.get(AdminPlugin.class);
        PlayerReport report;
        if(target == null)
            return false;
        else if(!target.isOnline())
            player.sendErrorMessage("�aid�jas atsijung�.");
        else if((report = adminPlugin.getPlayerReport(target)) == null)
            player.sendErrorMessage("�is �aid�jas n�ra pateik�s raporto.");
        else {
            report.setAnswered(true);
            target.sendMessage(Color.GREEN, "D�mesio, Administratorius pavirtino J�s� prane�im� (/report) ir tuojaus susisieks su Jumis. B�kite kantr�s.");
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) patvirtino prane�im� (/report) i� (%s) ", player.getName(), target.getName()));
            AdminLog.incrementReportAccepted(player);
            AdminLog.log(player, target, "Patvirtino /report i� veik�jo " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Atmeta �aid�jo pateikt� raport�")
    public boolean aDre(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "Raporto atmetimo prie�astis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdminPlugin adminPlugin = AdminPlugin.get(AdminPlugin.class);
        PlayerReport report;
        if(target == null || reason == null)
            return false;
        else if(!target.isOnline())
            player.sendErrorMessage("�aid�jas atsijung�.");
        else if((report = adminPlugin.getPlayerReport(target)) == null)
            player.sendErrorMessage("�is �aid�jas n�ra pateik�s raporto.");
        else {
            report.setAnswered(true);
            target.sendMessage(Color.GREEN, String.format("D�mesio, Administratorius %s atmet� J�s� prane�im� (/report) nes: %s ", player.getName(), reason));
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) atmet� prane�im� (/report) i� (%s)", player.getName(), target.getName()));
            AdminLog.incrementRepportRejected(player);
            AdminLog.log(player, "Atmet� /report i� veik�jo " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia per�i�r�ti paskutinius " + AdminPlugin.REPORT_CACHE_SIZE + " raportus")
    public boolean reports(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdminPlugin adminPlugin = AdminPlugin.get(AdminPlugin.class);
        new PlayerReportListDialog(player, eventManager, adminPlugin.getReports()).show();
        return true;
    }

    @Command
    @CommandHelp("Parodo kas paskutinis sed�jo transporto priemon�je")
    public boolean oldDriver(Player p, @CommandParameter(name = "Transporto priemon�s ID")LtrpVehicle vehicle) {
        LtrpPlayer player= LtrpPlayer.get(p);
        if(vehicle == null)
            return false;
        else {
            Integer uuid = VehiclePlugin.get(VehiclePlugin.class).getLastDriver(vehicle);
            if(uuid == null)
                player.sendErrorMessage("N�ra duomen�.");
            else {
                String username = PlayerController.get().getUsernameByUUID(uuid);
                LtrpPlayer target = LtrpPlayer.get(uuid);
                player.sendMessage(Color.CADETBLUE, "Paskutinis �ioje transporto priemon�je sed�jo  " + target);
                if(target != null)
                    player.sendMessage(Color.CADETBLUE, "�aid�jas prisijung�s, jo ID " + target.getId());
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo pasirinkto �aid�jo informacij�")
    public boolean check(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        else {
            PlayerPlugin.get(PlayerPlugin.class).showStats(player, target);
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo �aid�jo daiktus ir turimus ginklus")
    public boolean aFrisk(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else if(target.getInventory().isEmpty())
            player.sendErrorMessage("�aid�jo inventoriuje nieko n�ra!");
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
    @CommandHelp("I�siun�ia �inut� � administratori� chat�")
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
    @CommandHelp("Patikrina kiek �aid�jui liko laiko sed�ti kal�jime.")
    public boolean checkJail(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else {
            JailData data = PenaltyPlugin.get(PenaltyPlugin.class).getJailData(target);
            if(data == null || data.getRemainingTime() <= 0)
                player.sendErrorMessage("�is �aid�jas n�ra kal�jime.");
            else {
                player.sendMessage(Color.GREEN, String.format("�aid�jas %s(%d) yra kal�jime. Tipas: %s, lik�s laikas: %s",
                        target.getName(),
                        target.getId(),
                        data.getType().name(),
                        new SimpleDateFormat("HH:mm:ss").format(new Date(data.getRemainingTime() * 60 * 1000))));
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo pasirinktos frakcijos prisijungusi� �aid�j� s�ra��")
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
                        player.sendErrorMessage("N�ra nei vieno prisijungusio �aid�jo, kuris dirba " + j.getName());
                    else {
                        onlineEmployees.forEach(p -> {
                            PlayerJobData jd = jobPlugin.getJobData(player);
                            player.sendMessage(Color.GRAY, String.format("%s(%d) [%s(rangas %d)]",
                                    p.getName(),
                                    p.getId(),
                                    jd.getJobRank().getName(),
                                    jd.getJobRank().getNumber()));
                        });
                        player.sendMessage(Color.GREEN, "I� viso " + onlineEmployees.size() + " prisijungusi� darbuotoj�.");
                    }
                })
                .build()
                .show();
        return true;
    }

    @Command
    @CommandHelp("Pakei�ia �aid�jo virtual� pasaul� � pasirinkt�")
    public boolean setVW(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Virtualaus pasaulio ID")int worldId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeit� j�s� virtual�j� pasaul�.");
        player.sendMessage(Color.GREEN, target.getName() + " virtualus pasaulis pakeistas � " + worldId + ".");
        target.getLocation().setWorldId(worldId);
        AdminLog.log(player, target, "Changed players " + target.getUUID() + " virtual world to " + worldId);
        return true;
    }

    @Command
    @CommandHelp("Pakei�ia �aid�jo interjer� � pasirinkt�")
    public boolean setInt(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Interjero ID")int interiorId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeit� j�s� interjer�.");
        player.sendMessage(Color.GREEN, target.getName() + " interjeras pakeistas � " + interiorId + ".");
        target.getLocation().setInteriorId(interiorId);
        AdminLog.log(player, target, "Changed players " + target.getUUID() + " interior to " + interiorId);
        return true;
    }

    @Command
    @CommandHelp("I�siun�ia administratoriaus OOC �inut�")
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
    @CommandHelp("I�siun�ia globali� DO �inut�")
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
    @CommandHelp("Pakei�ia �aid�jo i�vaizd�")
    public boolean setSkin(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                           @CommandParameter(name = "Skino ID")int skinId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else if(!Skin.isValid(skinId))
            player.sendErrorMessage("Tokio skino n�ra.");
        else {
            target.setSkin(skinId);
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeit� j�s� i�vaizd�.");
            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pakeit� �aid�jo " + target.getName() + " skin'� � " + skinId);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " skin to " + skinId);
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo �aid�jo turim� turt� ir transporto priemones")
    public boolean aProperty(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else {
            Collection<House> houses = House.get().stream().filter(h -> h.isOwner(target)).collect(Collectors.toList());
            Collection<Business> businesses = Business.get().stream().filter(b -> b.isOwner(target)).collect(Collectors.toList());
            Collection<Garage> garages = Garage.get().stream().filter(g -> g.isOwner(target)).collect(Collectors.toList());
            PlayerVehiclePlugin vehiclePlugin = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class);
            int[] vehicles = vehiclePlugin.getVehicleUUIDs(target);

            if(houses.size() > 0) {
                houses.forEach(h -> {
                    player.sendMessage(Color.HOUSE, String.format("ID [%d]. Vert�: %d Rajonas %s:",
                            h.getUUID(), h.getPrice(), WorldZone.get(h.getEntrance())));
                });
                player.sendMessage(Color.HOUSE, "Bendra nam� vert�: " + houses.stream().collect(Collectors.summingInt(House::getPrice)));
            } else player.sendMessage(Color.HOUSE, "�aid�jas nam� neturi.");

            if(businesses.size() > 0) {
                businesses.forEach(b -> {
                    player.sendMessage(Color.BUSINESS, String.format("ID [%d]. Vert�: %d Rajonas %s:",
                            b.getUUID(), b.getPrice(), WorldZone.get(b.getEntrance())));
                });
                player.sendMessage(Color.BUSINESS, "Bendra versl� vert�: " + businesses.stream().collect(Collectors.summingInt(Property::getPrice)));
            } else player.sendMessage(Color.BUSINESS, "�aid�jas versl� neturi.");

            if(garages.size() > 0) {
                garages.forEach(g -> {
                    player.sendMessage(Color.GARAGE, String.format("ID [%d]. Vert�: %d Rajonas %s:",
                            g.getUUID(), g.getPrice(), WorldZone.get(g.getEntrance())));
                });
                player.sendMessage(Color.BUSINESS, "Bendra gara�� vert�: " + garages.stream().collect(Collectors.summingInt(Property::getPrice)));
            } else player.sendMessage(Color.GARAGE, "�aid�jas gara�� neturi.");

            if(vehicles.length > 0) {
                for(int uuid : vehicles) {
                    PlayerVehicleMetadata meta = vehiclePlugin.getMetaData(uuid);
                    player.sendMessage(Color.BEIGE, String.format("ID: [%d] Modelis: [%s] I�spwninta: [%b]", meta.getId(), VehicleModel.getName(meta.getModelId()), vehiclePlugin.isSpawned(uuid)));
                }
            } else player.sendMessage(Color.WHITE, "�aid�jas transporto priemoni� neturi.");
        }
        return true;
    }

    @Command
    @CommandHelp("�jungia/i�jungia �aid�j� mir�i� �intues")
    @Deprecated
    public boolean apKills(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.sendErrorMessage("Nustatymai persik�le � /settings.");
        return true;
    }

    @Command
    @CommandHelp("U��aldo/at�ildo �aid�j�(leid�ia/u�draud�ia jam jud�ti")
    public boolean freeze(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else if(target.isFrozen()) {
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) u��ald� (/freeze) veik�j� (%s)", player.getName(), target.getName()));
            target.sendInfoText("~w~ UZSALDYTAS", 4000);
            target.unfreeze();
            AdminLog.log(player, target, "Unfrozen player "+ target.getUUID());
        } else {
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) at�ald� (/freeze) veik�j� (%s)", player.getName(), target.getName()));
            target.sendInfoText("~w~ ATSALDYTAS", 4000);
            target.freeze();
            AdminLog.log(player, target, "Frozen player " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Pliauk�teli �aid�jui(atima 5 hp)")
    public boolean slap(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else {
            if(player.equals(target))
                player.sendMessage(Color.TAN, "Nemu�kite saves!");
            if(target.getHealth() > 5f)
                target.setHealth(target.getHealth() - 5f);
            target.setLocation(target.getLocation());
            target.playSound(1130);
            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pliauk�tel�jo �aid�jui " + target.getName());
            AdminLog.log(player, target, "Slapped player " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo �aid�j�, u�sid�jusi� kaukes s�ra��")
    public boolean masked(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Collection<LtrpPlayer> masked = LtrpPlayer.get().stream().filter(LtrpPlayer::isMasked).collect(Collectors.toList());
        if(masked.size() == 0)
            player.sendErrorMessage("N�ra nei vieno �aid�jo u�sid�jusio kauk�!");
        else {
            player.sendMessage(Color.GREEN, "_________�aid�jau u�sid�j� kaukes_____________");
            masked.forEach(m -> player.sendMessage(Color.WHITE, String.format("ID: %d, MySQL ID: %d %s(%s)", m.getId(), m.getUUID(), m.getName(), m.getMaskName())));
        }
        return true;
    }

    @Command
    @CommandHelp("Pradeda/u�baigia administracijos bud�jim�")
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
    @CommandHelp("Pasodina �aid�j� � OOC kal�jim� nurodytam laikui(minut�mis)")
    public boolean jail(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "Bausm�s trukm�(minut�mis)")int minutes,
                        @CommandParameter(name = "Kal�jimo prie�astis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        if(target == null || reason == null)
            return false;
        else {
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Administratorius %s pasodino � kal�jim�%s, %d minut�ms.",
                    player.getName(), target.getName(), minutes));
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Nurodyt� prie�astis: %s ", reason));
            penaltyPlugin.jail(target, JailData.JailType.OutOfCharacter, minutes, player);
            AdminLog.log(player, target, "Pasodino " + target.getUUID() + " � OOC kal�jim� " + minutes + " minut�ms. Prie�astis:" + reason);

        }
        return true;
    }

    @Command
    @CommandHelp("�sp�ja �aid�j�(Max " + PenaltyPlugin.MAX_WARNS + " �sp�jimai")
    public boolean warn(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "�sp�jimo prie�astis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        if(target == null || reason == null)
            return false;
        else if(target.getAdminLevel() > player.getAdminLevel())
            player.sendErrorMessage("Negalite �sp�ti auk�tesnio lygio administratoriaus.");
        else {
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Administratorius %s �sp�jo �aid�ja %s, prie�astis: %s",
                    player.getName(), target.getName(), reason));
            penaltyPlugin.warn(target, reason, player);
            AdminLog.log(player, target, "�sp�jo �adi�j� " + target.getUUID() +". Prie�astis: " + reason);

        }
        return true;
    }

    @Command
    @CommandHelp("U�blokuoja �aid�j�(NE IP)")
    public boolean ban(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                       @CommandParameter(name = "Blokavimo prie�astis")String reason,
                       @CommandParameter(name = "Valand� skai�ius, 0 - visam laikui")int hours) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        if(target == null || reason == null)
            return false;
        else if(target.getAdminLevel() > player.getAdminLevel())
            player.sendErrorMessage("Negalite blokuoti auk�tesnio lygio administratoriaus.");
        else {
            if(hours > 0) {
                LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s u�draud� �aisti �aid�jui %s %d valandoms, prie�astis: %s",
                        player.getName(), target.getName(), hours, reason));
                penaltyPlugin.banPlayer(target, reason, hours, player);
                AdminLog.log(player, target, "U�blokavo " + target.getUUID() + " vartotoj� " + hours + " valandoms. Prie�astis: " + reason);
            } else {
                LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s u�draud� �aisti �aid�jui %s, prie�astis: %s",
                        player.getName(), target.getName(), reason));
                penaltyPlugin.banPlayer(target, reason, player);
                AdminLog.log(player, target, "U�blokavo " + target.getUUID() + " vartotoj� visam laikui. Prie�astis: " + reason);
            }
        }
        return true;
    }

    @Command
    @CommandHelp("U�blokuoti �aid�j� ir jo IP adres�")
    public boolean ipBan(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Blokavimo prie�astis")String reason,
                         @CommandParameter(name = "Valand� skai�ius, 0 - visam laikui")int hours) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PenaltyPlugin penaltyPlugin = PenaltyPlugin.get(PenaltyPlugin.class);
        if(target == null || reason == null)
            return false;
        else if(target.getAdminLevel() > player.getAdminLevel())
            player.sendErrorMessage("Negalite blokuoti auk�tesnio lygio administratoriaus.");
        else {
            if(hours > 0) {
                LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s u�draud� �aisti �aid�jui %s %d valandoms, prie�astis: %s",
                        player.getName(), target.getName(), hours, reason));
                penaltyPlugin.banIp(target, reason, hours, player);
                AdminLog.log(player, target, "U�blokavo " + target.getUUID() + " vartotojo IP " + target.getIp() +"  " + hours + " valandoms. Prie�astis " + reason);
            } else {
                LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s u�draud� �aisti �aid�jui %s, prie�astis: %s",
                        player.getName(), target.getName(), reason));
                penaltyPlugin.banIp(target, reason, player);
                AdminLog.log(player, target, "U�blokavo " + target.getUUID() + " vartotojo IP " + target.getIp() + " visam laikui. Prie�astis: " + reason);
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Pakei�ia serverio or�")
    public boolean setWeather(Player p, @CommandParameter(name = "Serverio oro ID")int weatherId) {
        LtrpPlayer player = LtrpPlayer.get(p);
        World.get().setWeather(weatherId);
        LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pakeit� serverio or� � " + weatherId);
        AdminLog.log(player, "Changed weather to " + weatherId);
        return true;
    }

    @Command
    @CommandHelp("�jungia/i�jungia vaizdo aptemima VISIEMS �aid�jams")
    public boolean toggleFading(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        FaderPlugin plugin = FaderPlugin.get(FaderPlugin.class);
        plugin.setDisabled(!plugin.isDisabled());
        if(plugin.isDisabled()) {
            LtrpPlayer.sendAdminMessage(player.getName() + " i�jung� ekrano aptemim� visiems �aid�jams");
        } else {
            LtrpPlayer.sendAdminMessage(player.getName() + " �jung� ekrano aptemim� visiems �aid�jams");
        }
        AdminLog.log(player, "Set player fading disabled to " + plugin.isDisabled());
        return true;
    }

    @Command
    @CommandHelp("Gra�ina visas nenaudojamas tr. priemones � j� atsiradimo viet�.")
    public boolean rac(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle.get().stream().filter(v -> !v.isUsed()).forEach(LtrpVehicle::respawn);
        LtrpPlayer.sendGlobalMessage("Administratorius " + player.getName() + " atstat� visas nenaudojamas transporto priemones.");
        AdminLog.log(player, "Respawned all vehicles");
        return true;
    }

    @Command
    @CommandHelp("Leid�ia per�i�r�ti visus serveryje esan�ius tam tikro modelio ginklus")
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
    @CommandHelp("I�meta visus �aid�jus(i�skyrus administratorius) i� serverio")
    public boolean kickAll(Player pp, @CommandParameter(name = "Prie�astis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        if(reason == null)
            return false;
        else {
            LtrpPlayer.sendGlobalMessage(Color.RED, "Administratorius " + player.getName() + " i�met� visus �aid�jus i� serverio. Prie�astis:" + reason);
            AdminLog.log(player, "Kicked all players. Reason:" + reason);
            LtrpPlayer.get().stream().filter(p -> !p.isAdmin())
                    .forEach(Player::kick);
        }
        return true;
    }

    @Command
    @CommandHelp("Nustato pasirinkto �aid�jo gyvybi� skai�i�")
    public boolean setHp(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                         @CommandParameter(name = "Gyvybi� skai�ius")float amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        else {
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeit� j�s� gyvybi� skai�i�.");
            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pakeit� �aid�jo " + target.getName() + " gyvybi� skai�i� � " + amount);
            target.setHealth(amount);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " health to " + amount);
        }
        return true;
    }


    @Command
    @CommandHelp("Nustato pasirinkto �aid�jo �aarv� skai�i�")
    public boolean setArmour(Player p,
                             @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                             @CommandParameter(name = "�arv� skai�ius")float amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        else {
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeit� j�s� �arv� skai�i�.");
            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pakeit� �aid�jo " + target.getName() + " �arv� skai�i� � " + amount);
            target.setArmour(amount);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " armour to " + amount);
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia per�i�r�t visus serveryje esan�ius ne darbinius ginklus")
    public boolean serverWeapons(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        ServerWeaponListDialog.create(player, eventManager)
                .show();
        return true;
    }

    @Command
    @CommandHelp("I�meta pasirinkt� �aid�j� i� serverio")
    public boolean kick(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                        @CommandParameter(name = "I�metimo prie�astis")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null || reason == null)
            return false;
        else if(target.getAdminLevel() > player.getAdminLevel())
            player.sendErrorMessage("Negalite i�mesti auk�tesnio lygio administratoriaus.");
        else {
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, String.format("Admin. %s i�spyr� �aid�j� %s i� serverio, prie�astis: %s",
                    player.getName(), target.getName(), reason));
            target.kick();
            AdminLog.log(player, target, "I�met� vartotoj� " + target.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Atkelia nurodyt� transporto priemon� � j�s� pozicij�")
    public boolean getoldcar(Player player, @CommandParameter(name = "Transporto priemon�s ID")Vehicle vehicle) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(vehicle == null)
            p.sendMessage(Color.LIGHTRED, "Transporto priemon�s su tokiu ID n�ra.");
        else {
            vehicle.setLocation(p.getLocation());
        }
        return true;
    }

    @Command
    @CommandHelp("Nukelia jus prie �aid�jo")
    public boolean goTo(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else {
            player.setLocation(target.getLocation());
            player.sendMessage(Color.GREEN, "S�kmingai nusiteleportavai.");
        }
        return true;
    }

    @Command
    @CommandHelp("Atkelia �aid�j� prie j�s�")
    public boolean getHere(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else {
            if(target.isInAnyVehicle())
                target.getVehicle().setLocation(player.getLocation());
            else
                target.setLocation(player.getLocation());
            target.sendMessage(Color.GREEN, "J�s buvote nuteleportuotas �alia administratoriaus.");
        }
        return true;
    }


    @Command
    @CommandHelp("Nukelia jus � vien� i� galim� vietovi�, pvz.: ls, bb")
    public boolean gotoloc(Player player, @CommandParameter(name = "Vietov�. Pvz: ls, bb")String pos) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(pos == null || !teleportLocations.keySet().contains(pos)) {
            p.sendErrorMessage("Vietov� " + pos + "  neegzistuoja.");
            String msg = "Galimos vietov�s: ";
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
            p.sendMessage(Color.CHOCOLATE, "Persik�l�te � " + pos);
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
        player.sendMessage(Color.GREEN, String.format("J�s� koordinat�s - X %.2f, Y %.2f Z %.2f", player.getLocation().x, player.getLocation().y, player.getLocation().z));
        return true;
    }

    @Command
    @CommandHelp("Nukelia jus � pasirinktas koordinates")
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
    @CommandHelp("Nukelia jus.... � niekur/ka�kur")
    public boolean gotoNowhere(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Instant now = Instant.now();
        if(now.getEpochSecond() - lastUsedGotoNowhereTimestamp[p.getId()] < 5)
            player.sendErrorMessage("�i� komand� galima naudoti tik kas 5 sekundes.");
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
    @CommandHelp("Nukelia jus prie pasirinktos transporot priemon�s")
    public boolean gotoCar(Player p, LtrpVehicle vehicle) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(vehicle == null) {
            player.sendErrorMessage("Tokios transporto priemon�s n�ra!");
        } else {
            player.setLocation(vehicle.getLocation());
            player.sendMessage(Color.NEWS, "Nusik�l�te prie " + vehicle.getModelName() + "(ID:" + vehicle.getId() + ")");
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia/nebeleid�ia �aid�jui kalb�ti")
    public boolean mute(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra");
        else if(target.isMuted()) {
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) leido kalb�ti (/unmute) veik�jui (%s)", player.getName(), target.getName()));
            target.sendMessage(Color.GREEN, "Administratorius v�l leido jums kalb�ti.");
            target.unMute();
            AdminLog.log(player, target, "Unmuted player " + target.getUUID());
        } else {
            LtrpPlayer.sendAdminMessage(String.format("Administratorius (%s) u�draud� kalb�ti (/mute) veik�jui (%s)", player.getName(), target.getName()));
            target.sendMessage(Color.GREEN," Administratorius " + player.getName() + " u�draud� jums kalb�ti.");
            target.mute();
            AdminLog.log(player, target, "Muted player " + player.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Atstato transporto priemon� � atsiradimo viet�")
    public boolean rc(Player player, @CommandParameter(name = "Transporto priemon�s ID")Vehicle veh) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(veh == null) {
            p.sendMessage(Color.LIGHTRED, "Transporto priemon�s su tokiu ID n�ra.");
        }
        return true;
    }


    @Command
    @CommandHelp("Paskiria nurodyt� �aid�j� frakcij� pri�i�r�toju")
    public boolean makeFactionManager(Player player, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null) {
            p.sendMessage(Color.LIGHTRED, "Tokio �aid�jo n�ra.");
        } else {
            PlayerController.get().getPlayerDao().setFactionManager(p2);
            p2.setFactionManager(true);

            LtrpPlayer.sendAdminMessage("Administratorius " + p.getName() + " suteik� �aid�jui " + p2.getName() + " frakcij� pri�i�r�tojo rang�.");

            p2.sendMessage(Color.NEWS, "Administratorius " + p.getName() + " paskyr� jus frakcij� pri�i�r�toju.");

            AdminLog.log(p, p2, "Paskyr� �aid�j� " + p2.getName() + " frakcij� pri�ir�toju.");
        }
        return true;
    }

    @Command
    @CommandHelp("Paver�ia �aid�j� moderatorium")
    public boolean makeModerator(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                                 @CommandParameter(name = "Moderatoriaus lygis")int level) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        else if(level < 0)
            player.sendErrorMessage("Neigiamas moderatoriaus lygis b�ti negali.");
        else {
            target.setModLevel(level);
            playerPlugin.getPlayerDao().update(target);
            player.sendMessage(Color.GREEN, String.format(" Administratorius (%s) suteik� veik�jui (%s) moderatoriaus status�. ", player.getName(), target.getName()));
            target.sendMessage(Color.MODERATOR, " Sveikiname, jus buvote priimtas � moderatori� grup�. Informacija /modhelp ");
            AdminLog.log(player, target, "Set players " + target.getUUID() + " mod level " + level);
        }
        return true;
    }

    @Command
    @CommandHelp("Paskiria �aid�j� frakcijos lyderiu")
    public boolean makeLeader(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra.");
        } else if(JobController.get().isLeader(target)) {
            player.sendErrorMessage(target.getName() + " jau yra kitos frakcijos lyderis. Pa�alinti j� galite su /removeLeader");
        } else {
            FactionListDialog.create(player, eventManager, JobController.get().getFactions())
                    .caption("Pasirinkite frakcij�")
                    .buttonOk("Pasirinkti")
                    .buttonCancel("I�eiti")
                    .onSelectFaction((d, f) -> {
                        String message = "Ar tikrai norite prid�ti " + target.getName() + " � frakcijos " + f.getName() + " lyderius?" +
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
                                .caption("D�mesio.")
                                .message(message)
                                .buttonOk("Taip")
                                .buttonCancel("Ne")
                                .onClickOk(dd -> {
                                    JobController.get().setJob(target, f, f.getRank(f.getRanks().size()-1));
                                    LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " prid�jo " + target.getName() + " � frakcijos " + f.getName() + " lyderius.");
                                    target.sendMessage(Color.NEWS, "Administratorius " + player.getName() + " paskyr� jus, frakcijos " + f.getName() + " lyderiu!");
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
    @CommandHelp("Pa�alina pasirinktos frakcijos lyder�, nesvarbu prisijung�s jis ar ne")
    public boolean removeLeader(Player p, String username) {
        LtrpPlayer player = LtrpPlayer.get(p);
        int userId = PlayerController.get().getPlayerDao().getUserId(username);
        if(userId == LtrpPlayer.INVALID_USER_ID) {
            player.sendErrorMessage("Tokio vartotojo n�ra!");
        } else if(!JobController.get().isLeader(userId)) {
            player.sendErrorMessage("Vartotojas " + username + " nevadovauja jokiai frakcijai.");
        } else {
            Optional<Faction> factionOp = JobController.get().getFactions().stream().filter(f -> f.getLeaders().contains(userId)).findFirst();
            if(factionOp.isPresent()) {
                Faction f = factionOp.get();
                MsgboxDialog.create(player, eventManager)
                        .caption("Frakcijos lyderio �alinimas")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .message("Ar tikrai norite pa�alinti " + username + " i� frakcijos " + f.getName() + " lyderiu?" +
                            "\n�iuo metu frakcija turi " + f.getLeaders().size() + " lyderius.")
                        .onClickOk(d -> {
                            eventManager.dispatchEvent(new RemoveFactionLeaderEvent(userId, f));
                            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pa�alino frakcijos " + f.getName() + " lyder� " + username + " i� pareig�.");
                            f.sendMessage(Color.NEWS, "Lyderis " + username + " buvo pa�alintas i� pareig�.");
                            JobController.get().getFactionDao().removeLeader(f, userId);
                        })
                        .build().show();
            }
        }

        return true;
    }

    @Command
    @CommandHelp("Suteikia nurodyt� daikt� nurodytam �aid�jui")
    public boolean giveItem(Player player, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null)
            p.sendErrorMessage("Tokio �aid�jo n�ra!");
        else if(p2.getInventory().isFull())
            p.sendErrorMessage("�aid�jo inventorius pilnas.");
        else {
            List<ItemType> types = Arrays.asList(ItemType.values());
            ItemTypeListDialog.create(p, eventManager, types, (d, t) -> {
                Item item = Item.create(t, p2, eventManager);
                if(item == null)
                    p.sendErrorMessage("�vyko klaida. Daiktas nebuvo suteiktas. Tik�tina kad �io daikto suteikti taip paprastai negalima, sorry plz come again.");
                else {
                    p2.getInventory().add(item);

                    p2.sendMessage(Color.GREEN, "Administratorius " + p.getName() + " dav� jums daikt� \"" + item.getName() + "\".");
                    LtrpPlayer.sendAdminMessage(p.getName() + " dav� " + item.getName() + " �aid�jui " + p2.getName());
                    AdminLog.log(p, p2, "Gave item " + item.getUUID() + " to player " + p2.getUUID());
                }
            });
        }
        return true;
    }


    @Command
    @CommandHelp("Atstato visas nenaudojamas kontraktinio darbo transporto priemones � atsiradimo viet�")
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
            player.sendMessage("Atstatytos " + count + " darbo " + job.getName() + " transporto priemon�s.");
            LtrpPlayer.sendGlobalMessage("Administratorius atstat� visas nenaudojamas darbo " + job.getName() + "transporto priemones.");
        } else
            player.sendErrorMessage("Darbo su tokiu ID n�ra.");
        return true;
    }

    @Command
    @CommandHelp("Atstato transporto priemon� � jos atsiradimo viet� ir pripildo jos bak�")
    public boolean rtc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 5f);
        if(vehicle == null)
            player.sendErrorMessage("Prie j�s� n�ra jokios transporto priemon�s.");
        else {
            vehicle.respawn();
            vehicle.getFuelTank().addFuel(vehicle.getFuelTank().getSize());
            player.sendMessage(Color.GREEN, "Transporto priemon� gr��inta � atsiradimo viet�, degalai atstatyti.");
            AdminLog.log(player, "Reseted and refueled vehicle " + vehicle.getUUID());
        }
        return true;
    }


    @Command
    @CommandHelp("Atstato visas nenaudojamas frakcinio darbo transporto priemones � atsiradimo viet�")
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
            player.sendMessage("Atstatytos " + count + " darbo " + faction.getName() + " transporto priemon�s.");
            LtrpPlayer.sendGlobalMessage("Administratorius atstat� visas nenaudojamas darbo " + faction.getName() + "transporto priemones.");
        } else
            player.sendErrorMessage("Frakcijos su tokiu ID n�ra.");
        return true;
    }

    @Command
    @CommandHelp("Priparkuoja transporto priemon�")
    public boolean dtc(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehicle playerVehicle = PlayerVehicle.getByVehicle(player.getVehicle());
        if(playerVehicle == null)
            playerVehicle = PlayerVehicle.getClosest(player, 4f);
        if(playerVehicle == null)
            player.sendErrorMessage("Turite b�ti transporto priemon�je arba prie jos(tinka tik �aid�jams priklausan�ios tr. priemon�s). ");
        else {
            String targetUsername = PlayerController.get().getUsernameByUUID(playerVehicle.getOwnerId());
            player.sendMessage(Color.GREEN, targetUsername + " automobilis " + playerVehicle.getModelName() + " s�kmingai priparkuotas.");
            player.destroy();
            LtrpPlayer target = LtrpPlayer.get(playerVehicle.getOwnerId());
            if(target != null)
                target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " priverstinai priparkavo j�s� " + playerVehicle.getModelName() + ", v�l j� i�parkuoti glaite su /v get.");
            AdminLog.log(player, "Priparkavo tr. priemon� " + playerVehicle.getUUID());
        }
        return true;
    }

    @Command()
    @CommandHelp("Pagydo �aid�j� bei prikelia j� i� komos")
    public boolean aheal(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
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
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pagyd� jus.");
            player.sendMessage(Color.GREEN, "�aid�jas " + target.getName() + "(ID:" + target.getId() + ") pagydytas");
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
                    .buttonCancel("I�eiti")
                    .item("Padangos " + Integer.toBinaryString(dmg.getTires()), i -> {
                        showBinaryInputDialog(player, "Padang� b�sena.",
                                "Dabartin� b�sena: " + Integer.toBinaryString(dmg.getTires()) + "(" + dmg.getTires() + ")",
                                (d, val) -> {
                                    dmg.setTires(val);
                                    player.sendMessage(Color.GREEN, "Padang� b�sena pakeista �" + Integer.toBinaryString(val));
                                }).show();
                    })
                    .item("Panel�s " + Integer.toBinaryString(dmg.getPanels()), i -> {
                        showBinaryInputDialog(player, "Paneli� b�sena.",
                                "Dabartin� b�sena: " + Integer.toBinaryString(dmg.getPanels()) + "(" + dmg.getPanels() + ")",
                                (d, val) -> {
                                    dmg.setPanels(val);
                                    player.sendMessage(Color.GREEN, "Paneli� b�sena pakeista �" + Integer.toBinaryString(val));
                                }).show();
                    })
                    .item("�viesos " + Integer.toBinaryString(dmg.getLights()), i -> {
                        showBinaryInputDialog(player, "�vies� b�sena.",
                                "Dabartin� b�sena: " + Integer.toBinaryString(dmg.getLights()) + "(" + dmg.getLights() + ")",
                                (d, val) -> {
                                    dmg.setLights(val);
                                    player.sendMessage(Color.GREEN, "�vies� b�sena pakeista �" + Integer.toBinaryString(val));
                                }).show();
                    })
                    .item("Durys " + Integer.toBinaryString(dmg.getDoors()), i -> {
                        showBinaryInputDialog(player, "Dur� b�sena.",
                                "Dur� b�sena: " + Integer.toBinaryString(dmg.getDoors()) + "(" + dmg.getDoors() + ")",
                                (d, val) -> {
                                    dmg.setDoors(val);
                                    player.sendMessage(Color.GREEN, "Dur� b�sena pakeista �" + Integer.toBinaryString(val));
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
                        player.sendErrorMessage("Pra�ome �vesti skai�i�, dvejetainiu formatu!");
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
            player.sendErrorMessage("�iuo metu plugin'as yra i�jungtas.");
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
                            player.sendMessage("Loginimas prad�tas.");
                        }
                        i.getCurrentDialog().show();
                    })
                    .item("Mano 'update' event loginimas[" + (loggerPlugin.isUpdateLoggingEnabled(p) ? "+" : "-") + "]", i -> {
                        loggerPlugin.startLogging(p, !loggerPlugin.isUpdateLoggingEnabled(p));
                        player.sendMessage("Asmenin� 'update' tipo loginimo informacija atnaujinta.");
                        i.getCurrentDialog().show();
                    })
                    .buttonOk("Pasirinkti")
                    .buttonCancel("I�eiti")
                    .build()
                    .show();

        }
        return true;
    }


    @Command
    @CommandHelp("I�meta �aid�j� i� darbo")
    public boolean aUnInvite(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else {
            PlayerJobData jobData = JobController.get().getJobData(target);
            if(jobData.getJob() == null) {
                player.sendErrorMessage(target.getName() + " neturi darbo.");
            } else {
                Job job = jobData.getJob();
                target.removeJobWeapons();
                JobController.get().setJob(target, null);
                player.sendMessage(Color.GREEN, String.format("�aid�jas %s(%d) i�mestas i� darbo \"%s\".", target.getName(), target.getId(), job.getName()));
                target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " i�met� jus i� darbo.");
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
    @CommandHelp("Pakei�ia mokest� u� vien� versl�")
    public boolean bizTax(Player p, @CommandParameter(name = "Naujas verslo mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali b�ti ma�esnis u� 0.");
        else {
            LtrpWorld.get().getTaxes().setBusinessTax(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeit� versl� mokest� � " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed business tax to " + tax);
        }
        return true;
    }


    @Command
    @CommandHelp("Pakei�ia mokest� u� vien� gara��")
    public boolean garageTax(Player p, @CommandParameter(name = "Naujas gara�o mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali b�ti ma�esnis u� 0.");
        else {
            LtrpWorld.get().getTaxes().setGarageTax(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeit� gara�� mokest� � " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed garage tax to " + tax);
        }
        return true;
    }

    @Command
    @CommandHelp("Pakei�ia mokest� u� vien� nam�")
    public boolean houseTax(Player p, @CommandParameter(name = "Naujas nam� mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali b�ti ma�esnis u� 0.");
        else {
            LtrpWorld.get().getTaxes().setHouseTax(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeit� nam� mokest� � " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed house tax to " + tax);
        }
        return true;
    }

    @Command
    @CommandHelp("Pakei�ia mokest� u� vien� transporto priemon�")
    public boolean carTax(Player p, @CommandParameter(name = "Naujas nam� mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali b�ti ma�esnis u� 0.");
        else {
            LtrpWorld.get().getTaxes().setVehicleTax(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeit� transporto priemoni� mokest� � " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed vehicle tax to " + tax);
        }
        return true;
    }

    @Command
    @CommandHelp("Pakei�ia PVM mokes�io dyd�")
    public boolean vatTax(Player p, @CommandParameter(name = "Naujas PVM mokestis")int tax) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(tax < 0)
            player.sendErrorMessage("Mokestis negali b�ti ma�esnis u� 0.");
        else {
            LtrpWorld.get().getTaxes().setVAT(tax);
            LtrpPlayer.sendAdminMessage(player.getName() + " pakeit� PVM mokest� � " + tax + Currency.SYMBOL);
            LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            AdminLog.log(player, "Changed VAT tax to " + tax);
        }
        return true;
    }

    @Command
    @CommandHelp("Duoda �aid�jui pinig�")
    public boolean giveMoney(Player p,
                             @CommandParameter(name = "�aid�jo ID/ Dalis vardo")LtrpPlayer target,
                             @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            return false;
        else {
            target.giveMoney(amount);
            LtrpPlayer.sendAdminMessage(String.format("Administratorius %s dav� �aid�jui %s %d%c", player.getName(), target.getName(), amount, Currency.SYMBOL));
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " jums dav� " + amount + Currency.SYMBOL);
            AdminLog.log(player, target, "Dav� �aid�jui " + target.getUUID() + "  " + amount);
        }
        return true;
    }


    @Command
    @CommandHelp("Atidaro �ym�j� \"amenu\"")
    public boolean aMenu(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdminServerManagementDialog.create(player, eventManager)
                .show();
        return true;
    }


    // TODO cmd:ado
    // TODO cmd:ao
}

