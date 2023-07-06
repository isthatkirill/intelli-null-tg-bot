package isthatkirill.IntelliNullBot.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HttpService {

    private final RestTemplate restTemplate;

    public String makeHttpRequest(String url) {
        return restTemplate.getForObject(url, String.class);
    }
}
