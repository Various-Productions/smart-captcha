package me.lucacw.smartcaptcha;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Luca R. at 14.07.2021
 * @project smart-captcha
 */
@SpringBootApplication
public class SmartCaptchaServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SmartCaptchaServerApplication.class)
                .banner((environment, sourceClass, out) -> {
                    try (FileReader fileReader = new FileReader("banner.txt")) {
                        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                            String line;

                            while ((line = bufferedReader.readLine()) != null) {
                                out.println(line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .run(args);
    }

}
