package lt.ltrp.data;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class Animation {

    public static final String[] ANIMATION_LIBS = new String[]{
            "AIRPORT", "Attractors", "BAR", "BASEBALL", "BD_FIRE", "BEACH", "benchpress", "BF_injection",
            "BIKED", "BIKEH", "BIKELEAP", "BIKES", "BIKEV", "BIKE_DBZ", "BLOWJOBZ", "BMX", "BOMBER", "BOX",
            "BSKTBALL", "BUDDY", "BUS", "CAMERA", "CAR", "CARRY", "CAR_CHAT", "CASINO", "CHAINSAW", "CHOPPA", "CLOTHES",
            "COACH", "COLT45", "COP_AMBIENT", "COP_DVBYZ", "CRACK", "CRIB", "DAMB_JUMP", "DANCING", "DEALER", "DILDO", "DODGE",
            "DOZER", "DRIVEBYS", "FAT", "FIGHT_B", "FIGHT_C", "FIGHT_D", "FIGHT_E", "FINALE", "FINALE2", "FLAME", "FOOD", "FLOWERS",
            "Freewights", "GANGS", "GHANDS", "GHETTO_DB", "goggles", "GRAFFITI", "GRAVEYARD", "GRENADE", "GYMNASIUM",
            "HAIRCUTS", "HEIST9", "INT_HOUSE", "INT_OFFICE", "INT_SHOP", "JET_BUISNESS", "KART", "KISSING", "KNIFE",
            "LAPDAN1", "LAPDAN2", "LAPDAN3", "LOWRIDER", "MD_CHASE", "MD_END", "MEDIC", "MISC", "MTB", "MUSCULAR", "NEVADA",
            "ON_LOOKERS", "OTB", "PARACHUTE", "PARK", "PAULNMAC", "ped", "PLAYER_DVBYS", "PLAYIDLES", "POLICE", "POOL",
            "POOR", "QUAD", "QUAD_DBZ", "RAPPING", "RIFLE", "RIOT", "ROB_BANK", "ROCKET", "RUSTLER", "RYDER",
            "SCRATCHING", "SHAMAL", "SHOP", "SHOTGUN", "SILENCED", "SKATE", "SMOKING", "SNIPER", "SPRAYCAN",
            "STRIP", "SUNBATHE", "SWAT", "SWEET", "SWIM", "SWORD", "TANK", "TATTOOS",
            "TEC", "TRAIN", "TRUCK", "UZI", "WAN", "VENDING", "VORTEX", "WAYFARER", "WEAPONS", "WUZI", "SAMP"
    };

    public static boolean isValidAnimLib(String lib) {
        for(String s : ANIMATION_LIBS) {
            if(s.equals(lib))
                return true;
        }
        return false;
    }

    private String animLib, animName;
    private float speed = 4.1f;
    private boolean loop = false, lockX = false, lockY = false, freeze = false, forseSync = true;
    private int time;

    public Animation(String animLib, String animName, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze, boolean forseSync, int time) {
        this.animLib = animLib;
        this.animName = animName;
        this.speed = speed;
        this.loop = loop;
        this.lockX = lockX;
        this.lockY = lockY;
        this.freeze = freeze;
        this.forseSync = forseSync;
        this.time = time;
    }

    public Animation(String animLib, String animName, boolean loop, int time) {
        this.animLib = animLib;
        this.animName = animName;
        this.loop = loop;
        this.time = time;
    }

    public static String[] getAnimationLibs() {
        return ANIMATION_LIBS;
    }

    public String getAnimLib() {
        return animLib;
    }

    public void setAnimLib(String animLib) {
        this.animLib = animLib;
    }

    public String getAnimName() {
        return animName;
    }

    public void setAnimName(String animName) {
        this.animName = animName;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLockX() {
        return lockX;
    }

    public void setLockX(boolean lockX) {
        this.lockX = lockX;
    }

    public boolean isLockY() {
        return lockY;
    }

    public void setLockY(boolean lockY) {
        this.lockY = lockY;
    }

    public boolean isFreeze() {
        return freeze;
    }

    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
    }

    public boolean isForseSync() {
        return forseSync;
    }

    public void setForseSync(boolean forseSync) {
        this.forseSync = forseSync;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
