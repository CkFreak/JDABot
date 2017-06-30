package weather;

import net.dv8tion.jda.core.entities.Message;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timbo on 30.06.17.
 * This Service will use a WeatherClient to query OpenWeather Map for the requested
 */
public class WeatherService
{
    private static final String COMPLETE = "complete";

    private static final String TEMPERATURE = "temperature";

    private static final String WIND = "wind";

    private WeatherClient _client;

    private Map<String, Integer> _queryTimes;

    private Map<String, Message> _weatherData;

    public WeatherService()
    {
        _client = new WeatherClient();
        _queryTimes = new HashMap<>();
        _weatherData = new HashMap<>();
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
        if (_queryTimes.containsKey(location + COMPLETE))
        {
            if (_queryTimes.get(location + COMPLETE) == new Date().getHours())
            {
                return _weatherData.get(location + COMPLETE);
            }
            else
            {
                _queryTimes.remove(location + COMPLETE);
            }
        }

        Message msg= _client.getCompleteFormattedWeatherFor(location);
        _weatherData.put(location + COMPLETE, msg);
        _queryTimes.put(location + COMPLETE, new Date().getHours());

        return msg;
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
        if (_queryTimes.containsKey(location + TEMPERATURE))
        {
            if (_queryTimes.get(location + TEMPERATURE) == new Date().getHours())
            {
                return _weatherData.get(location + TEMPERATURE);
            }
            else
            {
                _queryTimes.remove(location + TEMPERATURE);
            }
        }

        Message msg= _client.getTemperatureDataFor(location + TEMPERATURE);
        _weatherData.put(location + TEMPERATURE, msg);
        _queryTimes.put(location + TEMPERATURE, new Date().getHours());

        return msg;
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
        if (_queryTimes.containsKey(location + WIND))
        {
            if (_queryTimes.get(location + WIND) == new Date().getHours())
            {
                return _weatherData.get(location + WIND);
            }
            else
            {
                _queryTimes.remove(location + WIND);
            }
        }

        Message msg= _client.getWindDataFor(location + WIND);
        _weatherData.put(location + WIND, msg);
        _queryTimes.put(location + WIND, new Date().getHours());

        return msg;
    }
}