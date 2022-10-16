package ex.mysql.sqlapi.sqlapi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class SQLAPI extends JavaPlugin {
    public static Logger log = Logger.getLogger("Minecraft");

    public FileConfiguration config = getConfig();
    public HikariDataSource mysql = null;

    @Override
    public void onLoad() {
        config.addDefault("MYSQL.status", false);
        config.addDefault("MYSQL.host", "127.0.0.1");
        config.addDefault("MYSQL.port", "3306");
        config.addDefault("MYSQL.username", "root");
        config.addDefault("MYSQL.password", "");
        config.addDefault("MYSQL.database", "test");
        config.addDefault("MYSQL.useSSL", false);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }


    public void connect() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useSSL=%s", config.getString("MYSQL.host"), config.getString("MYSQL.port"), config.getString("MYSQL.database"), config.getString("MYSQL.useSSL") ));
        hikariConfig.setPassword(config.getString("MYSQL.password"));
        hikariConfig.setUsername(config.getString("MYSQL.username"));
        hikariConfig.setMaxLifetime(30000);
        hikariConfig.setIdleTimeout(10000);
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(3);
        hikariConfig.setPoolName("DropCollector ConnectionPool");

        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheCallableStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", true);
        hikariConfig.addDataSourceProperty("useLocalSessionState", true);
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", true);
        hikariConfig.addDataSourceProperty("alwaysSendSetIsolation", false);

        hikariConfig.setConnectionTestQuery("SELECT 1");

        this.mysql = new HikariDataSource(hikariConfig);
    }

    @Override
    public void onEnable() {
        if (!config.getBoolean("MYSQL.status")) {
            log.warning("[SQLAPI] SQL Status Kapalı!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        connect();
        //log.info(this.mysql.getConnectionTestQuery());
        //log.info("[SQLAPI] SQL Bağlantısı Başarılı!");
    }

    public Connection getConnection() throws SQLException {
        return mysql.getConnection();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
