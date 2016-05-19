package lt.ltrp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Bebras
 *         2016.04.18.
 */
public class TestModel {

    @JsonProperty
    private List<Object> repositories;

    public TestModel() {
    }

    public List<Object> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Object> repositories) {
        this.repositories = repositories;
    }
}
