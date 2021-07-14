package me.lucacw.smartcaptcha;

import me.lucacw.smartcaptcha.config.JsonConfig;
import me.lucacw.smartcaptcha.config.imp.MySQLDatabaseConfig;
import me.lucacw.smartcaptcha.database.AsyncMySQL;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 14.07.2021
 * @project smart-captcha
 */
@Component
public final class SmartCaptchaBeans {

    private static final JsonConfig<MySQLDatabaseConfig> MYSQL_CONFIG = new JsonConfig(MySQLDatabaseConfig.class, "mysql.json");

    private final AsyncMySQL mysql = new AsyncMySQL(MYSQL_CONFIG.getConfig());

    @Bean
    public AsyncMySQL mysql() {
        return this.mysql;
    }

}
