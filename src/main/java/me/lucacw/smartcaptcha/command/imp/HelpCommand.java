package me.lucacw.smartcaptcha.command.imp;

import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.command.AbstractCommand;
import me.lucacw.smartcaptcha.command.CommandHandler;
import me.lucacw.smartcaptcha.config.imp.DefaultMessagePhraseConfig;
import me.lucacw.smartcaptcha.embed.EasyEmbed;
import me.lucacw.smartcaptcha.utils.StringUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Instant;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
public final class HelpCommand extends AbstractCommand {

    private final CommandHandler commandHandler;
    private final GuildSettingsCacheProvider guildSettingsCacheProvider;
    private final DefaultMessagePhraseConfig defaultMessagePhraseConfig;

    public HelpCommand(CommandHandler commandHandler, GuildSettingsCacheProvider guildSettingsCacheProvider, DefaultMessagePhraseConfig defaultMessagePhraseConfig) {
        super("help", "Show the help menu.", "$help", new String[]{"support"});
        this.commandHandler = commandHandler;
        this.guildSettingsCacheProvider = guildSettingsCacheProvider;
        this.defaultMessagePhraseConfig = defaultMessagePhraseConfig;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, String command, String[] args, Message message) {
        final EasyEmbed easyEmbed = EasyEmbed.builder()
                .timestamp(Instant.now())
                .color(Color.decode("#2b3e51"))
                .title("Smart Captcha | Help Menu")
                .description("If you need some extra help just join our discord server: https://discord.gg/Sqq8XS3 and ask in support.")
                .footer(EasyEmbed.Footer.builder().text(this.defaultMessagePhraseConfig.getDefaultFooter()).build())
                .build();

        this.guildSettingsCacheProvider.getGuildSettings(channel.getGuild().getId()).thenAccept(guildSettings -> {
            this.commandHandler.getCommandList().stream()
                    .filter(abstractCommand -> !abstractCommand.isAdminCommand())
                    .forEach(abstractCommand -> {
                        final MessageEmbed.Field field = new MessageEmbed.Field(StringUtils.format("**" + abstractCommand.getUsage() + "**", "%prefix%", guildSettings.getPrefix()),
                                abstractCommand.getDescription(), false);
                        easyEmbed.addField(field);
                    });

            channel.sendMessage(easyEmbed.buildMessage()).queue();
        });
    }
}
