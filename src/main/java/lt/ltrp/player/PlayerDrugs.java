package lt.ltrp.player;

import lt.ltrp.item.drug.DrugItem;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class PlayerDrugs {


    private LtrpPlayer player;
    private Collection<PlayerAddiction> addictions;
    private Collection<Class<? extends DrugItem>> playerDrugs;

    public PlayerDrugs(LtrpPlayer player) {
        this.player = player;
        this.addictions = new ArrayList<>();
        this.playerDrugs = new HashSet<>();
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public PlayerAddiction getAddiction(Class<? extends DrugItem> drug) {
        Optional<PlayerAddiction> op = addictions.stream()
                .filter(a -> a.getType().equals(drug))
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public void addAddiction(PlayerAddiction addiction) {
        addictions.add(addiction);
    }

    public int getAddictionCount() {
        return addictions.size();
    }

    public int getAddictionLevel(Class<? extends DrugItem> drug) {
        Optional<PlayerAddiction> op = addictions.stream()
                .filter(a -> a.getType().equals(drug))
                .findFirst();
        return op.isPresent() ? op.get().getLevel() : 0;
    }

    public Collection<Class<? extends DrugItem>> getTypes() {
        return addictions.stream()
                .map(PlayerAddiction::getType)
                .collect(Collectors.toList());
    }

    public void setAddictionLevel(Class<? extends DrugItem> drug, int level) {
        addictions.add(new PlayerAddiction(player, drug, level, new Timestamp(new Date().getTime())));
    }

    public void remove(Class<? extends DrugItem> drug) {
        Optional<PlayerAddiction> op = addictions.stream()
                .filter(a -> a.getType().equals(drug))
                .findFirst();
        if(op.isPresent())
            addictions.remove(op.get());
    }

    public boolean isOn(Class<? extends DrugItem> drug) {
        return playerDrugs.contains(drug);
    }

    public void setOnDrugs(Class<? extends DrugItem> drugs, boolean set) {
        if(set) {
            playerDrugs.add(drugs);
            addictions.stream().filter(a -> a.getType().equals(drugs)).forEach(a -> a.setLastDose(new Timestamp(new Date().getTime())));
        }
        else
            playerDrugs.remove(drugs);
    }
}
