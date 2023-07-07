package isthatkirill.IntelliNullBot.bot.model;

import org.springframework.jdbc.core.RowMapper;

public class Mappers {

    public static final RowMapper<BotCall> BOT_CALL_MAPPER = (rs, rowNum) ->
        BotCall.builder()
                .id(rs.getLong("id"))
                .chatId(rs.getLong("chat_id"))
                .command(rs.getString("command"))
                .text(rs.getString("text"))
                .calledAt(rs.getTimestamp("called_at").toLocalDateTime())
                .build();

}
