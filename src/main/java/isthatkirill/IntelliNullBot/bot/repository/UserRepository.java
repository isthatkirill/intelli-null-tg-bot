package isthatkirill.IntelliNullBot.bot.repository;

import isthatkirill.IntelliNullBot.bot.model.BotCall;
import isthatkirill.IntelliNullBot.bot.model.Mappers;
import isthatkirill.IntelliNullBot.bot.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static isthatkirill.IntelliNullBot.bot.util.StringConstants.FIND_CALLS_BY_ID;
import static isthatkirill.IntelliNullBot.bot.util.StringConstants.FIND_USER_BY_ID;

@Slf4j
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

        log.info("[db] Saving user's call chatId={} text={}", botCall.getChatId(), botCall.getText());

        insert.execute(parameters);
    }

    public List<BotCall> findCallsByChatId(Long chatId) {
        return jdbcTemplate.query(FIND_CALLS_BY_ID, Mappers.BOT_CALL_MAPPER, chatId);
    }

    public boolean checkIfRegistered(Long chatId) {

        return jdbcTemplate.queryForObject(FIND_USER_BY_ID, Integer.class, chatId) == 1;
    }


}
