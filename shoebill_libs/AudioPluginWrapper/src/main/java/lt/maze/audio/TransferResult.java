package lt.maze.audio;

/**
 * @author Bebras
 *         2016.02.15.
 */
public enum TransferResult {


    LocalSuccessful(0),
    RemoveSuccessful(1),
    CheckSuccessful(2),
    Error(3);

    int id;

    TransferResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    static TransferResult getById(int id) {
        for(TransferResult r : TransferResult.values()) {
            if(r.getId() == id)
                return r;
        }
        return null;
    }
}
