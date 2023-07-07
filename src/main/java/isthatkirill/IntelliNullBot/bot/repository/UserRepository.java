package isthatkirill.IntelliNullBot.bot.repository;

import isthatkirill.IntelliNullBot.bot.model.BotCall;
import isthatkirill.IntelliNullBot.bot.model.Mappers;
import isthatkirill.IntelliNullBot.bot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long save(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("chat_id", user.getChatId());
        parameters.put("first_name", user.getFirstName());
        parameters.put("last_name", user.getLastName());
        parameters.put("user_name", user.getUserName());
        parameters.put("registered", user.getRegistered());

        Number id = insert.executeAndReturnKey(parameters);
        return id.longValue();
    }

    public void saveCall(BotCall botCall) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("bot_calls")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("chat_id", botCall.getChatId());
        parameters.put("text", botCall.getText());
        parameters.put("called_at", botCall.getCalledAt());
        parameters.put("command", botCall.getCommand());

        insert.execute(parameters);
    }

    public List<BotCall> findCallsByChatId(Long chatId) {
        String query = "SELECT b.* " +
                "FROM bot_calls b " +
                "JOIN users u ON b.chat_id = u.chat_id " +
                "WHERE b.chat_id = ? " +
                "ORDER BY b.called_at DESC " +
                "LIMIT 10";
        return jdbcTemplate.query(query, Mappers.BOT_CALL_MAPPER, chatId);
    }

    public boolean checkIfRegistered(Long chatId) {
        String sql = "SELECT COUNT(*) FROM users WHERE chat_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, chatId) == 1;
    }



}
