package me.lucacw.smartcaptcha.database;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import me.lucacw.smartcaptcha.config.imp.*;

/**
 * @author ShortyDev
 * @project mcprotection-ticket-bot
 */
@CommonsLog
public final class AsyncMySQL {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);
    private final MySQL sql;

    public AsyncMySQL(MySQLDatabaseConfig config) {
        this(config.getHostname(),
                config.getPort(),
                config.getUsername(),
                config.getPassword(),
                config.getDatabase());
    }

    public AsyncMySQL(String host, int port, String user, String password, String database) {
        sql = MySQL.builder().host(host).port(port).user(user).password(password).database(database).build();
        sql.openConnection();
    }

    public void update(PreparedStatement statement) {
        EXECUTOR.execute(() -> {
            sql.queryUpdate(statement);
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void update(String statement) {
        EXECUTOR.execute(() -> sql.queryUpdate(statement));
    }

    public void query(PreparedStatement statement, Consumer<ResultSet> consumer) {
        EXECUTOR.execute(() -> {
            ResultSet result = sql.query(statement);

            consumer.accept(result);
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void query(String statement, Consumer<ResultSet> consumer) {
        EXECUTOR.execute(() -> {
            ResultSet result = sql.query(statement);
            consumer.accept(result);
        });
    }

    @SneakyThrows
    public PreparedStatement prepare(String query) {
        return sql.getConnection().prepareStatement(query);
    }

    public MySQL getMySQL() {
        return sql;
    }

    @Builder
    public static class MySQL {

        private final String host;
        private final String user;
        private final String password;
        private final String database;
        private final int port;

        private Connection connection;

        public void queryUpdate(String query) {
            checkConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                queryUpdate(statement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void queryUpdate(PreparedStatement statement) {
            checkConnection();
            try {
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public ResultSet query(String query) {
            checkConnection();
            try {
                return query(connection.prepareStatement(query));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public ResultSet query(PreparedStatement statement) {
            checkConnection();
            try {
                return statement.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public Connection getConnection() {
            return this.connection;
        }

        private void checkConnection() {
            try {
                if (this.connection == null || !this.connection.isValid(10) || this.connection.isClosed()) openConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void openConnection() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true", this.user, this.password);
                log.info("MySQL Connection successfully established on " + this.user + "@" + this.host + ":" + this.port);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                log.info("MySQL Connection failed to establish.");
            }
        }

        public void closeConnection() {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                this.connection = null;
            }
        }
    }

}
