package lt.ltrp.data;


import lt.ltrp.object.LtrpPlayer;

import java.time.LocalDateTime;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class JailData {

    private int id;
    private LtrpPlayer player;
    private JailType type;
    private int remainingTime, totalTime;
    private int jailer;
    private LocalDateTime date;

    public JailData(int id, LtrpPlayer player, JailType type, int remainingTime, int totalTime, int jailer, LocalDateTime date) {
        this.id = id;
        this.player = player;
        this.type = type;
        this.remainingTime = remainingTime;
        this.totalTime = totalTime;
        this.jailer = jailer;
        this.date = date;
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

    public JailType getType() {
        return type;
    }

    public void setType(JailType type) {
        this.type = type;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getJailer() {
        return jailer;
    }

    public void setJailer(int jailer) {
        this.jailer = jailer;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public enum JailType {
        InCharacter,
        OutOfCharacter,
        InCharacterPrison
    }
}
