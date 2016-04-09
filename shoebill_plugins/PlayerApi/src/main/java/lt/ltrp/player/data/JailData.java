package lt.ltrp.player.data;


import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class JailData {

    private int id;
    private LtrpPlayer player;
    private JailType type;
    private int time;
    private String jailer;

    public JailData(int id, LtrpPlayer player, JailType type, int time, String jailer) {
        this.type = type;
        this.time = time;
        this.jailer = jailer;
        this.id = id;
        this.player = player;
    }

    public JailType getType() {
        return type;
    }

    public void setType(JailType type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getJailer() {
        return jailer;
    }

    public void setJailer(String jailer) {
        this.jailer = jailer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }

    public enum JailType {
        InCharacter,
        OutOfCharacter
    }
}
