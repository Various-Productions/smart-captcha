package me.lucacw.smartcaptcha.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@RequiredArgsConstructor
public final class CommandHandler implements ApplicationListener<ContextRefreshedEvent> {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    @Getter
    private final List<AbstractCommand> commandList = new ArrayList<>();

    private final ApplicationContext applicationContext;

    public void executeCommand(TextChannel textChannel, Member member, String message, Message jdaMessage) {
        String command = message.split(" ")[0];
        message = message.replaceFirst(command, "");
        for (AbstractCommand abstractCommand : this.commandList) {
            if (command.equalsIgnoreCase(abstractCommand.getName()) || Arrays.asList(abstractCommand.getAliases()).contains(command)) {
                final String finalMessage = message;
                EXECUTOR.execute(() -> {
                    abstractCommand.onCommand(textChannel, member, command, finalMessage.isEmpty() ? new String[]{} : finalMessage.substring(1).split(" "), jdaMessage);
                });
                jdaMessage.delete().queueAfter(500L, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent contextRefreshedEvent) {
        this.commandList.addAll(this.applicationContext.getBeansOfType(AbstractCommand.class).values());
    }
}
