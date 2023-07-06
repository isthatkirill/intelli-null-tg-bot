package isthatkirill.IntelliNullBot.bot.service;

import isthatkirill.IntelliNullBot.bot.util.HttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.text.DecimalFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final HttpService httpService;

    public String getWeather(String city) {
        String response;
        try {
            response =
                    httpService.makeHttpRequest("http://api.weatherapi.com/v1/" +
                            "forecast.json?key=b9a4ed35b7e0422a906153641230607&q=" + city + "&days=3&aqi=no&alerts=no");
            return parse(response);
        } catch (HttpClientErrorException e) {
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

            sb.append("\nDate: ").append(forecastDayObject.getString("date"));
            sb.append("\nTemperature (°C): ").append(dayObject.getDouble("avgtemp_c"));
            sb.append("\nWind speed (mps): ").append(decimalFormat.format(dayObject.getDouble("maxwind_kph") / 3.6));
            sb.append("\nVisibility (km): ").append(dayObject.getDouble("avgvis_km"));
            sb.append("\nHumidity (%): ").append(dayObject.getDouble("avghumidity"));
            sb.append("\nUV Index: ").append(dayObject.getDouble("uv"));
            sb.append("\nCondition: ").append(dayObject.getJSONObject("condition").getString("text"));
            sb.append("\n-----------------------------------");
        }

        return sb.toString();
    }

}
