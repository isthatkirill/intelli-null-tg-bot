package isthatkirill.IntelliNullBot.service;

import isthatkirill.IntelliNullBot.config.BotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();


            switch (messageText) {
                case "/start":
                    startReceived(update);
                    break;
                default:
                    executeMessage(chatId, "Такая команда пока недоступна!");
            }


        }

    }

    private void startReceived(Update update) {
        String answer = "Привет, " + update.getMessage().getChat().getUserName();
        executeMessage(update.getMessage().getChatId(), answer);
    }

    private void executeMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}
