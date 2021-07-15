package me.lucacw.smartcaptcha.config.imp;

import lombok.Data;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Collections;
import java.util.List;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Data
public class BotSettingsConfig {

    private String token;

    private Activity.ActivityType activityType;
    private String activity;
    private OnlineStatus onlineStatus;

    private List<String> admins;
    private List<Integer> shards;
    private int totalShards;

    public BotSettingsConfig() {
        this.token = "YOUR_TOKEN";
        this.activity = "smart-captcha - your captcha bot.";
        this.activityType = Activity.ActivityType.DEFAULT;
        this.onlineStatus = OnlineStatus.ONLINE;

        this.admins = Collections.singletonList("634056471746707468");
        this.shards = Collections.singletonList(0);
        this.totalShards = 1;
    }

}
