package lt.ltrp.player;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class JailData {

    private JailType type;
    private int time;
    private String jailer;

    public JailData(JailType type, int time, String jailer) {
        this.type = type;
        this.time = time;
        this.jailer = jailer;
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

    public enum JailType {
        InCharacter,
        OutOfCharacter
    }
}
