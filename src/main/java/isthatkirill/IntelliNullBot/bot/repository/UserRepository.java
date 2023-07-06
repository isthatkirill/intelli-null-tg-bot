package isthatkirill.IntelliNullBot.bot.repository;

import isthatkirill.IntelliNullBot.bot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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

    public boolean checkIfRegistered(Long chatId) {
        String sql = "SELECT COUNT(*) FROM users WHERE chat_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, chatId) == 1;
    }


}
