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

/**
 * Starts the bot and all its instances up
 * @version 06.2017
 */
public class StartUp
{
    private final static String PATH_TO_TOKEN = "src/main/res/tokens.txt";

    public static void main(String[] args)
    {
        JDA jda;
        try
        {

            for (String string  : Files.readAllLines(Paths.get(PATH_TO_TOKEN)))
            {
                jda = new JDABuilder(AccountType.BOT).setToken(string).buildBlocking();
                jda.addEventListener(new ReadyListener());
                jda.addEventListener(new CommandListener(jda));
                jda.addEventListener(new UserPromotedListener());
                jda.addEventListener(new MessageListener());
                jda.getPresence().setGame(Game.of("Hello Kitty Online"));
            }

        }
        catch (LoginException | IllegalArgumentException
                | InterruptedException | RateLimitedException e)
        {
            System.out.println("An Exception during the startup process has been raised check internet connection!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
