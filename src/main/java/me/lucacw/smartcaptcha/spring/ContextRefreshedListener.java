package me.lucacw.smartcaptcha.spring;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@RequiredArgsConstructor
public final class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    private final ShardManager shardManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        final ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        SpringInitializer.setupContext(applicationContext);

        applicationContext.getBeansOfType(ListenerAdapter.class).forEach((s, listenerAdapter) -> this.shardManager.addEventListener(listenerAdapter));
    }
}
