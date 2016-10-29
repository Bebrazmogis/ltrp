package lt.maze.audio.event;

import lt.maze.audio.TransferResult;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioTransferFileEvent extends PlayerEvent {

    private String fileName;
    private int current, total;
    private TransferResult result;

    public AudioTransferFileEvent(Player player, String fileName, int current, int total, TransferResult result) {
        super(player);
        this.fileName = fileName;
        this.current = current;
        this.total = total;
        this.result = result;
    }

    public String getFileName() {
        return fileName;
    }

    public int getCurrent() {
        return current;
    }

    public int getTotal() {
        return total;
    }

    public TransferResult getResult() {
        return result;
    }
}
