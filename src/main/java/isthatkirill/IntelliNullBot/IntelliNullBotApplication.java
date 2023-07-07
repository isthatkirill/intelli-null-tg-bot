package isthatkirill.IntelliNullBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class IntelliNullBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliNullBotApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
