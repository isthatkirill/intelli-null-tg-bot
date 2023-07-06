package isthatkirill.IntelliNullBot.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nfunk.jep.JEP;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalculatorService {

    public String calculate(String expression) {
        JEP jep = new JEP();
        jep.parseExpression(expression);
        if (jep.hasError()) return "Invalid expression. Please try again.";
        return String.valueOf(jep.getValue());
    }

}