package me.lucacw.smartcaptcha.command;

import lombok.Getter;
import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.spring.SpringInitializer;
import me.lucacw.smartcaptcha.utils.StringUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Getter
public abstract class AbstractCommand {

    private final String name;
    private final String[] aliases;
    private final String description;
    private final String usage;

    private final boolean adminCommand;

    public AbstractCommand(String name, String description, String usage, String[] aliases, boolean adminCommand) {
        this.name = name;
        this.description = description;
        this.adminCommand = adminCommand;
        this.aliases = aliases;
        this.usage = usage;
    }

    public AbstractCommand(String name, String description, String usage, String[] aliases) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.adminCommand = false;
    }

    /**
     * Execute this command with the specified sender, command and arguments.
     *
     * @param channel channel the command was executed in
     * @param member  command executor
     * @param command the executed command
     * @param args    arguments used to invoke this command
     * @param message JDA Message object
     */
    public abstract void onCommand(TextChannel channel, Member member, String command, String[] args, Message message);

    public void sendUsage(TextChannel channel) {
        final Guild guild = channel.getGuild();

        SpringInitializer.getBean(GuildSettingsCacheProvider.class).getGuildSettings(guild.getId()).thenAccept(guildSettings -> {
            channel.sendMessage(StringUtils.format("__Usage:__ " + this.usage, "%prefix%", guildSettings.getPrefix())).queue();
        });
    }

}
