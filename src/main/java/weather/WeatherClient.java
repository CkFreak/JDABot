package weather;

import utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Timbo on 30.06.17.
 * This class queries OpenWeatherMap for the Weather Data on the requested location
 */
public class WeatherClient
{
    /**
     * The API Key for OpenWeatherMap
     */
    private static final String API_KEY = getApiKey(0);

    /**
     * The Path to the key file
     */
    private static final String PATH_TO_KEY = "src/main/res/apikey.txt";

    /**
     * The Base URL to query OpenWeatherMap's API
     */
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    /**
     * Constructs a new WeatherClient
     */public WeatherClient()
    {
    }

    public String getWeatherDataFor(String location) throws IOException
    {
        URL weatherUrl = new URL(BASE_URL + "weather?q=" + location + "&APPID=" + API_KEY);

        HttpURLConnection connection = (HttpURLConnection) weatherUrl.openConnection();

        connection.setRequestMethod("POST");

        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();

        Log.info("Response Code from OpenWeatherMap: " + responseCode);

        BufferedReader in = new BufferedReader(
		        new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		Log.info("Response from the Server was: " + response.toString());

		return null;


    }


    /**
     * Returns the API Key form the File specified in the field of this class
     * @param position The position in the list that contains the key you are looking for
     * @return A String with the API Key in it
     */
    private static String getApiKey(int position)
    {
        try
        {
            List<String> readAllLines = Files.readAllLines(Paths.get(PATH_TO_KEY));
            return readAllLines.get(position);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.warning("The API Key for OpenWeatherMap could not be loaded!");
        }
        return null;
    }
}
