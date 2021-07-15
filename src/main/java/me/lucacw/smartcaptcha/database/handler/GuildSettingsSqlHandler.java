package me.lucacw.smartcaptcha.database.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lucacw.smartcaptcha.database.AsyncMySQL;
import me.lucacw.smartcaptcha.settings.GuildSettings;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

/**
 * @author Luca R. at 15.07.2021
 * @project smart-captcha
 */
@Component
@RequiredArgsConstructor
public final class GuildSettingsSqlHandler {

    private static final String FETCH_STATEMENT = "select * from guild_settings where id = ?;";
    private static final String UPDATE_STATEMENT = "insert into guild_settings(id, prefix, verification_channel, member_role) " +
            "values (?, ?, ?, ?) " +
            "on duplicate key update prefix = ?, verification_channel = ?, member_role = ?;";
    private static final String DELETE_STATEMENT = "delete from guild_settings where id = ?;";

    private final AsyncMySQL mySQL;

    @SneakyThrows
    public void updateGuildSettings(GuildSettings guildSettings) {
        final PreparedStatement preparedStatement = this.mySQL.prepare(UPDATE_STATEMENT);
        preparedStatement.setString(1, guildSettings.getId());
        preparedStatement.setString(2, guildSettings.getPrefix());
        preparedStatement.setString(3, guildSettings.getVerificationChannelId());
        preparedStatement.setString(4, guildSettings.getMemberRoleId());

        preparedStatement.setString(5, guildSettings.getPrefix());
        preparedStatement.setString(6, guildSettings.getVerificationChannelId());
        preparedStatement.setString(7, guildSettings.getMemberRoleId());

        this.mySQL.update(preparedStatement);
    }

    @SneakyThrows
    public void deleteGuildSettings(String id) {
        final PreparedStatement preparedStatement = this.mySQL.prepare(DELETE_STATEMENT);
        preparedStatement.setString(1, id);

        this.mySQL.update(preparedStatement);
    }

    @SneakyThrows
    public CompletableFuture<GuildSettings> getGuildSettings(String id) {
        final CompletableFuture<GuildSettings> completableFuture = new CompletableFuture<>();

        final PreparedStatement preparedStatement = this.mySQL.prepare(FETCH_STATEMENT);
        preparedStatement.setString(1, id);
        this.mySQL.query(preparedStatement, resultSet -> {
            try {
                if (resultSet.next())
                    completableFuture.complete(new GuildSettings(
                            resultSet.getString("id"),
                            resultSet.getString("prefix"),
                            resultSet.getString("verification_channel"),
                            resultSet.getString("member_role")));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        return completableFuture;
    }

}
