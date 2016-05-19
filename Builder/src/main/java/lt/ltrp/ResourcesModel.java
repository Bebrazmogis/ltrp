package lt.ltrp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.04.18.
 */
public class ResourcesModel {

    @JsonProperty
  //  @JsonRawValue
    private List<Object> repositories;

    @JsonProperty
    private Object cacheUpdatePolicy;

    @JsonProperty
    //@JsonRawValue
    private boolean offlineMode;

    @JsonProperty
   // @JsonRawValue
    private List<String> runtimes;

    @JsonProperty
   // @JsonRawValue
    private List<String> plugins;

    @JsonProperty
   // @JsonRawValue
    private String gamemode;

    public ResourcesModel() {
    }

    public List<Object> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Object> repositories) {
        this.repositories = repositories;
    }

    //@JsonRawValue
    public Object getCacheUpdatePolicy() {
        return cacheUpdatePolicy;
    }

    public void setCacheUpdatePolicy(Object cacheUpdatePolicy) {
        this.cacheUpdatePolicy = cacheUpdatePolicy;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public List<String> getRuntimes() {
        return runtimes;
    }

    public void setRuntimes(List<String> runtimes) {
        this.runtimes = runtimes;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public String toString() {
        return "ResourcesModel[" +
                "repositories={" + repositories.stream().map(Object::toString).collect(Collectors.joining(", ")) + "} " +
                "cacheUpdatePolicy=" + cacheUpdatePolicy + " " +
                "offlineMode=" + offlineMode + " " +
                "runtimes={" + runtimes.stream().map(Object::toString).collect(Collectors.joining(", ")) + "} " +
                "plugins={" + plugins.stream().map(Object::toString).collect(Collectors.joining(", ")) + "} " +
                "gamemode=" + gamemode +
                "]";
    }
}

