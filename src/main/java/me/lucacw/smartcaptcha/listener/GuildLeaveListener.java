package me.lucacw.smartcaptcha.listener;

import lombok.RequiredArgsConstructor;
import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.captcha.CaptchaProvider;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@RequiredArgsConstructor
public final class GuildLeaveListener extends ListenerAdapter {

    private final CaptchaProvider captchaProvider;
    private final GuildSettingsCacheProvider guildSettingsCacheProvider;

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        final String id = event.getGuild().getId();

        this.guildSettingsCacheProvider.deleteGuildSettings(id);
        this.captchaProvider.removeCaptchaByGuild(id);
    }
}
