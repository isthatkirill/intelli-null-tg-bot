package isthatkirill.IntelliNullBot.bot.service;

import isthatkirill.IntelliNullBot.bot.model.User;
import isthatkirill.IntelliNullBot.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;


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

    private User buildUser(Message message) {
        return User.builder()
                .chatId(message.getChatId())
                .userName(message.getChat().getUserName())
                .firstName(message.getChat().getFirstName())
                .lastName(message.getChat().getLastName())
                .registered(LocalDateTime.now())
                .build();
    }



}
