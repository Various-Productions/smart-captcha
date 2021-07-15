package me.lucacw.smartcaptcha.utils;

import lombok.Getter;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Getter
public final class SimpleCaptchaResult<T> {

    private final T body;

    private final boolean successful;
    private final String[] replacements;
    private final String message;

    public SimpleCaptchaResult() {
        this(null, true, null);
    }

    public SimpleCaptchaResult(final T body) {
        this(body, body != null, null);
    }

    public SimpleCaptchaResult(final String message) {
        this(null, false, message);
    }

    public SimpleCaptchaResult(final String message, final String... replacements) {
        this(null, false, message, replacements);
    }

    public SimpleCaptchaResult(final T body, final boolean successful, final String message, final String... replacements) {
        this.body = body;
        this.replacements = replacements;
        this.successful = successful;
        this.message = message;
    }

}
