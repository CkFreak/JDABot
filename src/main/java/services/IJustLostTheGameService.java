package services;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Random;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import runnables.TimeRefresher;

/**
 * A class that abstracts the Game so it can be played by a computer. In Random intervals of at most 4 days 
 * the game will be lost
 * @author Timbo
 * @version 11.2016
 */
public class IJustLostTheGameService extends Observable
{
    private static final String DAMMIT_I_JUST_LOST_THE_GAME = "Dammit I just lost the Game!";
    private static final int[] RANDOM_TIMES = {48, 72, 96, 168};
    private Calendar _now;
    private Date _nextGameLoss;
    private Random _random;
    private Thread _timeRefresher;
    private TimeRefresher _currentTime;
    private Thread _gameThread;

    /**
     * Initializes a new IJustLostTheGameService that collects all neccesarry infomration
     */
    public IJustLostTheGameService()
    {
        _currentTime = new TimeRefresher();
        _timeRefresher = new Thread(_currentTime);
        _timeRefresher.setDaemon(true);
        _timeRefresher.start();
        _now = _currentTime.getCalenderInstace();
        _random = new Random();
        _now.setTime(_currentTime.getDateInstance());
        _now.add(Calendar.HOUR_OF_DAY, RANDOM_TIMES[_random.nextInt(4)]);
        _nextGameLoss = _now.getTime();
    }

    /**
     * Prepares a loss of the Game
     * @param event The MessagereceivedEvent to get the public Server channel
     */
    public void executeGameLoss(MessageReceivedEvent event)
    {
        Guild guild = event.getGuild();
        TextChannel channel = guild.getPublicChannel();

        letTheGameBeLost(channel);

    }

    /**
     * Sends a message to the servers public channel to make all players lose the game
     * @param channel the channel the message shall be send to 
     */
    private void letTheGameBeLost(TextChannel channel)
    {
        _gameThread = new Thread()
        {
            @SuppressWarnings("static-access")
            @Override
            public void run()
            {
                try
                {
                    _gameThread.sleep(1000);
                    _now = _currentTime.getCalenderInstace();
                    _now.setTime(_currentTime.getDateInstance());
                    while (!_now.getTime()
                        .after(_nextGameLoss))
                    {
                        if (_now.getTime()
                            .after(_nextGameLoss))
                        {
                            channel.sendMessage(DAMMIT_I_JUST_LOST_THE_GAME);
                            restartGame();
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    System.out.println("The Game thread has been interrupted");
                    e.printStackTrace();
                }
            }
        };
        _gameThread.setDaemon(true);
        _gameThread.start();

    }

    /**
     * Restarts the game after it has been lost
     */
    private void restartGame()
    {
        _timeRefresher.interrupt();
        _now = _currentTime.getCalenderInstace();
        _now.setTime(_currentTime.getDateInstance());
        _now.add(Calendar.HOUR_OF_DAY, RANDOM_TIMES[_random.nextInt(4)]);
        _nextGameLoss = _now.getTime();
        _timeRefresher = new Thread(_currentTime);
        _timeRefresher.setDaemon(true);
        _timeRefresher.start();

        this.setChanged();
        this.notifyObservers();
        System.out.println("A new Game has been started!");
    }

    public Date getNextGameLoss()
    {
        return _nextGameLoss;
    }

    public Date getNow()
    {
        _now = _currentTime.getCalenderInstace();
        return _now.getTime();
    }
}
