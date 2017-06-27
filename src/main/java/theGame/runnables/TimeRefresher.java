package theGame.runnables;

import java.util.Observable;

/**
 * This class starts a new Thread to refresh the time constantly
 * @author Timbo
 * @version 05.2017
 */
public class TimeRefresher extends Observable implements Runnable
{
    private int _sleepTime;
    
    /**
     * Initializes a new TimeRefresher
     */
    public TimeRefresher(int sleepTime)
    {
        _sleepTime = Math.abs(sleepTime);
        System.out.println(sleepTime/3600000);
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(Math.abs(_sleepTime));
            setChanged();
            notifyObservers();
        }
        catch (InterruptedException e)
        {
            System.out.println("The game has ended");
        }
    }

}
