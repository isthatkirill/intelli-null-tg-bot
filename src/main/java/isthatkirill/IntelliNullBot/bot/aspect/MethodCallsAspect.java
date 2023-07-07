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
    public void pointcutShort(TrackMethodCall trackMethodCall, String text, Long id) {
    }

    @Pointcut("@annotation(trackMethodCall) && args(text, lang, id)")
    public void pointcutLong(TrackMethodCall trackMethodCall, String text, String lang, Long id) {
    }

    @After("pointcutShort(trackMethodCall, text, id)")
    public void afterMethodCall(JoinPoint joinPoint, TrackMethodCall trackMethodCall, String text, Long id) {
        userService.saveCall(trackMethodCall.value(), text, id);
    }

    @After("pointcutLong(trackMethodCall, text, lang, id)")
    public void afterMethodCall(JoinPoint joinPoint, TrackMethodCall trackMethodCall, String text, String lang, Long id) {
        userService.saveCall(trackMethodCall.value(), text, id);
    }

}
