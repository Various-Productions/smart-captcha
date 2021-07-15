package me.lucacw.smartcaptcha.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@ShellComponent
@RequiredArgsConstructor
public final class StopCommand {

    private final ApplicationContext applicationContext;

    @ShellMethod("Stops the Application.")
    public void stop() {
        final int exitCode = SpringApplication.exit(this.applicationContext);
        System.exit(exitCode);
    }

}
