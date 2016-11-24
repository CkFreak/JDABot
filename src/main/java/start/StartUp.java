package start;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.security.auth.login.LoginException;

import listener.CommandListener;
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
    private final static String PATH_TO_TOKEN = "/src/main/res/tokens.txt";
    private final static String PYTHON_COMMAND = "python ./youtube-dl -U";
    
    public static void main(String[] args)
    {
        try
        {
            try
            {
                Runtime.getRuntime().exec(PYTHON_COMMAND);
                System.out.println("Looking good(YT-DL)");
            }
            catch (IOException e)
            {
                System.out.println("Unfortunatley there has been an IOException for the update of the YT-DL!");
                e.printStackTrace();
            }
            JDA mciJda;
            try
            {
                mciJda = new JDABuilder(AccountType.BOT).setToken(MCI_SERVER_TOKEN).buildBlocking();
                mciJda.addEventListener(new ReadyListener());
                mciJda.addEventListener(new CommandListener(mciJda));
                mciJda.addEventListener(new UserPromotedListener());
                ((JDABuilder) mciJda).setGame(new Game()
                {
                    
                    @Override
                    public String getUrl()
                    {
                        return "This is not a valid URL";
                    }
                    
                    @Override
                    public GameType getType()
                    {
                        return GameType.DEFAULT;
                    }
                    
                    @Override
                    public String getName()
                    {
                        return "Hello Kitty Online";
                    }
                });
                
            }
            catch (RateLimitedException e)
            {
                e.printStackTrace();
            }
            
            JDA mogeJda;
            try
            {
                mogeJda = new JDABuilder(AccountType.BOT).setToken(MOGE_SERVER_TOKEN).buildBlocking();
                mogeJda.addEventListener(new ReadyListener());
                mogeJda.addEventListener(new CommandListener(mogeJda));
                mogeJda.addEventListener(new UserPromotedListener());
                
                ((JDABuilder) mogeJda).setGame(new Game()
                {
                    
                    @Override
                    public String getUrl()
                    {
                        return "This is not a valid URL";
                    }
                    
                    @Override
                    public GameType getType()
                    {
                        return GameType.DEFAULT;
                    }
                    
                    @Override
                    public String getName()
                    {
                        return "DOTA 2";
                    }
                });
                
            }
            catch (RateLimitedException e)
            {
                e.printStackTrace();
            }
            
            

        }
        catch (LoginException | IllegalArgumentException
                | InterruptedException e)
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
