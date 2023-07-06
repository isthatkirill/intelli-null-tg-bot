package isthatkirill.IntelliNullBot.bot.service;

import isthatkirill.IntelliNullBot.bot.config.BotConfig;
import isthatkirill.IntelliNullBot.weather.WeatherService;
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

import static isthatkirill.IntelliNullBot.bot.service.BotState.WAITING_CITY;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;
    @Autowired
    private WeatherService weatherService;
    private final BotConfig botConfig;

    private Map<Long, BotState> userStates = new HashMap<>();

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Start the bot!"));

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
                        sendMessage( "Enter your city", message);
                        setUserState(chatId, WAITING_CITY);
                    } else if ("/about".equals(messageText)) {
                        aboutReceived(message);
                    } else {
                        sendMessage("Such a command is not yet available!", message);
                    }
                    break;
                case WAITING_CITY:
                    if ("Go back".equals(messageText) || "/start".equals(messageText)) {
                        sendMessage("What do you want to do?", message);
                        setUserState(chatId, BotState.DEFAULT);
                        break;
                    }
                    processWeather(messageText, message);
                    setUserState(chatId, BotState.DEFAULT);
                    break;
            }
        }
    }

    private void aboutReceived(Message message) {
        sendMessage( "The bot is created by @isthatkirill", message);
    }

    private void processWeather(String city, Message message) {
        sendMessage( weatherService.getWeather(city), message);
    }
    private BotState getUserState(long chatId) {
        return userStates.getOrDefault(chatId, BotState.DEFAULT);
    }

    private void setUserState(long chatId, BotState state) {
        userStates.put(chatId, state);
    }

    private void startReceived(Message message) {
        String answer = "Hello, " + message.getChat().getUserName() +
                ", nice too meet уou! What do you want to do?";
        userService.save(message);
        sendMessage( answer, message);
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

        if (text.equals("/weather")) {
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
