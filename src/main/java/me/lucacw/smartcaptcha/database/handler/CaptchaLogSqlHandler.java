package me.lucacw.smartcaptcha.database.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lucacw.smartcaptcha.captcha.Captcha;
import me.lucacw.smartcaptcha.database.AsyncMySQL;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@RequiredArgsConstructor
public final class CaptchaLogSqlHandler {

    private static final String INSERT_STATEMENT = "insert into captcha_log(userId, guildId, code, imageUUID, attempts, createdAt, finishedAt) values (?, ?, ?, ?, ?, ?, ?);";

    private final AsyncMySQL mySQL;

    @SneakyThrows
    public void logCaptcha(Captcha captcha) {
        final PreparedStatement preparedStatement = this.mySQL.prepare(INSERT_STATEMENT);
        preparedStatement.setString(1, captcha.getUserID());
        preparedStatement.setString(2, captcha.getGuildID());
        preparedStatement.setString(3, captcha.getCode());
        preparedStatement.setString(4, captcha.getImageUUID().toString());
        preparedStatement.setInt(5, captcha.getAttempts());
        preparedStatement.setTimestamp(6, new Timestamp(captcha.getCreatedAt()));
        preparedStatement.setTimestamp(7, Timestamp.from(Instant.now()));

        this.mySQL.update(preparedStatement);
    }

}
