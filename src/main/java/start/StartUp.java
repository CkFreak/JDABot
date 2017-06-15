package start;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.security.auth.login.LoginException;

import listener.CommandListener;
import listener.MessageListener;
import listener.ReadyListener;
import listener.UserPromotedListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class StartUp
{
    private final static String MCI_SERVER_TOKEN = provideToken(0);
    private final static String MOGE_SERVER_TOKEN = provideToken(1);
    private final static String AQUILA_SERVER = provideToken(2);
    private final static String UNICORNPORN_SERVER = provideToken(3);
    private final static String PATH_TO_TOKEN = "src/main/res/tokens.txt";

    public static void main(String[] args)
    {
        JDA mciJda;
        JDA mogeJda;
        JDA aquilaJda;
        JDA unicornJda;
        try
        {

            //Build the MCI JDA instance
            mciJda = new JDABuilder(AccountType.BOT).setToken(MCI_SERVER_TOKEN).buildBlocking();
            mciJda.addEventListener(new ReadyListener());
            mciJda.addEventListener(new CommandListener(mciJda));
            mciJda.addEventListener(new UserPromotedListener());
            mciJda.addEventListener(new MessageListener());
            mciJda.getPresence().setGame(Game.of("Hello Kitty Online"));

            //Build the moge JDA instance
            mogeJda = new JDABuilder(AccountType.BOT).setToken(MOGE_SERVER_TOKEN).buildBlocking();
            mogeJda.addEventListener(new ReadyListener());
            mogeJda.addEventListener(new CommandListener(mogeJda));
            mogeJda.addEventListener(new UserPromotedListener());
            mogeJda.addEventListener(new MessageListener());
            mogeJda.getPresence().setGame(Game.of("DOTA 2"));

            //Build the aquila JDA instance
            aquilaJda = new JDABuilder(AccountType.BOT).setToken(AQUILA_SERVER).buildBlocking();
            aquilaJda.addEventListener(new ReadyListener());
            aquilaJda.addEventListener(new CommandListener(aquilaJda));
            aquilaJda.addEventListener(new UserPromotedListener());
            aquilaJda.addEventListener(new MessageListener());
            aquilaJda.getPresence().setGame(Game.of("League of Legends"));

            //Build the unicorn JDA instance
            unicornJda = new JDABuilder(AccountType.BOT).setToken(UNICORNPORN_SERVER).buildBlocking();
            unicornJda.addEventListener(new ReadyListener());
            unicornJda.addEventListener(new CommandListener(unicornJda));
            unicornJda.addEventListener(new UserPromotedListener());
            unicornJda.addEventListener(new MessageListener());
            unicornJda.getPresence().setGame(Game.of("Ben and Ed Bloodparty"));




        }
        catch (LoginException | IllegalArgumentException
                | InterruptedException | RateLimitedException e)
        {
            System.out.println("An Exception during the startup process has been raised check internet connection!");
            e.printStackTrace();
        }
    }

    /**
     * Returns the Token for a bot from a file located in the specified location
     * @param count Specifies which token you want (in case there is more than one)
     * @return The bot token corresponding to the count
     */
    private static String provideToken(int count)
    {
        try
        {
            List<String> readAllLines = Files.readAllLines(Paths.get(PATH_TO_TOKEN));
            return readAllLines.get(count);
        }
        catch (IOException e)
        {
            System.out.println("Unfortunately there was a permission problem with the file you specified for the Bot Token :(");
            e.printStackTrace();
        }
        return null;
    }
}
