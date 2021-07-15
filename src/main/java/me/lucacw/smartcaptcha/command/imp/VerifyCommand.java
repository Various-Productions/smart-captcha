package me.lucacw.smartcaptcha.command.imp;

import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.captcha.Captcha;
import me.lucacw.smartcaptcha.captcha.CaptchaProvider;
import me.lucacw.smartcaptcha.command.AbstractCommand;
import me.lucacw.smartcaptcha.utils.SimpleCaptchaResult;
import net.dv8tion.jda.api.entities.*;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
public final class VerifyCommand extends AbstractCommand {

    private final GuildSettingsCacheProvider guildSettingsCacheProvider;
    private final CaptchaProvider captchaProvider;

    public VerifyCommand(GuildSettingsCacheProvider guildSettingsCacheProvider, CaptchaProvider captchaProvider) {
        super("verify", "Resend the verification captcha.", "%prefix%verify", new String[]{"resend"});
        this.guildSettingsCacheProvider = guildSettingsCacheProvider;
        this.captchaProvider = captchaProvider;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, String command, String[] args, Message message) {
        final User user = member.getUser();
        final String memberId = member.getId();
        final Guild guild = channel.getGuild();

        this.guildSettingsCacheProvider.getGuildSettings(guild.getId()).thenAccept(guildSettings -> {
            if (guildSettings.getVerificationChannelId() == null) return;
            if (!guildSettings.getVerificationChannelId().equals(channel.getId())) return;

            final Captcha existingCaptcha = this.captchaProvider.getCaptcha(memberId);
            if (existingCaptcha != null) return;

            if (guildSettings.getMemberRoleId() == null) return;
            if (member.getRoles().stream().anyMatch(role -> role.getId().equals(guildSettings.getMemberRoleId()))) return;

            final SimpleCaptchaResult<Captcha> simpleCaptchaResult = this.captchaProvider.createCaptcha(memberId, guild.getId());

            if (!simpleCaptchaResult.isSuccessful()) {
                return;
            }

            simpleCaptchaResult.getBody().send(user, guild);
        });
    }
}
