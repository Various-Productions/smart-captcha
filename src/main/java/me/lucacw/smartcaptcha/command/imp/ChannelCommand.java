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
public final class ChannelCommand extends AbstractCommand {

    private final GuildSettingsCacheProvider guildSettingsCacheProvider;

    public ChannelCommand(GuildSettingsCacheProvider guildSettingsCacheProvider) {
        super("channel", "Set the channel for verification messages.", "%prefix%channel #<Channel>", new String[]{"setchannel", "verificationchannel", "verifychannel"});
        this.guildSettingsCacheProvider = guildSettingsCacheProvider;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, String command, String[] args, Message message) {
        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            return;
        }

        if (args.length != 1) {
            this.sendUsage(channel);
            return;
        }

        if (message.getMentionedChannels().size() != 1) {
            channel.sendMessage("Please mention **one** channel.").queue();
            return;
        }
        final Guild guild = channel.getGuild();

        final TextChannel textChannel = message.getMentionedChannels().get(0);
        if (textChannel == null) return;

        this.guildSettingsCacheProvider.getGuildSettings(guild.getId()).thenAccept(guildSettings -> {
            guildSettings.setVerificationChannelId(textChannel.getId());

            this.guildSettingsCacheProvider.updateGuildSettings(guildSettings);
            channel.sendMessage("You changed the channel for verification messages to " + textChannel.getAsMention() + ".").queue();
        });
    }
}
