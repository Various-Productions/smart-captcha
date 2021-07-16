package me.lucacw.smartcaptcha.command.imp.admin;

import lombok.SneakyThrows;
import me.lucacw.smartcaptcha.command.AbstractCommand;
import me.lucacw.smartcaptcha.config.imp.BotSettingsConfig;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
public final class ShardsCommand extends AbstractCommand {

    private final BotSettingsConfig botSettingsConfig;
    private final ShardManager shardManager;

    public ShardsCommand(BotSettingsConfig botSettingsConfig, ShardManager shardManager) {
        super("shards", "Displays the amount shards online.", "%prefix%shards", new String[]{}, true);
        this.botSettingsConfig = botSettingsConfig;
        this.shardManager = shardManager;
    }

    @SneakyThrows
    @Override
    public void onCommand(TextChannel channel, Member member, String command, String[] args, Message message) {
        final String memberId = member.getId();

        if (!this.botSettingsConfig.getAdmins().contains(memberId)) return;

        channel.sendMessage("Current online shards: " + this.shardManager.getShardsRunning()).queue();
    }
}
