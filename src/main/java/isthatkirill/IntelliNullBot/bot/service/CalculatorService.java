package isthatkirill.IntelliNullBot.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Stack;

@Slf4j
@Service
public class CalculatorService {

    public Map<String, Double> evaluateExpression(String expression) {
        String answer;

        try {
            if (expression.isEmpty()) {
                answer = "Expression is empty";
                log.info(answer);
                return Map.of(answer, 0D);
            }

            Stack<Double> numbers = new Stack<>();
            Stack<Character> operators = new Stack<>();

            for (int i = 0; i < expression.length(); i++) {
                char ch = expression.charAt(i);

                if (ch == ' ')
                    continue;

                if (Character.isDigit(ch)) {
                    StringBuilder number = new StringBuilder();

                    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        number.append(expression.charAt(i));
                        i++;
                    }

                    numbers.push(Double.parseDouble(number.toString()));
                    i--;

                } else if (ch == '(') {
                    operators.push(ch);

                } else if (ch == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
                        numbers.push(result);
                    }

                    if (!operators.isEmpty() && operators.peek() == '(') {
                        operators.pop();
                    } else {
                        answer = "Incorrect expression. Check the placement of brackets.";
                        log.info(answer);
                        return Map.of(answer, 0D);
                    }

                } else if (isOperator(ch)) {
                    while (!operators.isEmpty() && hasHigherPriority(ch, operators.peek())) {
                        double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
                        numbers.push(result);
                    }

                    operators.push(ch);
                } else {
                    answer = "Invalid character: " + ch;
                    log.info(answer);
                    return Map.of(answer, 0D);
                }
            }

            while (!operators.isEmpty()) {
                double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
                numbers.push(result);
            }


            return Map.of("", numbers.pop());
        } catch (Exception e) {
            answer = "Invalid expression. Check the correctness of the source data.";
            log.info(answer);
            return Map.of(answer, 0D);
        }
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
    }

    private static boolean hasHigherPriority(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        else if ((op1 == '^') && (op2 == '*' || op2 == '/'))
            return false;
        else if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    private double applyOperation(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    log.info("Division by zero is not possible");
                    return 0;
                }
                return a / b;
            case '^':
                return Math.pow(a, b);
            default:
                return 0;
        }
    }

    public String calculate(String expression) {
        Map<String, Double> result = evaluateExpression(expression);
        String key = result.keySet().iterator().next();
        Double value = result.get(key);
        return key.isBlank() ? String.valueOf(value) : key;
    }

}