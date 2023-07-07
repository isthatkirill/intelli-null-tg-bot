package isthatkirill.IntelliNullBot.bot.service;

import isthatkirill.IntelliNullBot.bot.model.BotCall;
import isthatkirill.IntelliNullBot.bot.model.User;
import isthatkirill.IntelliNullBot.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void save(Message message) {
        if (userRepository.checkIfRegistered(message.getChatId())) {
            log.info("[user-db] User {} already registered", message.getChat().getUserName());
            return;
        }
        Long id = userRepository.save(buildUser(message));
        log.info("[user-db] User {} added to the db with id={}", message.getChat().getUserName(), id);
    }

    public void saveCall(String command, String text, Long chatId) {
        log.info("[user-db] User with chatId={} called command {} with arg={}", chatId, command, text);
        userRepository.saveCall(buildCall(command, text, chatId));
    }

    public String getCallsByChatId(Long chatId) {
        List<BotCall> calls = userRepository.findCallsByChatId(chatId);
        log.info("[user-db] User with chatId={} requested his calls history", chatId);
        return calls.isEmpty() ? "History is empty!" : formatBotCalls(calls);
    }

    private BotCall buildCall(String command, String text, Long chatId) {
        return BotCall.builder()
                .command(command)
                .chatId(chatId)
                .text(text)
                .calledAt(LocalDateTime.now())
                .build();
    }

    private User buildUser(Message message) {
        return User.builder()
                .chatId(message.getChatId())
                .userName(message.getChat().getUserName())
                .firstName(message.getChat().getFirstName())
                .lastName(message.getChat().getLastName())
                .registered(LocalDateTime.now())
                .build();
    }

    private String formatBotCalls(List<BotCall> calls) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < calls.size(); i++) {
            BotCall call = calls.get(i);
            sb.append(i + 1).append(". ").append("Called command [").append(call.getCommand()).append("] with value [")
                    .append(call.getText()).append("] on ").append(call.getCalledAt().format(formatter)).append("\n");
        }
        return sb.toString();
    }



}
