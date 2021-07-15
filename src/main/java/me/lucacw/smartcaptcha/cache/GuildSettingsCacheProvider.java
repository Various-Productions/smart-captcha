package me.lucacw.smartcaptcha.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.lucacw.smartcaptcha.database.handler.GuildSettingsSqlHandler;
import me.lucacw.smartcaptcha.settings.GuildSettings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
public final class GuildSettingsCacheProvider {

    private final LoadingCache<String, CompletableFuture<GuildSettings>> guildSettingsLoadingCache;

    private final GuildSettingsSqlHandler guildSettingsSqlHandler;

    public GuildSettingsCacheProvider(GuildSettingsSqlHandler guildSettingsSqlHandler) {
        this.guildSettingsSqlHandler = guildSettingsSqlHandler;

        this.guildSettingsLoadingCache = CacheBuilder.newBuilder()
                .build(new CacheLoader<>() {
                    @Override
                    public CompletableFuture<GuildSettings> load(@NotNull String id) {
                        return guildSettingsSqlHandler.getGuildSettings(id);
                    }
                });
    }

    public CompletableFuture<GuildSettings> getGuildSettings(String id) {
        return this.guildSettingsLoadingCache.getUnchecked(id);
    }

    public void updateGuildSettings(GuildSettings guildSettings) {
        this.guildSettingsSqlHandler.updateGuildSettings(guildSettings);
        this.guildSettingsLoadingCache.invalidate(guildSettings.getId());
    }

    public void deleteGuildSettings(String id) {
        this.guildSettingsSqlHandler.deleteGuildSettings(id);
        this.guildSettingsLoadingCache.invalidate(id);
    }

}
