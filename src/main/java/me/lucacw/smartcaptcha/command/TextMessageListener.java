package me.lucacw.smartcaptcha.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import me.lucacw.smartcaptcha.cache.GuildSettingsCacheProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
public final class TextMessageListener extends ListenerAdapter {

    private final GuildSettingsCacheProvider guildSettingsCacheProvider;
    private final CommandHandler commandHandler;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        final Guild guild = event.getMessage().getGuild();
        this.guildSettingsCacheProvider.getGuildSettings(guild.getId()).thenAccept(guildSettings -> {
            final String prefix = guildSettings.getPrefix();

            if (event.getMessage().getContentRaw().startsWith(prefix))
                this.commandHandler.executeCommand(event.getChannel(), event.getMember(), event.getMessage().getContentRaw().substring(prefix.length()), event.getMessage());
        });
    }
}
