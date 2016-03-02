package lt.ltrp;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import net.gtaun.shoebill.resource.Plugin;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class DatabasePlugin extends Plugin {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "root";
    private static final String password = "";
    private static final String database = "ltrp-java";
    private static final String url = "jdbc:mysql://localhost:3306/" + database;

    private ComboPooledDataSource dataSource;

    @Override
    protected void onEnable() throws Throwable {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(driver);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        dataSource.setMinPoolSize(1);
        dataSource.setMaxPoolSize(6);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected void onDisable() throws Throwable {
        dataSource.close();
    }
}
