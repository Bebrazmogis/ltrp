package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.command.CommandParam;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.FactionListDialog;
import lt.ltrp.dialog.JobListDialog;
import lt.ltrp.item.Item;
import lt.ltrp.job.ContractJob;
import lt.ltrp.job.Faction;
import lt.ltrp.job.JobManager;
import lt.ltrp.player.util.AdminLog;
import lt.ltrp.vehicle.JobVehicle;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.maze.shoebilleventlogger.ShoebillEventLoggerPlugin;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleDamage;
import net.gtaun.util.event.EventManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class AdminCommands {

    private static final Map<String, Integer> adminLevels = new HashMap<>();
    private static final Map<String, Location> teleportLocations = new HashMap<>();

    static {
        adminLevels.put("ahelp", 1);
        adminLevels.put("getoldcar", 1);
        adminLevels.put("rc", 1);
        adminLevels.put("gotoloc", 1);
        adminLevels.put("rjc", 1);
        adminLevels.put("rfc", 2);
        adminLevels.put("gotopos", 2);
        adminLevels.put("gotocar", 2);
        adminLevels.put("aheal", 2);

        adminLevels.put("makefactionmanager", 4);
        adminLevels.put("makeleader", 4);
        adminLevels.put("removeLeader", 4);

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

    private JobManager jobManager;
    private EventManager eventManager;

    public AdminCommands(JobManager jobManager, EventManager eventManager) {
        this.jobManager = jobManager;
        this.eventManager = eventManager;
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
        p.sendMessage(Color.WHITE, "[AdmLvl 1] /mark /lockacc /rc  /setskin  /aproperty /apkills /fon ");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] PERSIKËLIMAS: /gotoloc /goto /gotomark /gotobiz /gotohouse /gotogarage /gotopos");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] TR. PRIEMONËS: /getoldcar /rtc /rfc /rjc /rc");
        if(p.getAdminLevel() >= 2)
            p.sendMessage(Color.WHITE, "[AdmLvl 2] /dtc /gotocar /mute/rac ");
        if(p.getAdminLevel() >= 3)
            p.sendMessage(Color.WHITE, "[AdmLvl 3] /sethp /setarmour /forcelogout /hideadmins /serverguns /checkgun /kickall ");
        if(p.getAdminLevel() >= 4)
        {
            p.sendMessage(Color.WHITE, "[AdmLvl 4] /auninvite /givemoney /giveweapon /amenu /intmenu");
            p.sendMessage(Color.WHITE, "[AdmLvl 4] /makeleader /setstat /setstatcar /gotohouse /gotobiz");
            p.sendMessage(Color.WHITE, "[AdmLvl 4] /makeadmin /makemoderator /cartax /housetax /biztax");
            p.sendMessage(Color.WHITE, "[AdmLvl 4] /makefactinomanager  /giveitem ");
        }
        return true;
    }

    @Command
    @CommandHelp("Atkelia nurodytà transporto priemonæ á jûsø pozicijà")
    public boolean getoldcar(Player player, @CommandParam("Transporto priemonës ID")Vehicle vehicle) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(vehicle == null)
            p.sendMessage(Color.LIGHTRED, "Transporto priemonës su tokiu ID nëra.");
        else {
            vehicle.setLocation(p.getLocation());
        }
        return true;
    }

    @Command
    @CommandHelp("Nukelia jus á vienà ið galimø vietoviø, pvz.: ls, bb")
    public boolean gotoloc(Player player, @CommandParam("Vietovë. Pvz: ls, bb")String pos) {
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
    @CommandHelp("Nukelia jus á pasirinktas koordinates")
    public boolean gotopos(Player player, Float x, Float y, Float z) {
        if(x == null || y == null || z == null) {
            player.sendMessage("Ne. ne taip");
            return false;
        }
        if(x != 0f && y != 0f && z != 0f) {
            player.setLocation(x, y, z);
        }
        return false;
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
        return false;
    }

    @Command
    @CommandHelp("Atstato transporto priemonæ á atsiradimo vietà")
    public boolean rc(Player player, @CommandParam("Transporto priemonës ID")Vehicle veh) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(veh == null) {
            p.sendMessage(Color.LIGHTRED, "Transporto priemonës su tokiu ID nëra.");
        }
        return true;
    }


    @Command
    @CommandHelp("Paskiria nurodytà þaidëjà frakcijø priþiûrëtoju")
    public boolean makefactionmanager(Player player, @CommandParam("Þaidëjo ID/Dalis vardo")LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null) {
            p.sendMessage(Color.LIGHTRED, "Tokio þaidëjo nëra.");
        } else {
            LtrpGamemode.getDao().getPlayerDao().setFactionManager(p2);
            p2.setFactionManager(true);

            LtrpPlayer.sendAdminMessage("Administratorius " + p.getName() + " suteikë þaidëjui " + p2.getName() + " frakcijø priþiûrëtojo rangà.");

            p2.sendMessage(Color.NEWS, "Administratorius " + p.getName() + " paskyrë jus frakcijø priþiûrëtoju.");

            AdminLog.log(p, "Paskyrë þaidëjà " + p2.getName() + " frakcijø priþirëtoju.");
        }
        return true;
    }

    @Command
    @CommandHelp("Paskiria þaidëjà frakcijos lyderiu")
    public boolean makeLeader(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra.");
        } else if(jobManager.isJobLeader(target)) {
            player.sendErrorMessage(target.getName() + " jau yra kitos frakcijos lyderis. Paðalinti já galite su /removeLeader");
        } else {
            FactionListDialog.create(player, eventManager, JobManager.getFactions())
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
                                message += LtrpGamemode.getDao().getPlayerDao().getUsername(leaderId) + "\n";
                            }
                        }

                        MsgboxDialog.create(player, eventManager)
                                .caption("Dëmesio.")
                                .message(message)
                                .buttonOk("Taip")
                                .buttonCancel("Ne")
                                .onClickOk(dd -> {
                                    target.setJob(f);
                                    target.setJobRank(f.getRank(f.getRanks().size()-1));
                                    LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " pridëjo " + target.getName() + " á frakcijos " + f.getName() + " lyderius.");
                                    target.sendMessage(Color.NEWS, "Administratorius " + player.getName() + " paskyrë jus, frakcijos " + f.getName() + " lyderiu!");
                                    f.sendMessage(Color.NEWS, target.getName() + " buvo paskirtas naujuoju frakcijos lyderiu!");
                                    jobManager.addFactionLeader(f, target.getUUID());
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
        int userId = LtrpGamemode.getDao().getPlayerDao().getUserId(username);
        if(userId == LtrpPlayer.INVALID_USER_ID) {
            player.sendErrorMessage("Tokio vartotojo nëra!");
        } else if(!jobManager.isJobLeader(userId)) {
            player.sendErrorMessage("Vartotojas " + username + " nevadovauja jokiai frakcijai.");
        } else {
            Optional<Faction> factionOp = JobManager.getFactions().stream().filter(f -> f.getLeaders().contains(userId)).findFirst();
            if(factionOp.isPresent()) {
                Faction f = factionOp.get();
                MsgboxDialog.create(player, eventManager)
                        .caption("Frakcijos lyderio ðalinimas")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .message("Ar tikrai norite paðalinti " + username + " ið frakcijos " + f.getName() + " lyderiu?" +
                            "\nÐiuo metu frakcija turi " + f.getLeaders().size() + " lyderius.")
                        .onClickOk(d -> {
                            jobManager.removeFactionLeader(f, userId);
                            LtrpPlayer.sendAdminMessage("Administratorius " + player.getName() + " paðalino frakcijos " + f.getName() + " lyderá " + username + " ið pareigø.");
                            f.sendMessage(Color.NEWS, "Lyderis " + username + " buvo paðalintas ið pareigø.");
                            LtrpGamemode.getDao().getPlayerDao().removeJob(userId);
                        })
                        .build().show();
            }
        }

        return true;
    }

    @Command
    @CommandHelp("Suteikia nurodytà daiktà nurodytam þaidëjui")
    public boolean giveitem(Player player, LtrpPlayer p2, String itemClass, String args) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null) {
            p.sendMessage(Color.LIGHTRED, "Tokio þaidëjo nëra.");
        } else {
            List<String> matches = new ArrayList<>();
            Matcher m = Pattern.compile("([^\"][^-]*|\".+?\")\\s*").matcher(args); // strings with spaces can be made like this: "my string"
            while (m.find())
                matches.add(m.group(1).replace("\"", ""));
            Item item = null;
            try {
                item = Item.get(itemClass, matches.toArray(new String[0]));
            } catch (Exception e) {
                p.sendMessage(Color.SIENNA, e.getMessage());
                return false;
            }
            p.getInventory().add(item);
            p.sendMessage(Color.SIENNA, "It worked, wow");
            return true;

        }
        return false;
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
            return true;
        } else
            player.sendErrorMessage("Darbo su tokiu ID nëra.");
        return false;
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
            return true;
        } else
            player.sendErrorMessage("Frakcijos su tokiu ID nëra.");
        return false;
    }

    @Command()
    @CommandHelp("Pagydo þaidëjà bei prikelia já ið komos")
    public boolean aheal(Player p, @CommandParam("Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
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
            AdminLog.log(player, "Healed user " + target.getName() + " uid: " + target.getUUID());
            return true;
        }
        return false;
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
            ListDialog.create(player, LtrpGamemode.get().getEventManager())
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
            return true;
        }
        return false;
    }

    private InputDialog showBinaryInputDialog(LtrpPlayer player, String caption, String message, BinaryInputDiualogClickOkHandler inputHandler) {
        return InputDialog.create(player, LtrpGamemode.get().getEventManager())
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
}

