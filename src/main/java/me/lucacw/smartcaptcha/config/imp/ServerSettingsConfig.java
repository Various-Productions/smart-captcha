package me.lucacw.smartcaptcha.config.imp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
public class ServerSettingsConfig {

    private final String apiKey;
    private final List<String> whitelistedIps;

    public ServerSettingsConfig() {
        this.apiKey = "API-KEY";
        this.whitelistedIps = Collections.singletonList("127.0.0.1");
    }
}
