package isthatkirill.IntelliNullBot.bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class BotConfig {

    @Value("${bot.name}")
    String name;

    @Value("${bot.token}")
    String token;

}
