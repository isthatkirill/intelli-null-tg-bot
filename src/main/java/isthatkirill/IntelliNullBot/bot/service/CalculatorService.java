package isthatkirill.IntelliNullBot.bot.service;

import isthatkirill.IntelliNullBot.bot.aspect.TrackMethodCall;
import lombok.extern.slf4j.Slf4j;
import org.nfunk.jep.JEP;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalculatorService {

    @TrackMethodCall(value = "/calculator", argNames = {"expression", "chatId"})
    public String calculate(String expression, Long chatId) {
        JEP jep = new JEP();
        jep.parseExpression(expression);
        if (jep.hasError()) {
            log.warn("[calculator] Invalid expression {}. Please try again.", expression);
            return "Invalid expression. Please try again.";
        }
        return expression + " = " + jep.getValue();
    }

}