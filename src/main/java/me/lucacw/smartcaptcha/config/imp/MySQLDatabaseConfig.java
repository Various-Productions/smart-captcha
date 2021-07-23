package me.lucacw.smartcaptcha.config.imp;

import lombok.Data;

/**
 * @author Luca R. at 14.07.2021
 * @project smart-captcha
 */
@Data
public class MySQLDatabaseConfig {

    private final int port;
    private final String hostname;

    private final String username;
    private final String password;
    private final String database;

    public MySQLDatabaseConfig() {
        this.port = 3306;
        this.hostname = "localhost";
        this.username = "root";
        this.password = "PASSWORD";
        this.database = "smart-captcha";
    }
}
