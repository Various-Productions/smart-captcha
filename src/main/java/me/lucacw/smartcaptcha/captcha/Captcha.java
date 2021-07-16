package me.lucacw.smartcaptcha.captcha;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Data;
import lombok.SneakyThrows;
import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.config.imp.DefaultMessagePhraseConfig;
import me.lucacw.smartcaptcha.embed.EasyEmbed;
import me.lucacw.smartcaptcha.spring.SpringInitializer;
import me.lucacw.smartcaptcha.utils.StringUtils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Data
public class Captcha {

    private static final CaptchaProvider CAPTCHA_PROVIDER = SpringInitializer.getBean(CaptchaProvider.class);
    private static final DefaultMessagePhraseConfig DEFAULT_MESSAGE_PHRASE_CONFIG = SpringInitializer.getBean(DefaultMessagePhraseConfig.class);
    private static final GuildSettingsCacheProvider GUILD_SETTINGS_CACHE_PROVIDER = SpringInitializer.getBean(GuildSettingsCacheProvider.class);

    private final BufferedImage bufferedImage;

    private final String guildID;
    private final String userID;
    private final String code;
    private final UUID imageUUID;

    private int attempts;

    private long createdAt = System.currentTimeMillis();

    @CanIgnoreReturnValue
    public Captcha increaseAttempts() {
        this.attempts++;
        return this;
    }

    @SneakyThrows
    public void send(User user, Guild guild) {
        File file = new File("images/" + this.imageUUID + ".png");

        try {
            Files.createFile(file.toPath());
            ImageIO.write(this.bufferedImage, "png", file);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        user.openPrivateChannel()
                .flatMap(privateChannel -> {

                    final MessageEmbed messageEmbed = EasyEmbed.builder()
                            .timestamp(Instant.now())
                            .color(Color.decode("#2b3e51"))
                            .title("Captcha Auth | " + guild.getName())
                            .description("Please send the captcha code in this channel. If you need help, feel free to contact a staff member.")
                            .footer(EasyEmbed.Footer.builder().text(DEFAULT_MESSAGE_PHRASE_CONFIG.getDefaultFooter()).build())
                            .imageUrl("attachment://" + this.imageUUID + ".png")
                            .build().buildMessage();

                    return privateChannel.sendFile(file, imageUUID + ".png").embed(messageEmbed);
                }).queue(null, new ErrorHandler()
                .handle(ErrorResponse.CANNOT_SEND_TO_USER, errorResponseException -> {
                            CAPTCHA_PROVIDER.removeCaptcha(user.getId());

                            GUILD_SETTINGS_CACHE_PROVIDER.getGuildSettings(guild.getId()).thenAccept(guildSettings -> {
                                if (guildSettings.getVerificationChannelId() == null) return;

                                final TextChannel verificationChannel = guild.getTextChannelById(guildSettings.getVerificationChannelId());
                                if (verificationChannel == null) return;

                                final MessageEmbed messageEmbed = EasyEmbed.builder()
                                        .timestamp(Instant.now())
                                        .color(Color.RED)
                                        .title("Captcha Verify | " + guild.getName())
                                        .description(StringUtils.format("Please turn your direct messages on as shown in the image. After this type ``%prefix%verify`` in this channel.",
                                                "%prefix%", guildSettings.getPrefix()))
                                        .imageUrl("https://img.inwabel.de/Lh9rEGbV")
                                        .footer(EasyEmbed.Footer.builder().text(DEFAULT_MESSAGE_PHRASE_CONFIG.getDefaultFooter()).build())
                                        .build().buildMessage();

                                verificationChannel.sendMessage(new MessageBuilder()
                                        .setContent(user.getAsMention())
                                        .setEmbed(messageEmbed).build())
                                        .queue();
                            });
                        }
                ));
    }

}
