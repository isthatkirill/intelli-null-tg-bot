package isthatkirill.IntelliNullBot.bot.aspect;

import isthatkirill.IntelliNullBot.bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MethodCallsAspect {

    private final UserService userService;

    @Pointcut("@annotation(trackMethodCall) && args(text, id)")
    public void trackMethodCallPointcut(TrackMethodCall trackMethodCall, String text, Long id) {
    }

    @After("trackMethodCallPointcut(trackMethodCall, text, id)")
    public void afterMethodCall(JoinPoint joinPoint, TrackMethodCall trackMethodCall, String text, Long id) {
        userService.saveCall(trackMethodCall.value(), text, id);
    }

}
