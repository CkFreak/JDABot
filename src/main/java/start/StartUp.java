package start;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;

import commands.CommandListener;
import listener.MessageListener;
import listener.ReadyListener;
import listener.UserPromotedListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import utils.Log;
import weather.WeatherClient;
import weather.WeatherService;

/**
 * Starts the bot and all its instances up
 * @version 06.2017
 */
public class StartUp
{
    private final static String PATH_TO_TOKEN = "src/main/res/tokens.txt";
    private final static String MCI_TOKEN = getToken(0);
    private final static String MOGE_TOKEN = getToken(1);
    private final static String AQUILA_TOKEN = getToken(2);
    private final static String UNICORN_TOKEN = getToken(3);
    private final static String[] GAMES = {"Hello Kitty Online", "DOTA 2", "StarCraft II", "League of Legends", "The Stainly Parable",
    "an sich rum", "mit deinen Gefühlen", "an der Steckdose"};

    public static void main(String[] args)
    {
        JDA mciJda;
        JDA mogeJDA;
        JDA aquilaJDA;
        JDA unicornJDA;
        Random random = new Random();
        WeatherService weather = new WeatherService();

        try
        {
                mciJda = new JDABuilder(AccountType.BOT).setToken(MCI_TOKEN).buildBlocking();
                mciJda.addEventListener(new ReadyListener());
                mciJda.addEventListener(new CommandListener(mciJda));
                mciJda.addEventListener(new UserPromotedListener());
                mciJda.addEventListener(new MessageListener());
                mciJda.getPresence().setGame(Game.of(GAMES[random.nextInt(GAMES.length - 1)]));

                mogeJDA = new JDABuilder(AccountType.BOT).setToken(MOGE_TOKEN).buildBlocking();
                mogeJDA.addEventListener(new ReadyListener());
                mogeJDA.addEventListener(new CommandListener(mogeJDA));
                mogeJDA.addEventListener(new UserPromotedListener());
                mogeJDA.addEventListener(new MessageListener());
                mogeJDA.getPresence().setGame(Game.of(GAMES[random.nextInt(GAMES.length - 1)]));

                aquilaJDA = new JDABuilder(AccountType.BOT).setToken(AQUILA_TOKEN).buildBlocking();
                aquilaJDA.addEventListener(new ReadyListener());
                aquilaJDA.addEventListener(new CommandListener(aquilaJDA));
                aquilaJDA.addEventListener(new UserPromotedListener());
                aquilaJDA.addEventListener(new MessageListener());
                aquilaJDA.getPresence().setGame(Game.of(GAMES[random.nextInt(GAMES.length - 1)]));

                unicornJDA = new JDABuilder(AccountType.BOT).setToken(UNICORN_TOKEN).buildBlocking();
                unicornJDA.addEventListener(new ReadyListener());
                unicornJDA.addEventListener(new CommandListener(unicornJDA));
                unicornJDA.addEventListener(new UserPromotedListener());
                unicornJDA.addEventListener(new MessageListener());
                unicornJDA.getPresence().setGame(Game.of(GAMES[random.nextInt(GAMES.length - 1)]));


        }
        catch (LoginException | IllegalArgumentException
                | InterruptedException | RateLimitedException e)
        {
            System.out.println("An Exception during the startup process has been raised check internet connection!");
            e.printStackTrace();
        }
    }

    private static String getToken(int position)
    {
        try
        {
            List<String> readAllLines = Files.readAllLines(Paths.get(PATH_TO_TOKEN));
            return readAllLines.get(position);
        }
        catch (IOException e)
        {
            Log.warning("Not all or no Bot tokens could be fetched! IOException raised");
        }
        return null;
    }
}
