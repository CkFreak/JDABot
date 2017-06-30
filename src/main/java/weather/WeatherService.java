package weather;

import java.io.IOException;

/**
 * Created by Timbo on 30.06.17.
 * This Service will use a WeatherClient to query OpenWeather Map for the requested
 */
public class WeatherService
{
    private WeatherClient _client;

    public WeatherService()
    {
        _client = new WeatherClient();
        try
        {
            _client.getWeatherDataFor("Hamburg");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
