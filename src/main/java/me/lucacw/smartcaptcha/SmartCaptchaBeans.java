package me.lucacw.smartcaptcha;

import lombok.SneakyThrows;
import me.lucacw.smartcaptcha.config.JsonConfig;
import me.lucacw.smartcaptcha.config.imp.BotSettingsConfig;
import me.lucacw.smartcaptcha.config.imp.DefaultMessagePhraseConfig;
import me.lucacw.smartcaptcha.config.imp.MySQLDatabaseConfig;
import me.lucacw.smartcaptcha.database.AsyncMySQL;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 14.07.2021
 * @project smart-captcha
 */
@Component
public final class SmartCaptchaBeans {

    private static final JsonConfig<MySQLDatabaseConfig> MYSQL_CONFIG = new JsonConfig<>(MySQLDatabaseConfig.class, "mysql.json");
    private static final JsonConfig<BotSettingsConfig> BOT_SETTINGS_CONFIG = new JsonConfig<>(BotSettingsConfig.class, "bot.json");
    private static final JsonConfig<DefaultMessagePhraseConfig> DEFAULT_MESSAGE_CONFIG = new JsonConfig<>(DefaultMessagePhraseConfig.class, "defaultMessages.json");

    private final MySQLDatabaseConfig mySQLDatabaseConfig = MYSQL_CONFIG.getConfig();
    private final BotSettingsConfig botSettingsConfig = BOT_SETTINGS_CONFIG.getConfig();
    private final DefaultMessagePhraseConfig defaultMessageConfig = DEFAULT_MESSAGE_CONFIG.getConfig();

    private final AsyncMySQL mysql = new AsyncMySQL(MYSQL_CONFIG.getConfig());

    private final ShardManager shardManager;

    @SneakyThrows
    public SmartCaptchaBeans() {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(this.botSettingsConfig.getToken());
        builder.setShardsTotal(this.botSettingsConfig.getTotalShards());
        builder.setShards(this.botSettingsConfig.getShards());
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        this.shardManager = builder.build();
        this.shardManager.setStatusProvider(id -> this.botSettingsConfig.getOnlineStatus());
        this.shardManager.setActivityProvider(shardId -> Activity.of(this.botSettingsConfig.getActivityType(), this.botSettingsConfig.getActivity()
                .replaceAll("%shard%", Integer.toString(shardId))));
    }

    @Bean
    public AsyncMySQL mysql() {
        return this.mysql;
    }

    @Bean
    public ShardManager shardManager() {
        return this.shardManager;
    }

    @Bean
    public MySQLDatabaseConfig mySQLDatabaseConfig() {
        return this.mySQLDatabaseConfig;
    }

    @Bean
    public BotSettingsConfig botSettingsConfig() {
        return this.botSettingsConfig;
    }

    @Bean
    public DefaultMessagePhraseConfig defaultMessageConfig() {
        return this.defaultMessageConfig;
    }

}
