package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.AdminLog;
import lt.ltrp.command.CommandParam;
import lt.ltrp.data.Color;
import lt.ltrp.item.Item;
import lt.ltrp.job.ContractJob;
import lt.ltrp.job.Faction;
import lt.ltrp.vehicle.JobVehicle;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        adminLevels.put("giveitem", 6);
        adminLevels.put("fly", 6);

        teleportLocations.put("pc", new Location(2292.1936f, 26.7535f, 25.9974f, 0, 0));
        teleportLocations.put("ls", new Location(1540.1237f, -1675.2844f, 13.5500f, 0, 0));
        teleportLocations.put("mg", new Location(1313.8589f, 314.4103f, 19.4098f, 0, 0));
        teleportLocations.put("bb", new Location(230.9343f, -146.9140f, 1.4297f, 0, 0));
        teleportLocations.put("dl", new Location(641.5609f, -559.9846f, 16.0626f, 0, 0));
        teleportLocations.put("fc", new Location(-183.3534f, 1034.6022f, 19.7422f, 0, 0));
        teleportLocations.put("lb", new Location(-837.1216f, 1537.0032f, 22.5471f, 0, 0));
    }


    @BeforeCheck
    public boolean beforeCheck(LtrpPlayer p, String cmd, String params) {
        cmd = cmd.toLowerCase();
        LtrpPlayer player = p;
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
        p.sendMessage(Color.WHITE, "[AdmLvl 1] /mark /lockacc /rc  /setskin  /aproperty /apkills /fon ");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] PERSIK�LIMAS: /gotoloc /goto /gotomark /gotobiz /gotohouse /gotogarage /gotopos");
        p.sendMessage(Color.WHITE, "[AdmLvl 1] TR. PRIEMON�S: /getoldcar /rtc /rfc /rjc /rc");
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
    @CommandHelp("Atkelia nurodyt� transporto priemon� � j�s� pozicij�")
    public boolean getoldcar(Player player, @CommandParam("Transporto priemon�s ID")Vehicle vehicle) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(vehicle == null)
            p.sendMessage(Color.LIGHTRED, "Transporto priemon�s su tokiu ID n�ra.");
        else {
            vehicle.setLocation(p.getLocation());
        }
        return true;
    }

    @Command
    @CommandHelp("Nukelia jus � vien� i� galim� vietovi�, pvz.: ls, bb")
    public boolean gotoloc(Player player, @CommandParam("Vietov�. Pvz: ls, bb")String pos) {
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
    @CommandHelp("Nukelia jus � pasirinktas koordinates")
    public boolean gotopos(LtrpPlayer player, Float x, Float y, Float z) {
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
    @CommandHelp("Nukelia jus prie pasirinktos transporot priemon�s")
    public boolean gotoCar(LtrpPlayer player, LtrpVehicle vehicle) {
        if(vehicle == null) {
            player.sendErrorMessage("Tokios transporto priemon�s n�ra!");
        } else {
            player.setLocation(vehicle.getLocation());
            player.sendMessage(Color.NEWS, "Nusik�l�te prie " + vehicle.getModelName() + "(ID:" + vehicle.getId() + ")");
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Atstato transporto priemon� � atsiradimo viet�")
    public boolean rc(Player player, @CommandParam("Transporto priemon�s ID")Vehicle veh) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(veh == null) {
            p.sendMessage(Color.LIGHTRED, "Transporto priemon�s su tokiu ID n�ra.");
        }
        return true;
    }


    @Command
    @CommandHelp("Paskiria nurodyt� �aid�j� frakcij� pri�i�r�toju")
    public boolean makefactionmanager(Player player, @CommandParam("�aid�jo ID/Dalis vardo")LtrpPlayer p2) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(p2 == null) {
            p.sendMessage(Color.LIGHTRED, "Tokio �aid�jo n�ra.");
        } else {
            LtrpGamemode.getDao().getPlayerDao().setFactionManager(p2);
            p2.setFactionManager(true);

            LtrpPlayer.sendAdminMessage("Administratorius " + p.getName() + " suteik� �aid�jui " + p2.getName() + " frakcij� pri�i�r�tojo rang�.");

            p2.sendMessage(Color.NEWS, "Administratorius " + p.getName() + " paskyr� jus frakcij� pri�i�r�toju.");

            AdminLog.log(p, "Paskyr� �aid�j� " + p2.getName() + " frakcij� pri�ir�toju.");
        }
        return true;
    }

    @Command
    @CommandHelp("Suteikia nurodyt� daikt� nurodytam �aid�jui")
    public boolean giveitem(LtrpPlayer p, LtrpPlayer p2, String itemClass, String args) {
        if(p2 == null) {
            p.sendMessage(Color.LIGHTRED, "Tokio �aid�jo n�ra.");
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
    @CommandHelp("Atstato visas nenaudojamas kontraktinio darbo transporto priemones � atsiradimo viet�")
    public boolean rjc(LtrpPlayer player, ContractJob job) {
        if(job != null) {
            int count = 0;
            for(JobVehicle jobVehicle : job.getVehicles().values()) {
                if(!jobVehicle.isUsed()) {
                    jobVehicle.respawn();
                    count++;
                }
            }
            player.sendMessage("Atstatytos " + count + " darbo " + job.getName() + " transporto priemon�s.");
            LtrpPlayer.sendGlobalMessage("Administratorius atstat� visas nenaudojamas darbo " + job.getName() + "transporto priemones.");
            return true;
        } else
            player.sendErrorMessage("Darbo su tokiu ID n�ra.");
        return false;
    }


    @Command
    @CommandHelp("Atstato visas nenaudojamas frakcinio darbo transporto priemones � atsiradimo viet�")
    public boolean rfc(LtrpPlayer player, Faction faction) {
        if(faction != null) {
            int count = 0;
            for(JobVehicle jobVehicle : faction.getVehicles().values()) {
                if(!jobVehicle.isUsed()) {
                    jobVehicle.respawn();
                    count++;
                }
            }
            player.sendMessage("Atstatytos " + count + " darbo " + faction.getName() + " transporto priemon�s.");
            LtrpPlayer.sendGlobalMessage("Administratorius atstat� visas nenaudojamas darbo " + faction.getName() + "transporto priemones.");
            return true;
        } else
            player.sendErrorMessage("Frakcijos su tokiu ID n�ra.");
        return false;
    }

    @Command()
    @CommandHelp("Pagydo �aid�j� bei prikelia j� i� komos")
    public boolean aheal(LtrpPlayer player, @CommandParam("�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else {
            target.setHealth(100f);
            if(target.isInAnyVehicle()) {
                target.getVehicle().repair();
            }
            if(target.isInComa()) {
                target.setInComa(false);
                target.getInfoBox().setDeathTime(null);
            }
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pagyd� jus.");
            player.sendMessage(Color.GREEN, "�aid�jas " + target.getName() + "(ID:" + target.getId() + ") pagydytas");
            AdminLog.log(player, "Healed user " + target.getName() + " uid: " + target.getUserId());
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Dont")
    public boolean fly(LtrpPlayer player) {
        if(player.getSpecialAction() == SpecialAction.NONE) {
            player.setSpecialAction(SpecialAction.USE_JETPACK);
        } else
            player.setSpecialAction(SpecialAction.NONE);
        return true;
    }




}

