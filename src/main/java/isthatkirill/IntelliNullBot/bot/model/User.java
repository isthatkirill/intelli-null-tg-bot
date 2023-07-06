package isthatkirill.IntelliNullBot.bot.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    Long id;
    Long chatId;
    String firstName;
    String lastName;
    String userName;
    LocalDateTime registered;

}
