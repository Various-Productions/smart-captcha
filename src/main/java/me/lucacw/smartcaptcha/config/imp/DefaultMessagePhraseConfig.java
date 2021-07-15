package me.lucacw.smartcaptcha.config.imp;

import lombok.Data;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Data
public class DefaultMessagePhraseConfig {

    private String defaultFooter;
    private String defaultFooterIconUrl;

    private String defaultAuthorIconUrl;
    private String defaultAuthor;

    public DefaultMessagePhraseConfig() {
        this.defaultFooter = "smart-captcha | developed by LucaCW#0023";
        this.defaultFooterIconUrl = null;
        this.defaultAuthorIconUrl = null;
        this.defaultAuthor = "discord-captcha.net";
    }
}
