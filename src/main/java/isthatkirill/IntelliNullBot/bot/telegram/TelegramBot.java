package isthatkirill.IntelliNullBot.bot.telegram;

import com.vdurmont.emoji.EmojiParser;
import isthatkirill.IntelliNullBot.bot.config.BotConfig;
import isthatkirill.IntelliNullBot.bot.model.BotState;
import isthatkirill.IntelliNullBot.bot.service.CalculatorService;
import isthatkirill.IntelliNullBot.bot.service.TranslatorService;
import isthatkirill.IntelliNullBot.bot.service.UserService;
import isthatkirill.IntelliNullBot.bot.service.WeatherService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static isthatkirill.IntelliNullBot.bot.model.BotState.*;
import static isthatkirill.IntelliNullBot.bot.util.StringConstants.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CalculatorService calculatorService;

    @Autowired
    private TranslatorService translatorService;

    private final BotConfig botConfig;
    private final Map<Long, BotState> userStates = new HashMap<>();
    private final Map<Long, String> textToTranslate = new HashMap<>();

    @SneakyThrows
    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", START));

        this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = EmojiParser.parseToAliases(update.getMessage().getText());
            Long chatId = update.getMessage().getChatId();
            Message message = update.getMessage();

            BotState currentState = getUserState(chatId);

            switch (currentState) {
                case DEFAULT:
                    if ("/start".equals(messageText)) {
                        startReceived(message);
                    } else if ("Weather :white_sun_behind_cloud_rain:".equals(messageText)) {
                        weatherReceived(message);
                    } else if ("Calculator :1234:".equals(messageText)) {
                        calculatorReceived(message);
                    } else if ("About :question:".equals(messageText)) {
                        aboutReceived(message);
                    } else if ("History :telescope:".equals(messageText)) {
                        historyReceived(message);
                    } else if ("Translate :page_facing_up:".equals(messageText)) {
                        translateReceived(message);
                    } else {
                        notSupportedReceived(message);
                    }
                    break;
                case WAITING_CITY:
                    if (COMMANDS.contains(messageText)) {
                        goBackReceived(message);
                        break;
                    }
                    getWeather(message);
                    break;
                case WAITING_EXPRESSION:
                    if (COMMANDS.contains(messageText)) {
                        goBackReceived(message);
                        break;
                    }
                    solveExpression(message);
                    break;
                case WAITING_TEXT:
                    if (COMMANDS.contains(messageText)) {
                        goBackReceived(message);
                        textToTranslate.remove(chatId);
                        break;
                    }
                    textToTranslate.put(chatId, messageText);
                    inlineButtonCall(message);
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            EditMessageText editedMessage = new EditMessageText();
            editedMessage.setChatId(String.valueOf(chatId));
            editedMessage.setMessageId(messageId);

            userService.saveCall("/translate", textToTranslate.get(chatId), chatId, true);
            editedMessage.setText(translatorService.translate(textToTranslate.get(chatId), callBackData));
            textToTranslate.remove(chatId);

            executeMessage(editedMessage);
        }
    }

    private void inlineButtonCall(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText("Select a language available for translation: ");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(InlineKeyboardButton.builder().text("RUS -> ENG").callbackData("RUS-ENG").build());
        rowInline.add(InlineKeyboardButton.builder().text("ENG -> RUS").callbackData("ENG-RUS").build());

        rowsInline.add(rowInline);

        rowInline = new ArrayList<>();

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        executeMessage(sendMessage);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void solveExpression(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        log.info("[calculator] Expression received by user with chatId={}(username={})", chatId,
                message.getChat().getUserName());
        userService.saveCall("/calculate", text, chatId, true);
        sendMessage(calculatorService.calculate(text), message);
    }

    private void getWeather(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        log.info("[weather] City received by user with chatId={}(username={})", chatId,
                message.getChat().getUserName());
        userService.saveCall("/weather", text, chatId, true);
        sendMessage(weatherService.getWeather(text), message);
    }

    private void calculatorReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[pre-calculator] received by user with chatId={}(username={}), waiting for expression", chatId,
                message.getChat().getUserName());
        userService.saveCall("/calculate", " ", chatId, false);
        sendMessage(ENTER_EXPRESSION, message);
        setUserState(chatId, WAITING_EXPRESSION);
    }

    private void translateReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[pre-translate] received by user with chatId={}(username={}), waiting for text", chatId,
                message.getChat().getUserName());
        userService.saveCall("/translate", " ", chatId, false);
        sendMessage("Write the text that you need to translate", message);
        setUserState(chatId, WAITING_TEXT);
    }

    private void weatherReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[pre-weather] received by user with chatId={}(username={}), waiting for a city", chatId,
                message.getChat().getUserName());
        userService.saveCall("/weather", " ", chatId, false);
        sendMessage(ENTER_CITY, message);
        setUserState(chatId, WAITING_CITY);
    }

    private void goBackReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[Go back] received by user with chatId={}(username={})", chatId,
                message.getChat().getUserName());
        userService.saveCall("/go_back", " ", chatId, false);
        setUserState(chatId, BotState.DEFAULT);
        sendMessage(WHAT_DO_U_DO, message);
    }

    private void notSupportedReceived(Message message) {
        log.info("[NOT_SUPPORTED] received by user with chatId={}(username={})", message.getChatId(),
                message.getChat().getUserName());
        sendMessage(COMMAND_NOT_AVAILABLE, message);
    }

    private void aboutReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[about] received by user with chatId={}", chatId);
        userService.saveCall("/about", " ", chatId, true);
        sendMessage(userService.getInfo(), message);
    }

    private void historyReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[history] received by user with chatId={}", chatId);
        userService.saveCall("/history", " ", chatId, true);
        sendMessage(userService.getCallsByChatId(chatId), message);
    }

    private void startReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[start] received by user with chatId={}(username={})", chatId,
                message.getChat().getUserName());
        String answer = "Hello, " + message.getChat().getUserName() + INTRO;
        userService.save(message);
        userService.saveCall("/start", " ", chatId, true);
        sendMessage(answer, message);
    }

    private BotState getUserState(Long chatId) {
        BotState state = userStates.getOrDefault(chatId, BotState.DEFAULT);
        log.info("[bot-state] Bot's state for user with chatId={} now is {}", chatId, state);
        return state;
    }

    private void setUserState(Long chatId, BotState state) {
        log.info("[bot-state] Bot's state for user with chatId={} now is {}", chatId, state);
        userStates.put(chatId, state);
    }

    private void sendMessage(String textToSend, Message message) {
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        showButtons(message, sendMessage);
        executeMessage(sendMessage);
    }

    @SneakyThrows
    private void executeMessage(SendMessage message) {
        execute(message);
    }

    @SneakyThrows
    private void executeMessage(EditMessageText message) {
        execute(message);
    }

    private void showButtons(Message message, SendMessage sendMessage) {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setIsPersistent(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        String text = EmojiParser.parseToAliases(message.getText());

        if (text.equals("Weather :white_sun_behind_cloud_rain:") || text.equals("Calculator :1234:") ||
                text.equals("Translate :page_facing_up:")) {
            row.add(EmojiParser.parseToUnicode("Go back :back:"));
            keyboardRows.add(row);
        } else if (!getUserState(message.getChatId()).name().equals(DEFAULT.name())) {
            row.add(EmojiParser.parseToUnicode("Go back :back:"));
            keyboardRows.add(row);
        } else {
            row.add(EmojiParser.parseToUnicode("Weather :white_sun_behind_cloud_rain:"));
            row.add(EmojiParser.parseToUnicode("Meme :chicken:"));
            row.add(EmojiParser.parseToUnicode("Translate :page_facing_up:"));
            keyboardRows.add(row);
            row = new KeyboardRow();
            row.add(EmojiParser.parseToUnicode("Calculator :1234:"));
            row.add(EmojiParser.parseToUnicode("History :telescope:"));
            row.add(EmojiParser.parseToUnicode("About :question:"));
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);
    }
}
