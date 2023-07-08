package isthatkirill.IntelliNullBot.bot.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotCall {

    Long id;
    Long chatId;
    String command;
    String text;
    LocalDateTime calledAt;
    Boolean isComplete;

}
