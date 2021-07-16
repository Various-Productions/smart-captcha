package me.lucacw.smartcaptcha.command.imp;

import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import me.lucacw.smartcaptcha.command.AbstractCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
public final class RoleCommand extends AbstractCommand {

    private final GuildSettingsCacheProvider guildSettingsCacheProvider;

    public RoleCommand(GuildSettingsCacheProvider guildSettingsCacheProvider) {
        super("role", "Set the role that verified users received.", "%prefix%role @<Role>", new String[]{"changerole", "setrole"});
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

        if (message.getMentionedRoles().size() != 1) {
            channel.sendMessage("Please mention **one** role.").queue();
            return;
        }
        final Guild guild = channel.getGuild();

        final Role role = message.getMentionedRoles().get(0);
        if (role == null) return;

        this.guildSettingsCacheProvider.getGuildSettings(guild.getId()).thenAccept(guildSettings -> {
            guildSettings.setMemberRoleId(role.getId());

            this.guildSettingsCacheProvider.updateGuildSettings(guildSettings);
            channel.sendMessage("You changed the verified member role to " + role.getAsMention() + ".").queue();
        });
    }
}
