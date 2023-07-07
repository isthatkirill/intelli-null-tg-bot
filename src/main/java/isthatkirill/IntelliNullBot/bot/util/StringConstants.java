package isthatkirill.IntelliNullBot.bot.util;

import com.vdurmont.emoji.EmojiParser;

import java.util.List;

public class StringConstants {

    public static final String ENTER_EXPRESSION = EmojiParser
            .parseToUnicode("Enter your expression. :pencil2: \nAvailable operations:\n" +
        "[+] addition\n[-] subtraction\n[*] multiplication\n[/] division\n[^] exponentiation\n[()] brackets");

    public static final String ENTER_CITY = EmojiParser
            .parseToUnicode("Enter your city :cityscape:");

    public static final String COMMAND_NOT_AVAILABLE = EmojiParser
            .parseToUnicode("Such a command is not yet available! :confused:");

    public static final String CREATED_BY = EmojiParser
            .parseToUnicode("The bot is created by @isthatkirill :revolving_hearts:");

    public static final String INTRO = EmojiParser
            .parseToUnicode(", nice too meet уou! What do you want to do? :eyes:");

    public static final String START = "Start the bot!";

    public static final String WHAT_DO_U_DO = EmojiParser
            .parseToUnicode("What do you want to do? :eyes:");

    public static final List<String> COMMANDS = List.of("/start", "/weather", "/calculator",
            "Go back", "/about");

}
