package me.lucacw.smartcaptcha.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.captcha.Captcha;
import me.lucacw.smartcaptcha.captcha.CaptchaProvider;
import me.lucacw.smartcaptcha.config.imp.DefaultMessagePhraseConfig;
import me.lucacw.smartcaptcha.embed.EasyEmbed;
import me.lucacw.smartcaptcha.utils.SimpleCaptchaResult;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Instant;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@CommonsLog
@RequiredArgsConstructor
public final class PrivateMessageListener extends ListenerAdapter {

    private final DefaultMessagePhraseConfig defaultMessagePhraseConfig;
    private final GuildSettingsCacheProvider guildSettingsCacheProvider;
    private final CaptchaProvider captchaProvider;
    private final ShardManager shardManager;

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        final User author = event.getAuthor();
        final Message message = event.getMessage();
        final MessageChannel channel = message.getChannel();

        final Captcha captcha = this.captchaProvider.getCaptcha(author.getId());
        if (captcha == null) return;

        final boolean captchaResult = this.captchaProvider.verifyCaptcha(author.getId(), message.getContentRaw());
        captcha.increaseAttempts();

        final Guild guild = this.shardManager.getGuildById(captcha.getGuildID());
        if (guild == null) return;

        if (captchaResult) {
            this.guildSettingsCacheProvider.getGuildSettings(captcha.getGuildID()).thenAccept(guildSettings -> {
                final String memberRoleId = guildSettings.getMemberRoleId();
                if (memberRoleId == null) return;

                final Role role = this.shardManager.getRoleById(memberRoleId);
                if (role == null) return;

                guild.addRoleToMember(captcha.getUserID(), role)
                        .queue(unused -> {
                            final MessageEmbed messageEmbed = EasyEmbed.builder()
                                    .timestamp(Instant.now())
                                    .title("Successfully Verified | " + guild.getName())
                                    .description("Success! You are now verified on **" + guild.getName() + "**.")
                                    .color(Color.GREEN)
                                    .footer(EasyEmbed.Footer.builder().text(this.defaultMessagePhraseConfig.getDefaultFooter()).build())
                                    .build().buildMessage();

                            channel.sendMessage(messageEmbed)
                                    .queue(unused1 -> {

                                    }, new ErrorHandler());
                        }, new ErrorHandler()
                                .handle(ErrorResponse.MISSING_PERMISSIONS, missingPermissions -> {
                                    final MessageEmbed errorEmbed = EasyEmbed.builder()
                                            .timestamp(Instant.now())
                                            .title("Error | " + guild.getName())
                                            .description("Error! The bot doesn't have enough permissions to give you the member role. Please contact a staff member!")
                                            .color(Color.RED)
                                            .footer(EasyEmbed.Footer.builder().text(this.defaultMessagePhraseConfig.getDefaultFooter()).build())
                                            .build().buildMessage();

                                    channel.sendMessage(errorEmbed)
                                            .queue(unused -> {
                                            }, new ErrorHandler());
                                }));
                this.captchaProvider.removeCaptcha(author.getId());
            });
        } else {
            int attempts = 3 - captcha.getAttempts();

            final MessageEmbed messageEmbed = EasyEmbed.builder()
                    .timestamp(Instant.now())
                    .color(Color.RED)
                    .title("Captcha Auth | Failed (" + captcha.getAttempts() + "/3) ")
                    .description(captcha.getAttempts() < 3
                            ? "Captcha failed! You have " + attempts + " attempt" + (attempts != 1 ? "s" : "") + " left. Please pay attention, it's case sensitive."
                            : "Captcha failed too often, please enter the new code.")
                    .footer(EasyEmbed.Footer.builder().text(this.defaultMessagePhraseConfig.getDefaultFooter()).build())
                    .build().buildMessage();

            channel.sendMessage(messageEmbed).queue(null, new ErrorHandler());

            if (captcha.getAttempts() >= 3) {
                this.captchaProvider.removeCaptcha(author.getId());
                final SimpleCaptchaResult<Captcha> result = this.captchaProvider.createCaptcha(author.getId(), captcha.getGuildID());

                if (!result.isSuccessful()) return;

                result.getBody().send(author, guild);
            }
        }

    }
}
