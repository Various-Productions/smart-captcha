package me.lucacw.smartcaptcha.captcha;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lucacw.smartcaptcha.database.handler.CaptchaLogSqlHandler;
import me.lucacw.smartcaptcha.utils.SimpleCaptchaResult;
import me.lucacw.smartcaptcha.utils.ImageUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@RequiredArgsConstructor
public final class CaptchaProvider {

    private final Map<String, Captcha> captchaMap = new ConcurrentHashMap<>();
    private final CaptchaLogSqlHandler captchaLogSqlHandler;

    public Captcha getCaptcha(String id) {
        return this.captchaMap.get(id);
    }

    @SneakyThrows
    public SimpleCaptchaResult<Captcha> createCaptcha(String id, String guildID) {
        if (this.captchaMap.containsKey(id)) return new SimpleCaptchaResult<>("captcha.existing");

        final UUID uuid = UUID.randomUUID();
        final String code = RandomStringUtils.randomAlphanumeric(5);

        final BufferedImage bufferedImage = ImageUtils.createImage(code, 500, 200);

        final Captcha captcha = new Captcha(bufferedImage, guildID, id, code, uuid);

        this.captchaMap.put(id, captcha);
        return new SimpleCaptchaResult<>(captcha);
    }

    public void removeCaptcha(String id) {
        final Captcha captcha = this.captchaMap.get(id);
        if (captcha == null) return;

        this.captchaLogSqlHandler.logCaptcha(captcha);
        this.captchaMap.remove(id);
    }

    public void removeCaptchaByGuild(String guildId) {
        this.captchaMap.forEach((s, captcha) -> {
            if (captcha.getGuildID().equals(guildId)) {
                this.captchaMap.remove(s);
            }
        });
    }

    @CanIgnoreReturnValue
    public boolean verifyCaptcha(String id, String input) {
        final Captcha captcha = this.getCaptcha(id);
        if (captcha == null) return false;

        return captcha.getCode().equals(input);
    }

}
