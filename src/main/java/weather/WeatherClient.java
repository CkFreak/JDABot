package weather;

import utils.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Timbo on 30.06.17.
 * This class queries OpenWeatherMap for the Weather Data on the requested location
 */
public class WeatherClient
{
    private static final String API_KEY = getApiKey(0);
    private static final String PATH_TO_KEY = "/src/main/res/weatherKey.txt";

    /**
     * The city that is to be queried at OpenWeatherMap
     */
    private String _city;

    /**
     * Constructs a new WeatherClient
     * @param location The Location that weather data is to be aquired for.
     */
    public WeatherClient(String location)
    {
        _city = location;
    }


    private static String getApiKey(int position)
    {
        try
        {
            List<String> readAllLines = Files.readAllLines(Paths.get(PATH_TO_KEY));
            return readAllLines.get(position);
        }
        catch (IOException e)
        {
            Log.warning("The API Key for OpenWeatherMap could not be loaded!");
        }
        return null;
    }
}
