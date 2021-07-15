package me.lucacw.smartcaptcha.settings;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Data
@AllArgsConstructor
public final class GuildSettings {

    private final String id;
    private String prefix;

    private String verificationChannelId;
    private String memberRoleId;

    public GuildSettings(String id) {
        this.id = id;
        this.prefix = ".";
    }

}
