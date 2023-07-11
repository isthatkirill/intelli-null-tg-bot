package isthatkirill.IntelliNullBot.bot.util;

import com.vdurmont.emoji.EmojiParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringConstants {

    public static final String ENTER_EXPRESSION = EmojiParser
            .parseToUnicode("Enter your expression.:pencil2: \nAvailable operations:\n" +
        "[+] addition\n[-] subtraction\n[*] multiplication\n[/] division\n[^] exponentiation\n[()] brackets");

    public static final String ENTER_CITY = EmojiParser
            .parseToUnicode("Enter your city or airport code and i will send you the weather " +
                    "forecast:cityscape:");

    public static final String COMMAND_NOT_AVAILABLE = EmojiParser
            .parseToUnicode("Such a command is not yet available!:confused:");

    public static final String CREATED_BY = EmojiParser
            .parseToUnicode("The bot is created by @isthatkirill:revolving_hearts:");

    public static final String INTRO = EmojiParser
            .parseToUnicode(", nice too meet уou! What do you want to do?:eyes:");

    public static final String START = "Start the bot!";

    public static final String SELECT_LANG = "Select a language available for translation: ";

    public static final String WHAT_DO_U_DO = EmojiParser
            .parseToUnicode("What do you want to do?:eyes:");

    public static final List<String> COMMANDS = List.of("/start", "Weather :white_sun_behind_cloud_rain:",
            "Calculator :1234:", "Go back :back:", "About :question:", "History :telescope:",
            "Translate :page_facing_up:", "Meme :chicken:");

    public static final String FIND_USER_BY_ID = "SELECT COUNT(*) FROM users WHERE chat_id = ?";

    public static final String TOTAL_CALLS = "SELECT COUNT(*) FROM bot_calls";

    public static final String TOTAL_USERS = "SELECT COUNT(*) FROM users";

    public static final String FIND_CALLS_BY_ID = "SELECT b.* " +
            "FROM bot_calls b " +
            "JOIN users u ON b.chat_id = u.chat_id " +
            "WHERE b.chat_id = ? AND " +
            "b.is_complete = true " +
            "ORDER BY b.called_at DESC " +
            "LIMIT 10";

    public static final String INVALID_EXPRESSION = "Invalid expression. Please try again.";

    public static final Map<String, String> EMOJI = new HashMap<>(12) {{
        put("Thunder", ":thunder_cloud_rain:");
        put("Sunny", ":sunny:");
        put("Partly cloudy", ":white_sun_small_cloud:");
        put("Cloudy", ":partly_sunny:");
        put("Overcast", ":cloud:");
        put("Patchy rain possible", ":white_sun_behind_cloud_rain:");
        put("Moderate rain", ":cloud_rain:");
        put("Heavy rain", ":cloud_rain:");
        put("Light rain", ":white_sun_behind_cloud_rain:");
        put("Light rain shower", ":white_sun_behind_cloud_rain:");
        put("Moderate or heavy rain with thunder", ":thunder_cloud_rain:");
    }};

    public static final Map<String, String> LANGUAGES_CODES =
            Map.of("RUS", "rus_Cyrl", "ENG", "eng_Latn");

}
