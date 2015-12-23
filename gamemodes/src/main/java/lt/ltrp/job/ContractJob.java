package lt.ltrp.job;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.JobVehicle;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.List;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */

public class ContractJob implements Job {

    public static final int INVALID_ID = 0;

    private int id;
    private int contractLength;
    private int minPaycheck, maxPaycheck;
    private String name;
    private Location location;
    private List<ContractJobRank> ranks;
    private Map<ContractJobRank, JobVehicle> vehicles;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location loc) {
        this.location = loc;
    }

    @Override
    public List<ContractJobRank> getRanks() {
        return ranks;
    }

    @Override
    public ContractJobRank getRank(int id) {
        for(ContractJobRank c : ranks) {
            if(c.getNumber() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public Map<ContractJobRank, JobVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public boolean isAtWork(LtrpPlayer player) {
        return this.location.distance(player.getLocation()) < 10.0f;
    }

    @Override
    public void sendMessage(Color color, String message) {
        LtrpPlayer.get().stream().filter(p -> p.getJob().equals(this)).forEach(p -> p.sendMessage(color, message));
    }

    public void setRanks(List<ContractJobRank> ranks) {
        this.ranks =  ranks;
    }

    public int getMaxPaycheck() {
        return maxPaycheck;
    }

    public void setMaxPaycheck(int maxPaycheck) {
        this.maxPaycheck = maxPaycheck;
    }

    public int getMinPaycheck() {
        return minPaycheck;
    }

    public void setMinPaycheck(int minPaycheck) {
        this.minPaycheck = minPaycheck;
    }

    public int getContractLength() {
        return contractLength;
    }

    public void setContractLength(int contractLength) {
        this.contractLength = contractLength;
    }

    @Override
    public String toString() {
        return String.format("Contract Job. Id %s Name:%s Rank count:%d vehicle count:%d", id, getName(), getRanks().size(), vehicles.size());
    }
}
