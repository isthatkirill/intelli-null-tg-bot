package isthatkirill.IntelliNullBot.bot.service;

import isthatkirill.IntelliNullBot.bot.aspect.TrackMethodCall;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static isthatkirill.IntelliNullBot.bot.util.StringConstants.LANGUAGES_CODES;

@Slf4j
@Service
public class TranslatorService {

    @SneakyThrows
    private String requestTranslation(String text, String lang) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", text);
        requestBody.put("source", LANGUAGES_CODES.get(lang.split("-")[0]));
        requestBody.put("target", LANGUAGES_CODES.get(lang.split("-")[1]));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.nlpcloud.io/v1/nllb-200-3-3b/translation"))
                .header("Authorization", "Token 08dbcac06195bf9b758b383d6a9c9bfad9b002b8")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());
        return json.getString("translation_text");
    }

    @TrackMethodCall(value = "/translate", argNames = {"text", "chatId"})
    public String translate(String text, String lang, Long chatId) {
        log.info("[translator] User with chatId={} wants translate text={} into lang={}", chatId, text, lang);
        return requestTranslation(text, lang);
    }
}
