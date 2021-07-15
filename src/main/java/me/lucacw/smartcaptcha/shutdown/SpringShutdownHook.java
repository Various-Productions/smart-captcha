package me.lucacw.smartcaptcha.shutdown;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import me.lucacw.smartcaptcha.database.AsyncMySQL;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@CommonsLog
@RequiredArgsConstructor
public final class SpringShutdownHook {

    private final ShardManager shardManager;
    private final AsyncMySQL asyncMySQL;

    @PreDestroy
    @SneakyThrows
    public void run() {
        log.info("Shutting down Shard Manager...");
        this.shardManager.shutdown();
        log.info("Shutting down MySQL Connection...");
        this.asyncMySQL.getMySQL().closeConnection();
        log.info("Shutting down...");
    }
}
