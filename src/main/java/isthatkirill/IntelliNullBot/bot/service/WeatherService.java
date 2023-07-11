package isthatkirill.IntelliNullBot.bot.service;

import com.vdurmont.emoji.EmojiParser;
import isthatkirill.IntelliNullBot.bot.util.HttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.text.DecimalFormat;

import static isthatkirill.IntelliNullBot.bot.util.StringConstants.EMOJI;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final HttpService httpService;
    private static final String API_KEY = "b9a4ed35b7e0422a906153641230607";

    public String getWeather(String city) {
        String response;
        try {
            response =
                    httpService.makeHttpRequest("http://api.weatherapi.com/v1/" +
                            "forecast.json?key=" + API_KEY + "&q=" + city + "&days=3&aqi=no&alerts=no");
            return parse(response);
        } catch (HttpClientErrorException e) {
            log.warn("[weather] There is no such city or API doesnt response");
            response = "There is no such city";
            return response;
        }
    }

    private String parse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject forecastObject = jsonObject.getJSONObject("forecast");
        JSONObject locationObject = jsonObject.getJSONObject("location");
        JSONArray forecastDayArray = forecastObject.getJSONArray("forecastday");
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        StringBuilder sb = new StringBuilder();

        sb.append("Forecast for the city ").append(locationObject.getString("name"));

        for (int i = 0; i < forecastDayArray.length(); i++) {
            JSONObject forecastDayObject = forecastDayArray.getJSONObject(i);
            JSONObject dayObject = forecastDayObject.getJSONObject("day");

            sb.append("\n:calendar:Date: ").append(forecastDayObject.getString("date"));
            sb.append("\n:temperature:Temperature (°C): ").append(dayObject.getDouble("avgtemp_c"));
            sb.append("\n:dash:Wind speed (mps): ").append(decimalFormat.format(dayObject.getDouble("maxwind_kph") / 3.6));
            sb.append("\n:eye:Visibility (km): ").append(dayObject.getDouble("avgvis_km"));
            sb.append("\n:droplet:Humidity (%): ").append(dayObject.getDouble("avghumidity"));
            sb.append("\n:hotsprings:UV Index: ").append(dayObject.getDouble("uv"));

            String condition = dayObject.getJSONObject("condition").getString("text");

            sb.append("\n").append(EMOJI.get(condition)).append("Condition: ").append(condition);
            sb.append("\n-----------------------------------");
        }

        return EmojiParser.parseToUnicode(sb.toString());
    }

}
