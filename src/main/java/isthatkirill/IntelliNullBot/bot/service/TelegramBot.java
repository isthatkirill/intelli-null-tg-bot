package isthatkirill.IntelliNullBot.bot.service;

import isthatkirill.IntelliNullBot.bot.config.BotConfig;
import isthatkirill.IntelliNullBot.bot.model.BotState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    private final BotConfig botConfig;

    private Map<Long, BotState> userStates = new HashMap<>();
    List<String> commands = List.of("/start", "/weather", "/calculator",
            "Go back", "/about");

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", START));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Message message = update.getMessage();

            BotState currentState = getUserState(chatId);

            switch (currentState) {
                case DEFAULT:
                    if ("/start".equals(messageText)) {
                        startReceived(message);
                    } else if ("/weather".equals(messageText)) {
                        weatherReceived(message);
                    } else if ("/calculator".equals(messageText)) {
                        calculatorReceived(message);
                    } else if ("/about".equals(messageText)) {
                        aboutReceived(message);
                    } else {
                        notSupportedReceived(message);
                    }
                    break;
                case WAITING_CITY:
                    if (commands.contains(messageText)) {
                        goBackReceived(message);
                        break;
                    }
                    getWeather(message);
                    break;
                case WAITING_EXPRESSION:
                    if (commands.contains(messageText)) {
                        goBackReceived(message);
                        break;
                    }
                    solveExpression(message);
                    break;
            }
        }
    }

    private void goBackReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[Go back] received by user with chatId={}(username={})", chatId,
                message.getChat().getUserName());
        setUserState(chatId, BotState.DEFAULT);
        sendMessage(WHAT_DO_U_DO, message);
    }

    private void solveExpression(Message message) {
        Long chatId = message.getChatId();
        log.info("[calculator] Expression received by user with chatId={}(username={})", chatId,
                message.getChat().getUserName());
        sendMessage(calculatorService.calculate(message.getText()), message);
    }

    private void calculatorReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[pre-calculator] received by user with chatId={}(username={}), waiting for expression",chatId,
                message.getChat().getUserName());
        sendMessage( ENTER_EXPRESSION, message);
        setUserState(chatId, WAITING_EXPRESSION);
    }

    public void weatherReceived(Message message) {
        Long chatId = message.getChatId();
        log.info("[pre-weather] received by user with chatId={}(username={}), waiting for a city", chatId,
                message.getChat().getUserName());
        sendMessage(ENTER_CITY, message);
        setUserState(chatId, WAITING_CITY);
    }

    private void notSupportedReceived(Message message) {
        log.info("[NOT_SUPPORTED] received by user with chatId={}(username={})", message.getChatId(),
                message.getChat().getUserName());
        sendMessage( COMMAND_NOT_AVAILABLE, message);
    }

    private void getWeather(Message message) {
        Long chatId = message.getChatId();
        log.info("[weather] City received by user with chatId={}(username={})", chatId,
                message.getChat().getUserName());
        sendMessage( weatherService.getWeather(message.getText()), message);
    }

    private void aboutReceived(Message message) {
        log.info("[about] received by user with chatId={}", message.getChatId());
        sendMessage( CREATED_BY, message);
    }
    private void startReceived(Message message) {
        log.info("[start] received by user with chatId={}(username={})", message.getChatId(),
                message.getChat().getUserName());
        String answer = "Hello, " + message.getChat().getUserName() + INTRO;
        userService.save(message);
        sendMessage( answer, message);
    }

    private BotState getUserState(Long chatId) {
        log.info("Bot's state for user with chatId={} now is {}", chatId, BotState.DEFAULT);
        return userStates.getOrDefault(chatId, BotState.DEFAULT);
    }

    private void setUserState(Long chatId, BotState state) {
        log.info("Bot's state for user with chatId={} now is {}", chatId, state);
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

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void showButtons(Message message, SendMessage sendMessage) {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setIsPersistent(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        String text = message.getText();

        if (text.equals("/weather") || text.equals("/calculator")) {
            row.add("Go back");
            keyboardRows.add(row);
        } else if (!getUserState(message.getChatId()).name().equals(DEFAULT.name())) {
            row.add("Go back");
            keyboardRows.add(row);
        } else {
            row.add("/weather");
            row.add("/meme");
            row.add("/translate");
            keyboardRows.add(row);
            row = new KeyboardRow();
            row.add("/calculator");
            row.add("/about");
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
