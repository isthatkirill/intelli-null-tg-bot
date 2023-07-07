package isthatkirill.IntelliNullBot.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.nfunk.jep.JEP;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalculatorService {
    public String calculate(String expression) {
        JEP jep = new JEP();
        jep.parseExpression(expression);
        if (jep.hasError()) {
            log.warn("[calculator] Invalid expression {}. Please try again.", expression);
            return "Invalid expression. Please try again.";
        }
        return String.valueOf(jep.getValue());
    }

}