package isthatkirill.IntelliNullBot.bot.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static isthatkirill.IntelliNullBot.bot.util.StringConstants.LANGUAGES_CODES;

@Slf4j
@Service
public class TranslatorService {

    private static final String token = "72abf95b687a8d2bbc8fbed66fc3a509e9bf23aa";
    // 08dbcac06195bf9b758b383d6a9c9bfad9b002b8

    @SneakyThrows
    private String requestTranslation(String text, String lang) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", text);
        requestBody.put("source", LANGUAGES_CODES.get(lang.split("-")[0]));
        requestBody.put("target", LANGUAGES_CODES.get(lang.split("-")[1]));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.nlpcloud.io/v1/nllb-200-3-3b/translation"))
                .header("Authorization", "Token " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());
        return json.getString("translation_text");
    }

    public String translate(String text, String lang) {
        log.info("[translator]  translate text={} into lang={}", text, lang);
        return requestTranslation(text, lang);
    }
}
