package me.lucacw.smartcaptcha.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author Luca R. at 14.07.2021
 * @project smart-captcha
 */
@NoArgsConstructor
@Getter
public final class JsonConfig<T> {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private File file;
    private T config;
    private Class<T> configClass;

    public JsonConfig(Class<T> configClass, String filePath) {
        this.file = new File(filePath);
        this.configClass = configClass;
        this.reload();
    }

    @SneakyThrows
    public void reload() {
        if (!this.file.exists()) {
            this.file.createNewFile();
            this.config = (T) this.configClass.getConstructors()[0].newInstance();
            this.save();
        }
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(this.file), StandardCharsets.UTF_8)) {
            this.config = GSON.fromJson(reader, this.configClass);
        }
    }

    @SneakyThrows
    public void save() {
        try (Writer writer = new FileWriter(this.file)) {
            GSON.toJson(this.config, writer);
        }
    }

}
