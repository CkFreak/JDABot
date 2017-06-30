package weather;

import net.dv8tion.jda.core.entities.Message;
import org.json.simple.parser.ParseException;

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
    }

    /**
     * Gives a complete overview over the weather conditions
     * @param location The city one want to know the weather information about
     * @return A Message Object with all the weather information
     * @throws IOException When OpenWeatherMap returns an error
     * @throws ParseException When the JSON Object cannot be parsed correctly
     */
    public Message getCompleteWeatherData(String location) throws IOException, ParseException
    {
        return _client.getCompleteFormattedWeatherFor(location);
    }

    /**
     * Gives the temperature information for a city
     * @param location The city one wants to know the temperature for
     * @return A Message Object with the temperature information
     * @throws IOException When OpenWeatherMap returns an error
     * @throws ParseException When the JSON Object cannot be parsed correctly
     */
    public Message getTemperatureData(String location) throws IOException, ParseException
    {
        return _client.getTemperatureDataFor(location);
    }

    /**
     * Gives the wind data for a city
     * @param location The city one wants to know the wind data for
     * @return A Message Object with the wind information
     * @throws IOException When OpenWeatherMap returns an error
     * @throws ParseException When the JSON Object cannot be parsed correctly
     */
    public Message getWindData(String location) throws IOException, ParseException
    {
        return _client.getWindDataFor(location);
    }
}