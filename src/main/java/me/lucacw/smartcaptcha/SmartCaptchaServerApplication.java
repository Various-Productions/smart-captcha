package me.lucacw.smartcaptcha;

import me.lucacw.smartcaptcha.discord.SmartCaptchaDiscordBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Luca R. at 14.07.2021
 * @project smart-captcha
 */
@SpringBootApplication
public class SmartCaptchaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCaptchaServerApplication.class, args);
        new SmartCaptchaDiscordBot().enable();
    }

}
