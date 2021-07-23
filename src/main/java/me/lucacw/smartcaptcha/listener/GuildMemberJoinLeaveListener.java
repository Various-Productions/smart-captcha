package me.lucacw.smartcaptcha.listener;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import me.lucacw.smartcaptcha.captcha.Captcha;
import me.lucacw.smartcaptcha.captcha.CaptchaProvider;
import me.lucacw.smartcaptcha.utils.ResponseEntity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
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
public final class GuildMemberJoinLeaveListener extends ListenerAdapter {

    private final CaptchaProvider captchaProvider;

    @Override
    @SneakyThrows
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        final User user = event.getUser();
        final Guild guild = event.getGuild();
        final ResponseEntity<Captcha> responseEntity = this.captchaProvider.createCaptcha(user.getId(), guild.getId());

        if (!responseEntity.isSuccessful()) {
            return;
        }

        responseEntity.getBody().send(user, guild);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        this.captchaProvider.removeCaptcha(event.getUser().getId());
    }
}
