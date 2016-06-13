package lt.ltrp.command;

import lt.ltrp.constant.TalkStyle;
import lt.ltrp.constant.WalkStyle;
import lt.ltrp.data.Color;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.object.Player;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.12.
 */
public class PlayerAnimationCommands {

    private static final Collection<String> ON_FOOT_COMMANDS = new ArrayList<>();
    private static final Collection<String> IN_VEHICLE_COMMANDS = new ArrayList<>();

    static {
        ON_FOOT_COMMANDS.add("fall");
        ON_FOOT_COMMANDS.add("injured");
        ON_FOOT_COMMANDS.add("push");
        ON_FOOT_COMMANDS.add("handsup");
        ON_FOOT_COMMANDS.add("kiss");
        ON_FOOT_COMMANDS.add("slapass");
        ON_FOOT_COMMANDS.add("bomb");
        ON_FOOT_COMMANDS.add("drunk");
        ON_FOOT_COMMANDS.add("laugh");
        ON_FOOT_COMMANDS.add("facepalm");
        ON_FOOT_COMMANDS.add("basketball");
        ON_FOOT_COMMANDS.add("medic");
        ON_FOOT_COMMANDS.add("spraycan");
        ON_FOOT_COMMANDS.add("robman");
        ON_FOOT_COMMANDS.add("taichi");
        ON_FOOT_COMMANDS.add("lookout");
        ON_FOOT_COMMANDS.add("sit");
        ON_FOOT_COMMANDS.add("lay");
        ON_FOOT_COMMANDS.add("crossarms");
        ON_FOOT_COMMANDS.add("deal");
        ON_FOOT_COMMANDS.add("crack");
        ON_FOOT_COMMANDS.add("smoke");
        ON_FOOT_COMMANDS.add("bar");
        ON_FOOT_COMMANDS.add("hike");
        ON_FOOT_COMMANDS.add("dance");
        ON_FOOT_COMMANDS.add("fuck");
        ON_FOOT_COMMANDS.add("lean");
        ON_FOOT_COMMANDS.add("walk");
        ON_FOOT_COMMANDS.add("rap");
        ON_FOOT_COMMANDS.add("sex");
        ON_FOOT_COMMANDS.add("tires");
        ON_FOOT_COMMANDS.add("box");
        ON_FOOT_COMMANDS.add("scratch");
        ON_FOOT_COMMANDS.add("hide");
        ON_FOOT_COMMANDS.add("vomit");
        ON_FOOT_COMMANDS.add("eats");
        ON_FOOT_COMMANDS.add("cop");
        ON_FOOT_COMMANDS.add("stance");
        ON_FOOT_COMMANDS.add("wave");
        ON_FOOT_COMMANDS.add("skick");
        ON_FOOT_COMMANDS.add("aload");
        ON_FOOT_COMMANDS.add("flag");
        ON_FOOT_COMMANDS.add("giver");
        ON_FOOT_COMMANDS.add("look");
        ON_FOOT_COMMANDS.add("show");
        ON_FOOT_COMMANDS.add("shout");
        ON_FOOT_COMMANDS.add("endchat");
        ON_FOOT_COMMANDS.add("face");
        ON_FOOT_COMMANDS.add("gsign");
        ON_FOOT_COMMANDS.add("dj");
        ON_FOOT_COMMANDS.add("loudtalk");
        ON_FOOT_COMMANDS.add("rem");
        ON_FOOT_COMMANDS.add("lift");
        ON_FOOT_COMMANDS.add("place");
        ON_FOOT_COMMANDS.add("yes");
        ON_FOOT_COMMANDS.add("no");
        ON_FOOT_COMMANDS.add("bag");
        ON_FOOT_COMMANDS.add("wank");
        ON_FOOT_COMMANDS.add("pee");
        ON_FOOT_COMMANDS.add("riot");
        ON_FOOT_COMMANDS.add("knife");
        ON_FOOT_COMMANDS.add("bat");
        ON_FOOT_COMMANDS.add("lebelly");
        ON_FOOT_COMMANDS.add("leface");
        ON_FOOT_COMMANDS.add("ahouse");
        ON_FOOT_COMMANDS.add("talk");
        ON_FOOT_COMMANDS.add("benchpress");
        ON_FOOT_COMMANDS.add("camera");
        ON_FOOT_COMMANDS.add("carry");

        IN_VEHICLE_COMMANDS.add("caract");
    }


    @BeforeCheck
    public boolean bC(Player p, String cmd, String params) {
        cmd = cmd.toLowerCase();
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerState state = player.getState();
        if(ON_FOOT_COMMANDS.contains(cmd) && state != PlayerState.ONFOOT) {
            player.sendErrorMessage("Ðios animacijos negalite naudoti bûdamas transporto priemonëje!");
            return false;
        }
        if(IN_VEHICLE_COMMANDS.contains(cmd) && state != PlayerState.DRIVER && state != PlayerState.PASSENGER) {
            player.sendErrorMessage("Ðià animacijà galite naudoti tik bûdami transporto priemonëje!");
            return false;
        }
        player.sendInfoText("~w~~h~Spauskite ~r~SPACE~w~~h~ kad sutabdytumet animacija.", 3000);
        return true;
    }

    @Command
    public boolean anims(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.sendMessage(Color.GREEN, "________________________ Animacijos ________________________");
        player.sendMessage(Color.WHITE, "/fall /injured /push /handsup /kiss /slapass /bomb /drunk /laugh /facepalm");
        player.sendMessage(Color.LIGHTGREY, "/basketball /medic /spraycan /robman /taichi /lookout /sit /lay /crossarms");
        player.sendMessage(Color.WHITE, "/deal /crack /smoke /bar /hike /dance /fuck /lean /walk /rap /caract /sex");
        player.sendMessage(Color.LIGHTGREY, "/tired /box /scratch /hide /vomit /eats /cop /stance /wave /skick /aload");
        player.sendMessage(Color.WHITE, "/flag /giver /look /show /shout /endchat /face /gsign /dj /loudtalk");
        player.sendMessage(Color.LIGHTGREY, "/rem /lift /place /yes /no /bag /wank /pee /riot /knife /bat");
        player.sendMessage(Color.WHITE, "/lebelly /leface /ahouse /talk /benchpress /camera /carry");
        player.sendMessage(Color.GREEN, "____________________________________________________________");
        return true;
    }

    @Command
    @CommandHelp("PAkeièia jûsø vaikðèiojimo stiliø")
    public boolean walkStyle(Player p, @CommandParameter(name = "Numeris")int number) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(number < 0 || number > WalkStyle.values().length)
            player.sendErrorMessage("Galimi vaikðèiojimo stiliai 0 - " + WalkStyle.values().length);
        else if(number == 0) {
            player.setWalkStyle(null);
            player.sendMessage(Color.NEWS, "Vaikðèiojimo stilius paðalintas");
        }
        else {
            player.setWalkStyle(WalkStyle.values()[number-1]);
            player.sendMessage(Color.NEWS, "Vaikðèiojimo stilius sëkmingai pakeistas!");
        }
        return true;
    }

    @Command
    @CommandHelp("Pakeièia jûsø kalbëjimo stiliø")
    public boolean talkStyle(Player p, @CommandParameter(name = "Numeris")int number) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(number < 0|| number > TalkStyle.values().length)
            player.sendErrorMessage("Galimi kalbëjimo stiliai 0 - " + TalkStyle.values().length);
        else if(number == 0) {
            player.setTalkStyle(null);
            player.sendMessage(Color.NEWS, "Kalbëjimo stilius paðalintas");
        }
        else {
            player.setTalkStyle(TalkStyle.values()[number-1]);
            player.sendMessage(Color.NEWS, "Kalbëjimo stilius sëkmingai pakeistas!");
        }
        return true;
    }


    @Command
    public boolean fall(Player p, @CommandParameter(name = "1-3")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("PED", "KO_skid_front", false, false, true, true);
                break;
            case 2:
                player.applyLoopAnimation("PED", "FLOOR_hit_f", false, false, true, true);
                break;
            case 3:
                player.applyLoopAnimation("PED", "KO_skid_back", false, false, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean injured(Player p, @CommandParameter(name = "1-3")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("SWEET", "Sweet_injuredloop", false, false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("WUZI", "CS_DEAD_GUY", false, false, true, true);
                break;
            case 3:
                player.applyLoopAnimation("PED", "gas_cwr", true, true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean push(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("GANGS", "SHAKE_CARA", true, true, false, 1490, true);
                break;
            case 2:
                player.applyAnimation("GANGS", "SHAKE_CARSH", true, true, false, 1300, true);
                break;
            default:
                return false;
        }
        return true;
    }


    @Command
    public boolean handsUp(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.setSpecialAction(SpecialAction.HANDSUP);
        return true;
    }

    @Command
    public boolean kiss(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("KISSING", "PLAYA_KISS_02", true, true, false, 5850, true);
                break;
            case 2:
                player.applyAnimation("BD_FIRE", "GRLFRD_KISS_03", true, true, false, 3500, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean slapAss(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("SWEET", "SWEET_ASS_SLAP", true, true, false, 1900, true);
                break;
            case 2:
                player.applyAnimation("MISC", "BITCHSLAP", true, true, false, 1000, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean bomb(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("BOMBER", "BOM_PLANT_LOOP", false, false, false, true);
                break;
            case 2:
                player.applyAnimation("MISC", "PLUNGER_01", false, false, false, 3000, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean drunk(Player p, @CommandParameter(name = "1-3")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("PED", "WALK_DRUNK", true, true, false, true);
                break;
            case 2:
                player.applyLoopAnimation("PAULNMAC", "PNM_LOOP_A", true, true, false, true);
                break;
            case 3:
                player.applyLoopAnimation("PAULNMAC", "PNM_LOOP_B", true, true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean laugh(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("RAPPING", "LAUGH_01", true, true, false, 5400, true);
        return true;
    }

    @Command
    public boolean facePalm(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("MISC", "PLYR_SHKHEAD", true, true, false, 1700, true);
        return true;
    }

    @Command
    public boolean basketball(Player p, @CommandParameter(name = "1-5")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("BSKTBALL", "BBALL_IDLELOOP", true, true, false, true);
                break;
            case 2:
                player.applyAnimation("BSKTBALL", "BBALL_JUMP_SHOT", true, true, false, 1400, true);
                break;
            case 3:
                player.applyAnimation("BSKTBALL", "BBALL_PICKUP", true, true, false, 1600, true);
                break;
            case 4:
                player.applyLoopAnimation("BSKTBALL", "BBALL_RUN", true, true, false, true);
                break;
            case 5:
                player.applyLoopAnimation("BSKTBALL", "BBALL_DEF_LOOP", true, true, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean medic(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("MEDIC", "CPR", true, true, false, 8300, true);
        return true;
    }

    @Command
    public boolean sprayCan(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("SPRAYCAN", "SPRAYCAN_FULL", true, true, false, 4400, true);
                break;
            case 2:
                player.applyLoopAnimation("PAULNMAC", "SPRAYCAN_FIRE", true, true, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean robMan(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("SHOP", "ROB_LOOP_THREAT", false, false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("PED", "GANG_GUNSTAND", false, false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean taiChi(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("PARK", "TAI_CHI_LOOP", false, false, false, true);
        return true;
    }


    @Command
    public boolean lookout(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("FOOD", "EAT_VOMIT_SK", false, false, false, 8000, true);
                break;
            case 2:
                player.applyAnimation("PED", "HANDSCOWER", false, false, false, 1750, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean sit(Player p, @CommandParameter(name = "1-7")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("PED", "SEAT_DOWN", true, true, true, 0, true);
                break;
            case 2:
                player.applyLoopAnimation("MISC", "SEAT_LR", false, false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("MISC", "SEAT_TALK_01", false, false, false, true);
                break;
            case 4:
                player.applyLoopAnimation("MISC", "SEAT_TALK_02", false, false, false, true);
                break;
            case 5:
                player.applyLoopAnimation("PED", "SEAT_IDLE", false, false, false, true);
                break;
            case 6:
                player.applyLoopAnimation("CRACK", "CRCKIDLE1", false, false, false, true);
                break;
            case 7:
                player.applyLoopAnimation("CRACK", "CRCKIDLE2", false, false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean lay(Player p, @CommandParameter(name = "1-10")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("BEACH", "BATHER", false, false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("BEACH", "PARKSIT_W_LOOP", false, false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("BEACH", "PARKSIT_M_LOOP", false, false, false, true);
                break;
            case 4:
                player.applyLoopAnimation("BEACH", "LAY_BAC_LOOP", false, false, false, true);
                break;
            case 5:
                player.applyLoopAnimation("BEACH", "SITNWAIT_LOOP_W", false, false, false, true);
                break;
            case 6:
                player.applyAnimation("SUNBATHE", "LAY_BAC_IN", false, false, true, 0, true);
                break;
            case 7:
                player.applyAnimation("SUNBATHE", "BATHERDOWN", false, false, true, 0, true);
                break;
            case 8:
                player.applyAnimation("SUNBATHE", "PARKSIT_M_IN", false, false, true, 0, true);
                break;
            case 9:
                player.applyLoopAnimation("CAR", "FIXN_CAR_LOOP", false, false, false, true);
                break;
            case 10:
                player.applyLoopAnimation("CRACK", "CRCKIDLE4", false, false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean crossArms(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("COP_AMBIENT", "COPLOOK_LOOP", false, false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("OTB", "WTCHRACE_LOOP", false, false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }


    @Command
    public boolean deal(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("DEALER", "DEALER_DEAL", false, false, false, true);
        return true;
    }


    @Command
    public boolean crack(Player p, @CommandParameter(name = "1-6")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("CRACK", "CRCKDETH1", true, true, true, 0, true);
                break;
            case 2:
                player.applyLoopAnimation("CRACK", "CRCKDETH2", false, false, false, true);
                break;
            case 3:
                player.applyAnimation("CRACK", "CRCKDETH3", false, false, true, 0, true);
                break;
            case 4:
                player.applyLoopAnimation("CRACK", "CRCKIDLE2", false, false, false, true);
                break;
            case 5:
                player.applyLoopAnimation("CRACK", "CRCKIDLE3", false, false, false, true);
                break;
            case 6:
                player.applyLoopAnimation("CRACK", "CRCKIDLE4", false, false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean smoke(Player p, @CommandParameter(name = "1-6")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("SMOKING", "M_SMKLEAN_LOOP", false, false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("CRACK", "F_SMKLEAN_LOOP", false, false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("SMOKING", "M_SMKSTND_LOOP", false, false, false, true);
                break;
            case 4:
                player.applyAnimation("SMOKING", "M_SMK_OUT", false, false, false, 6900, true);
                break;
            case 5:
                player.applyAnimation("SMOKING", "M_SMK_IN", false, false, false, 6500, true);
                break;
            case 6:
                player.applyAnimation("SMOKING", "M_SMK_TAP", false, false, false, 3000, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean bar(Player p, @CommandParameter(name = "1-12")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("BAR", "BARCUSTOM_GET", false, false, true, 0, true);
                break;
            case 2:
                player.applyLoopAnimation("BAR", "BARCUSTOM_LOOP", false, false, false, true);
                break;
            case 3:
                player.applyAnimation("BAR", "BARCUSTOM_ORDER", false, false, false, 3500, true);
                break;
            case 4:
                player.applyLoopAnimation("BAR", "BARMAN_IDLE", false, false, false, true);
                break;
            case 5:
                player.applyAnimation("BAR", "BARSERVE_BOTTLE", false, false, false, 3000, true);
                break;
            case 6:
                player.applyAnimation("BAR", "BARSERVE_GIVE", false, false, false, 2500, true);
                break;
            case 7:
                player.applyAnimation("BAR", "BARSERVE_GLASS", false, false, false, 3500, true);
                break;
            case 8:
                player.applyAnimation("BAR", "BARSERVE_IN", false, false, true, 0, true);
                break;
            case 9:
                player.applyLoopAnimation("BAR", "BARSERVE_LOOP", false, false, true);
                break;
            case 10:
                player.applyAnimation("BAR", "BARSERVE_ORDER", false, false, false, 3500, true);
                break;
            case 11:
                player.applyAnimation("BAR", "DNK_STNDF_LOOP", false, false, true, 0, true);
                break;
            case 12:
                player.applyAnimation("BAR", "DNK_STNDM_LOOP", false, false, true, 0, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean hike(Player p, @CommandParameter(name = "1-3")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("MISC", "HIKER_POSE", false, false, true, 0, true);
                break;
            case 2:
                player.applyAnimation("MISC", "HIKER_POSE_L", false, false, true, 0, true);
                break;
            case 3:
                player.applyAnimation("PED", "idle_taxi", false, false, true, 0, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean dance(Player p, @CommandParameter(name = "1-13")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.setSpecialAction(SpecialAction.DANCE1);
                break;
            case 2:
                player.setSpecialAction(SpecialAction.DANCE2);
                break;
            case 3:
                player.setSpecialAction(SpecialAction.DANCE3);
                break;
            case 4:
                player.setSpecialAction(SpecialAction.DANCE4);
                break;
            case 5:
                player.applyLoopAnimation("DANCING", "DANCE_LOOP", false, false, true);
                break;
            case 6:
                player.applyLoopAnimation("DANCING", "DAN_DOWN_A", false, false, true);
                break;
            case 7:
                player.applyLoopAnimation("DANCING", "DAN_LEFT_A", false, false, true);
                break;
            case 8:
                player.applyLoopAnimation("DANCING", "DAN_LOOP_A", false, false, true);
                break;
            case 9:
                player.applyLoopAnimation("DANCING", "DAN_RIGHT_A", false, false, true);
                break;
            case 10:
                player.applyLoopAnimation("DANCING", "DAN_UP_A", false, false, true);
                break;
            case 11:
                player.applyLoopAnimation("DANCING", "DANCE_M_A", false, false, true);
                break;
            case 12:
                player.applyLoopAnimation("DANCING", "DANCE_M_B", false, false, true);
                break;
            case 13:
                player.applyLoopAnimation("DANCING", "DANCE_M_C", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean fuck(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("PED", "FUCKU", false, false, false, 1200, true);
                break;
            case 2:
                player.applyAnimation("RIOT", "RIOT_FUKU", false, false, false, 1000, true);
                break;
            default:
                return false;
        }
        return true;
    }



    @Command
    public boolean lean(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("GANGS", "LEANIDLE", false, false, true, 0, true);
                break;
            case 2:
                player.applyLoopAnimation("MISC", "PLYRLEAN_LOOP", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }


    @Command
    public boolean walk(Player p, @CommandParameter(name = "1-14")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("PED", "WALK_GANG1", true, true, true);
                break;
            case 2:
                player.applyLoopAnimation("DANCING", "WALK_GANG2", true, true, true);
                break;
            case 3:
                player.applyLoopAnimation("FAT", "FATWALK", true, true, true);
                break;
            case 4:
                player.applyLoopAnimation("WUZI", "CS_WUZI_PT1", true, true, true);
                break;
            case 5:
                player.applyLoopAnimation("WUZI", "WUZI_WALK", true, true, true);
                break;
            case 6:
                player.applyLoopAnimation("POOL", "POOL_WALK", true, true, true);
                break;
            case 7:
                player.applyLoopAnimation("PED", "WALK_PLAYER", true, true, true);
                break;
            case 8:
                player.applyLoopAnimation("PED", "WALK_OLD", true, true, true);
                break;
            case 9:
                player.applyLoopAnimation("PED", "WALK_FATOLD", true, true, true);
                break;
            case 10:
                player.applyLoopAnimation("PED", "WOMAN_WALKFATOLD", true, true, true);
                break;
            case 11:
                player.applyLoopAnimation("PED", "WOMAN_WALKNORM", true, true, true);
                break;
            case 12:
                player.applyLoopAnimation("PED", "WOMAN_WALKOLD", true, true, true);
                break;
            case 13:
                player.applyLoopAnimation("PED", "WOMAN_WALKPRO", true, true, true);
                break;
            case 14:
                player.applyLoopAnimation("PED", "WOMAN_WALKSHOP", true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean rap(Player p, @CommandParameter(name = "1-11")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("RAPPING", "RAP_A_LOOP", true, true, true);
                break;
            case 2:
                player.applyLoopAnimation("RAPPING", "RAP_C_LOOP", true, true, true);
                break;
            case 3:
                player.applyLoopAnimation("GANGS", "PRTIAL_GNGTLKA", true, true, true);
                break;
            case 4:
                player.applyLoopAnimation("GANGS", "PRTIAL_GNGTLKB", true, true, true);
                break;
            case 5:
                player.applyLoopAnimation("GANGS", "PRTIAL_GNGTLKC", true, true, true);
                break;
            case 6:
                player.applyLoopAnimation("GANGS", "PRTIAL_GNGTLKD", true, true, true);
                break;
            case 7:
                player.applyLoopAnimation("GANGS", "PRTIAL_GNGTLKE", true, true, true);
                break;
            case 8:
                player.applyLoopAnimation("GANGS", "PRTIAL_GNGTLKF", true, true, true);
                break;
            case 9:
                player.applyLoopAnimation("GANGS", "PRTIAL_GNGTLKG", true, true, true);
                break;
            case 10:
                player.applyLoopAnimation("GANGS", "PRTIAL_GNGTLKH", true, true, true);
                break;
            case 11:
                player.applyLoopAnimation("RAPPING", "RAP_B_LOOP", true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean carAct(Player p, @CommandParameter(name = "1-7")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        int seat = player.getVehicleSeat();
        switch(type) {
            case 1:
                if(seat == 0 || seat == 2)
                    player.applyLoopAnimation("PED", "TAP_HAND", true, true, true);
                else
                    player.applyLoopAnimation("PED", "TAP_HANDP", true, true, true);
                break;
            case 2:
                player.applyLoopAnimation("CAR", "SIT_RELAXED", false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("CAR", "TAP_HAND", false, false, true);
                break;
            case 4:
                player.applyAnimation("CAR_CHAT", "CARFONE_IN", false, false, true, 0, true);
                break;
            case 5:
                player.applyLoopAnimation("CAR_CHAT", "CARFONE_LOOPA", false, false, true);
                break;
            case 6:
                player.applyLoopAnimation("CAR_CHAT", "CARFONE_LOOPB", false, false, true);
                break;
            case 7:
                if(seat == 0 || seat == 2)
                    player.applyLoopAnimation("DRIVEBYS", "GANG_DRIVEBYLHS", false, false, true);
                else
                    player.applyLoopAnimation("DRIVEBYS", "GANG_DRIVEBYRHS", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean sex(Player p, @CommandParameter(name = "1-7")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        int seat = player.getVehicleSeat();
        switch(type) {
            case 1:
                player.applyLoopAnimation("BLOWJOBZ", "BJ_COUCH_START_W", true, true, true);
                break;
            case 2:
                player.applyLoopAnimation("BLOWJOBZ", "BJ_COUCH_LOOP_W", false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("BLOWJOBZ", "BJ_COUCH_END_W", false, false, true);
                break;
            case 4:
                player.applyLoopAnimation("BLOWJOBZ", "BJ_COUCH_START_P", false, false, true);
                break;
            case 5:
                player.applyLoopAnimation("BLOWJOBZ", "BJ_COUCH_LOOP_P", false, false, true);
                break;
            case 6:
                player.applyLoopAnimation("BLOWJOBZ", "BJ_COUCH_END_P", false, false, true);
                break;
            case 7:
                player.applyLoopAnimation("BLOWJOBZ", "BJ_STAND_START_W", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean tired(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("PED", "IDLE_TIRED", false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("FAT", "IDLE_TIRED", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }


    @Command
    public boolean box(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("GYMNASIUM", "GYMSHADOWBOX", false, false, true);
        return true;
    }


    @Command
    public boolean scratch(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("MISC", "SCRATCHBALLS_01", false, false, true);
        return true;
    }


    @Command
    public boolean hide(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("PED", "COWER", false, false, true);
                break;
            case 2:
                player.applyAnimation("ON_LOOKERS", "PANIC_HIDE", false, false, true, 0, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean vomit(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("FOOD", "EAT_VOMIT_P", false, false, false, 8000, true);
        return true;
    }

    @Command
    public boolean eats(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("FOOD", "EAT_Burger", false, false, false, 4800, true);
        return true;
    }



    @Command
    public boolean cop(Player p, @CommandParameter(name = "1-19")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("SWORD", "SWORD_IDLE", true, true, true);
                break;
            case 2:
                player.applyLoopAnimation("POLICE", "COP_TRAF_AWAY", true, true, true);
                break;
            case 3:
                player.applyLoopAnimation("POLICE", "COP_TRAF_COME", true, true, true);
                break;
            case 4:
                player.applyLoopAnimation("POLICE", "COP_TRAF_LEFT", true, true, true);
                break;
            case 5:
                player.applyLoopAnimation("POLICE", "COP_TRAF_STOP", true, true, true);
                break;
            case 6:
                player.applyLoopAnimation("POLICE", "COP_MOVE_FWD", true, true, true);
                break;
            case 7:
                player.applyLoopAnimation("PED", "ARRESTGUN", true, true, true);
                break;
            case 8:
                player.applyAnimation("COP_AMBIENT", "COPBROWSE_IN", true, true, true, 0, true);
                break;
            case 9:
                player.applyLoopAnimation("COP_AMBIENT", "COPBROWSE_LOOP", true, true, true);
                break;
            case 10:
                player.applyLoopAnimation("COP_AMBIENT", "COPBROWSE_NOD", true, true, true);
                break;
            case 11:
                player.applyAnimation("COP_AMBIENT", "COPBROWSE_OUT", true, true, true, 0, true);
                break;
            case 12:
                player.applyLoopAnimation("COP_AMBIENT", "COPBROWSE_SHAKE", true, true, true);
                break;
            case 13:
                player.applyAnimation("COP_AMBIENT", "COPLOOK_IN", true, true, true, 0, true);
                break;
            case 14:
                player.applyLoopAnimation("COP_AMBIENT", "COPLOOK_LOOP", true, true, true);
                break;
            case 15:
                player.applyLoopAnimation("COP_AMBIENT", "COPLOOK_NOD", true, true, true);
                break;
            case 16:
                player.applyAnimation("COP_AMBIENT", "COPLOOK_OUT", true, true, true, 0, true);
                break;
            case 17:
                player.applyLoopAnimation("COP_AMBIENT", "COPLOOK_SHAKE", true, true, true);
                break;
            case 18:
                player.applyLoopAnimation("COP_AMBIENT", "COPLOOK_TWINK", true, true, true);
                break;
            case 19:
                player.applyLoopAnimation("COP_AMBIENT", "COPLOOK_WATCH", true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean stance(Player p, @CommandParameter(name = "1-16")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("DEALER", "DEALER_IDLE", true, true, true);
                break;
            case 2:
                player.applyLoopAnimation("PED", "WOMAN_IDLESTANCE", true, true, true);
                break;
            case 3:
                player.applyLoopAnimation("PED", "CAR_HOOKERTALK", false, false, true);
                break;
            case 4:
                player.applyLoopAnimation("FAT", "FATIDLE", true, true, true);
                break;
            case 5:
                player.applyLoopAnimation("WUZI", "WUZI_STAND_LOOP", true, true, true);
                break;
            case 6:
                player.applyLoopAnimation("GRAVEYARD", "MRNF_LOOP", true, true, true);
                break;
            case 7:
                player.applyLoopAnimation("GRAVEYARD", "MRNM_LOOP", true, true, true);
                break;
            case 8:
                player.applyLoopAnimation("GRAVEYARD", "PRST_LOOP", true, true, true);
                break;
            case 9:
                player.applyLoopAnimation("PED", "IDLESTANCE_FAT", true, true, true);
                break;
            case 10:
                player.applyLoopAnimation("PED", "IDLESTANCE_OLD", true, true, true);
                break;
            case 11:
                player.applyLoopAnimation("PED", "TURN_L", true, true, true);
                break;
            case 12:
                player.applyLoopAnimation("DEALER", "DEALER_IDLE_01", true, true, true);
                break;
            case 13:
                player.applyLoopAnimation("DEALER", "DEALER_IDLE_02", true, true, true);
                break;
            case 14:
                player.applyLoopAnimation("DEALER", "DEALER_IDLE_03", true, true, true);
                break;
            case 15:
                player.applyLoopAnimation("DEALER", "DRUGS_BUY", true, true, true);
                break;
            case 16:
                player.applyLoopAnimation("DEALER", "SHOP_PAY", true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }


    @Command
       public boolean wave(Player p, @CommandParameter(name = "1-5")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("ON_LOOKERS", "WAVE_LOOK", true, true, true);
                break;
            case 2:
                player.applyAnimation("BD_Fire", "BD_GF_WAVE", true, true, false, 5200, true);
                break;
            case 3:
                player.applyLoopAnimation("RIOT", "RIOT_CHANT", true, true, true);
                break;
            case 4:
                player.applyLoopAnimation("WUZI", "WUZI_FOLLOW", true, true, true);
                break;
            case 5:
                player.applyLoopAnimation("KISSING", "GFWAVE2", true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean sKick(Player p, @CommandParameter(name = "1-4")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("POLICE", "Door_Kick", true, true, false, 1200, true);
                break;
            case 2:
                player.applyAnimation("FIGHT_D", "FIGHTD_2", true, true, false, 1900, true);
                break;
            case 3:
                player.applyAnimation("FIGHT_C", "FIGHTC_M", true, true, false, 700, true);
                break;
            case 4:
                player.applyAnimation("FIGHT_D", "FIGHTD_G", true, true, false, 800, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean aLoad(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("COLT45", "COLT45_RELOAD", false, false, false, 1000, true);
                break;
            case 2:
                player.applyAnimation("UZI", "UZI_RELOAD", false, false, false, 1000, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean flag(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("CAR", "FLAG_DROP", false, false, false, 4500, true);
        return true;
    }

    @Command
    public boolean giver(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("KISSING", "GIFT_GIVE", false, false, false, 5000, true);
        return true;
    }

    @Command
    public boolean look(Player p, @CommandParameter(name = "1-3")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("ON_LOOKERS", "LKUP_LOOP", false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("ON_LOOKERS", "LKAROUND_LOOP", false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("PED", "FLEE_LKAROUND_01", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean show(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("ON_LOOKERS", "POINT_LOOP", false, false, true);
        return true;
    }

    @Command
    public boolean shout(Player p, @CommandParameter(name = "1-3")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("ON_LOOKERS", "SHOUT_LOOP", false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("ON_LOOKERS", "SHOUT_01", false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("ON_LOOKERS", "SHOUT_02", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean endChat(Player p, @CommandParameter(name = "1-3")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("PED", "ENDCHAT_01", false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("PED", "ENDCHAT_02", false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("PED", "ENDCHAT_03", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean face(Player p, @CommandParameter(name = "1-6")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("PED", "FACANGER", false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("PED", "FACGUM", false, false, true);
                break;
            case 3:
                player.applyLoopAnimation("PED", "FACSURP", false, false, true);
                break;
            case 4:
                player.applyLoopAnimation("PED", "FACSURPM", false, false, true);
                break;
            case 5:
                player.applyLoopAnimation("PED", "FACTALK", false, false, true);
                break;
            case 6:
                player.applyLoopAnimation("PED", "FACURIOS", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean gSign(Player p, @CommandParameter(name = "1-9")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("GHANDS", "GSIGN2", false, false, false, 2000, true);
                break;
            case 2:
                player.applyAnimation("GHANDS", "GSIGN3", false, false, false, 4800, true);
                break;
            case 3:
                player.applyAnimation("GHANDS", "GSIGN4", false, false, false, 4300, true);
                break;
            case 4:
                player.applyAnimation("GHANDS", "GSIGN5", false, false, false, 5000, true);
                break;
            case 5:
                player.applyAnimation("GHANDS", "GSIGN2LH", false, false, false, 2000, true);
                break;
            case 6:
                player.applyAnimation("GHANDS", "GSIGN3LH", false, false, false, 4800, true);
                break;
            case 7:
                player.applyAnimation("GHANDS", "GSIGN4LH", false, false, false, 4300, true);
                break;
            case 8:
                player.applyAnimation("GHANDS", "GSIGN5LH", false, false, false, 5000, true);
                break;
            case 9:
                player.applyAnimation("GHANDS", "GSIGN1LH", false, false, false, 2800, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean dj(Player p, @CommandParameter(name = "1-4")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("SCRATCHING", "SCDLDLP", true, true, true);
                break;
            case 2:
                player.applyLoopAnimation("SCRATCHING", "SCDLULP", true, true, true);
                break;
            case 3:
                player.applyLoopAnimation("SCRATCHING", "SCDRDLP", true, true, true);
                break;
            case 4:
                player.applyLoopAnimation("SCRATCHING", "SCDRULP", true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean loudTalk(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("RIOT", "RIOT_SHOUT", false, false, true);
        return true;
    }

    @Command
    public boolean rem(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("MUSCULAR", "MUSCLEIDLE", false, false, true);
        return true;
    }


    @Command
    public boolean lift(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("CARRY", "LIFTUP", false, false, true, 0, true);
                break;
            case 2:
                player.applyAnimation("CARRY", "LIFTUP05", false, false, true, 0, true);
                break;
            default:
                return false;
        }
        return true;
    }


    @Command
    public boolean place(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("CARRY", "PUTDWN", false, false, false, 900, true);
                break;
            case 2:
                player.applyAnimation("CARRY", "PUTDWN05", false, false, false, 420, true); // BLAZE IT
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean yes(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("GANGS", "INVITE_YES", false, false, false, 3000, true);
        return true;
    }

    @Command
    public boolean no(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("GANGS", "INVITE_NO", false, false, false, 3500, true);
        return true;
    }

    @Command
    public boolean bag(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("BASEBALL", "BAT_IDLE", false, false, true);
        return true;
    }

    @Command
    public boolean wank(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("PAULNMAC", "WANK_LOOP", false, false, true);
        return true;
    }


    @Command
    public boolean pee(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.setSpecialAction(SpecialAction.SPECIAL_ACTION_PISSING);
        return true;
    }


    @Command
    public boolean riot(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("RIOT", "RIOT_ANGRY", false, false, true);
        return true;
    }

    @Command
    public boolean knife(Player p, @CommandParameter(name = "1-4")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("KNIFE", "KILL_KNIFE_PED_DAMAGE", true, true, true, 0, true);
                break;
            case 2:
                player.applyAnimation("KNIFE", "KILL_KNIFE_PED_DIE", true, true, true, 0, true);
                break;
            case 3:
                player.applyAnimation("KNIFE", "KILL_KNIFE_PLAYER", true, true, false, 2300, true);
                break;
            case 4:
                player.applyLoopAnimation("KNIFE", "KILL_PARTIAL", true, true, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean bat(Player p, @CommandParameter(name = "1-2")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("CRACK", "BBALBAT_IDLE_03", false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("CRACK", "BBALBAT_IDLE_03", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean leBelly(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("PED", "KO_SHOT_STOM", true, true, true, 0, true);
        return true;
    }

    @Command
    public boolean leFace(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyAnimation("PED", "KO_SHOT_FACE", true, true, true, 0, true);
        return true;
    }

    @Command
    public boolean aHouse(Player p, @CommandParameter(name = "1-6")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("INT_HOUSE", "BED_IN_L", true, true, true, 0, true);
                break;
            case 2:
                player.applyAnimation("INT_HOUSE", "BED_IN_R", true, true, true, 0, true);
                break;
            case 3:
                player.applyAnimation("INT_HOUSE", "BED_OUT_L", true, true, false, 3000, true);
                break;
            case 4:
                player.applyAnimation("INT_HOUSE", "BED_OUT_R", true, true, false, 3000, true);
                break;
            case 5:
                player.applyLoopAnimation("INT_HOUSE", "LOU_LOOP", false, false, true);
                break;
            case 6:
                player.applyAnimation("INT_HOUSE", "LOU_OUT", false, false, false, 1800, true);
                break;
            default:
                return false;
        }
        return true;
    }


    @Command
    public boolean talk(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.applyLoopAnimation("PED", "IDLE_CHAT", true, true, true);
        return true;
    }

    @Command
    public boolean benchPress(Player p, @CommandParameter(name = "1-7")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("BENCHPRESS", "GYM_BP_CELEBRATE", false, false, false, 5000, true);
                break;
            case 2:
                player.applyLoopAnimation("BENCHPRESS", "GYM_BP_DOWN", false, false, true);
                break;
            case 3:
                player.applyAnimation("BENCHPRESS", "GYM_BP_GETOFF", false, false, false, 8000, true);
                break;
            case 4:
                player.applyAnimation("BENCHPRESS", "GYM_BP_GETON", false, false, true, 0, true);
                break;
            case 5:
                player.applyLoopAnimation("BENCHPRESS", "GYM_BP_UP_A", false, false, true);
                break;
            case 6:
                player.applyLoopAnimation("BENCHPRESS", "GYM_BP_UP_B", false, false, true);
                break;
            case 7:
                player.applyLoopAnimation("BENCHPRESS", "GYM_BP_UP_SMOOTH", false, false, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean camera(Player p, @CommandParameter(name = "1-13")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyLoopAnimation("CAMERA", "CAMCRCH_CMON", false, false, true);
                break;
            case 2:
                player.applyLoopAnimation("CAMERA", "CAMCRCH_IDLELOOP", true, true, true);
                break;
            case 3:
                player.applyAnimation("CAMERA", "CAMCRCH_TO_CAMSTND", true, true, true, 0, true);
                break;
            case 4:
                player.applyLoopAnimation("CAMERA", "CAMSTND_CMON", true, true, true);
                break;
            case 5:
                player.applyLoopAnimation("CAMERA", "CAMSTND_IDLELOOP", true, true, true);
                break;
            case 6:
                player.applyLoopAnimation("CAMERA", "CAMSTND_LKABT", true, true, true);
                break;
            case 7:
                player.applyAnimation("CAMERA", "CAMSTND_TO_CAMCRCH", true, true, true, 0, true);
                break;
            case 8:
                player.applyAnimation("CAMERA", "PICCRCH_IN", true, true, true, 0, true);
                break;
            case 9:
                player.applyAnimation("CAMERA", "PICCRCH_OUT", true, true, true, 0, true);
                break;
            case 10:
                player.applyAnimation("CAMERA", "PICCRCH_TAKE", true, true, true, 0, true);
                break;
            case 11:
                player.applyAnimation("CAMERA", "PICSTND_IN", true, true, true, 0, true);
                break;
            case 12:
                player.applyAnimation("CAMERA", "PICSTND_OUT", true, true, true, 0, true);
                break;
            case 13:
                player.applyAnimation("CAMERA", "PICSTND_TAKE", true, true, true, 0, true);
                break;
            default:
                return false;
        }
        return true;
    }

    @Command
    public boolean carry(Player p, @CommandParameter(name = "1-6")Integer type) {
        LtrpPlayer player = LtrpPlayer.get(p);
        switch(type) {
            case 1:
                player.applyAnimation("CARRY", "LIFTUP", true, true, true, 0, true);
                break;
            case 2:
                player.applyAnimation("CARRY", "LIFTUP05", false, false, true, 0, true);
                break;
            case 3:
                player.applyAnimation("CARRY", "LIFTUP105", false, false, true, 0, true);
                break;
            case 4:
                player.applyAnimation("CARRY", "PUTDWN", false, false, false, 900, true);
                break;
            case 5:
                player.applyAnimation("CARRY", "PUTDWN05", false, false, false, 420, true); // BLAZE IT
                break;
            case 6:
                player.applyAnimation("CARRY", "PUTDWN105", false, false, false, 300, true);
                break;
            default:
                return false;
        }
        return true;
    }

}

