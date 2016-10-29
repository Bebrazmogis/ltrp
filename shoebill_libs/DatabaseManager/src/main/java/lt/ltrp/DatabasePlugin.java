package lt.ltrp;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class DatabasePlugin extends Plugin {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "root";
    private static final String password = "";
    private static final String database = "ltrp-java";
    private static final String DEFAULT_CHARSET = "utf8";
    private static final String DEFAULT_COLLATE = "utf8_unicode_ci";
    private static final String url = "jdbc:mysql://localhost:3306/" + database + "?characterEncoding=" + DEFAULT_CHARSET + "";

    private ComboPooledDataSource dataSource;

    @Override
    protected void onEnable() throws Throwable {
        Logger logger = getLogger();
        logger.debug("onEnable");
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(driver);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        Properties props = dataSource.getProperties();
        props.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.slf4j.Slf4jMLog");
        props.put("com.mchange.v2.log.NameTransformer", "com.mchange.v2.log.PackageNames");
        props.put("charSet", DEFAULT_CHARSET);
        dataSource.setProperties(props);
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        dataSource.setMinPoolSize(1);
        dataSource.setMaxPoolSize(6);

        if(dataSource == null) {
            throw new RuntimeException("Datasource is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement();
        ) {
            stmt.executeUpdate("SET NAMES " + DEFAULT_CHARSET);
            stmt.executeUpdate("SET character_set_client=" + DEFAULT_CHARSET);
            stmt.executeUpdate("SET character_set_results=" + DEFAULT_CHARSET);
            stmt.executeUpdate("SET character_set_connection=" + DEFAULT_CHARSET);
            stmt.executeUpdate("SET CHARACTER SET " + DEFAULT_CHARSET);
            stmt.executeUpdate("SET character_set_database=" + DEFAULT_CHARSET);
            stmt.executeUpdate("SET character_set_server="+DEFAULT_CHARSET);

            ResultSet r = stmt.executeQuery("show variables like '%character%'");
            while(r.next()) {
                logger.debug(r.getObject(1) + "=" + r.getObject(2));
            }
        } catch (SQLException e) {
            logger.error(getClass().getName() + " Could not set charset");
        }

        logger.info("Database plugin loaded. DataSource:" + dataSource.toString());
    }

    public DataSource getDataSource() {
        return dataSource;
    }


    @Override
    protected void onDisable() throws Throwable {
        dataSource.close();
    }
}
