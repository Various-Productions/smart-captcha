package me.lucacw.smartcaptcha.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.settings.GuildSettings;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@CommonsLog
@RequiredArgsConstructor
public final class GuildJoinListener extends ListenerAdapter {

    private final GuildSettingsCacheProvider guildSettingsCacheProvider;

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        this.guildSettingsCacheProvider.updateGuildSettings(new GuildSettings(event.getGuild().getId()));
    }
}
