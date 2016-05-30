package lt.ltrp.dao.impl;

import lt.ltrp.dao.GraffitiColorDao;
import lt.ltrp.data.Color;
import lt.ltrp.data.GraffitiColor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2016.05.30.
 */
public abstract class AbstractMySqlGraffitiColorDao implements GraffitiColorDao {

    private DataSource dataSource;

    public AbstractMySqlGraffitiColorDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected GraffitiColor toColor(ResultSet r) throws SQLException {
        return new GraffitiColor(
                r.getInt("id"),
                new Color(r.getInt("color"))
        );
    }
}
