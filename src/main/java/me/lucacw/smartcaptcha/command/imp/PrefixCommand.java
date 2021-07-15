package me.lucacw.smartcaptcha.command.imp;

import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.command.AbstractCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
public final class PrefixCommand extends AbstractCommand {

    private final GuildSettingsCacheProvider guildSettingsCacheProvider;

    public PrefixCommand(GuildSettingsCacheProvider guildSettingsCacheProvider) {
        super("prefix", "Set the prefix of the bot.", "%prefix%prefix <Custom Prefix>", new String[]{"setprefix"});
        this.guildSettingsCacheProvider = guildSettingsCacheProvider;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, String command, String[] args, Message message) {
        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            return;
        }

        final Guild guild = channel.getGuild();

        if (args.length != 1) {
            this.sendUsage(channel);
            return;
        }

        final String prefix = args[0];

        this.guildSettingsCacheProvider.getGuildSettings(guild.getId()).thenAccept(guildSettings -> {
            guildSettings.setPrefix(prefix);

            this.guildSettingsCacheProvider.updateGuildSettings(guildSettings);

            channel.sendMessage("Successfully changed the prefix to **" + prefix + "**.").queue();
        });
    }
}
