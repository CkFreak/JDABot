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
    private TimeRefresher _currentTimeRunnable;
    private Thread _gameThread;

    /**
     * Initializes a new IJustLostTheGameService that collects all neccesarry infomration
     */
    public IJustLostTheGameService()
    {
        _currentTimeRunnable = new TimeRefresher();
        _timeRefresher = new Thread(_currentTimeRunnable);
        _timeRefresher.setDaemon(true);
        _timeRefresher.setName("TIME REFRESHER");
        _timeRefresher.start();
        _now = _currentTimeRunnable.getCalenderInstace();
        _random = new Random();
        _now.setTime(_currentTimeRunnable.getDateInstance());
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
        _gameThread = new Thread("GAME THREAD")
        {
            @SuppressWarnings("static-access")
            @Override
            public void run()
            {
                try
                {
                    _gameThread.sleep(1000);
                    _now = _currentTimeRunnable.getCalenderInstace();
                    _now.setTime(_currentTimeRunnable.getDateInstance());
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
                    System.out.println(Thread.currentThread().getName() +  " has been interrupted");
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
        _now = _currentTimeRunnable.getCalenderInstace();
        _now.setTime(_currentTimeRunnable.getDateInstance());
        _now.add(Calendar.HOUR_OF_DAY, RANDOM_TIMES[_random.nextInt(4)]);
        _nextGameLoss = _now.getTime();
        _timeRefresher = new Thread(_currentTimeRunnable);
        _timeRefresher.setDaemon(true);
        _timeRefresher.setName("TIME REFRESHER");
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
        _now = _currentTimeRunnable.getCalenderInstace();
        return _now.getTime();
    }
}
