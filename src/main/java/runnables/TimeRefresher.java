package runnables;

import java.util.Calendar;
import java.util.Date;

/**
 * This class starts a new Thread to refresh the time constantly
 * @author Timbo
 * @version 05.2017
 */
public class TimeRefresher implements Runnable 
{
    private int _sleepTime;
    
    /**
     * Initializes a new TimeRefresher
     */
    public TimeRefresher(int sleepTime)
    {
        _sleepTime = sleepTime;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(_sleepTime);
        }
        catch (InterruptedException e)
        {
            System.out.println("The game has ended");
        }
    }

}
