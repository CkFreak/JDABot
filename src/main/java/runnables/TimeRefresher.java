package runnables;

import java.util.Calendar;
import java.util.Date;

/**
 * This class starts a new Thread to refresh the time constantly
 * @author Timbo
 * @version 11.2016
 */
public class TimeRefresher implements Runnable 
{
    private Calendar _now;
    
    /**
     * Initializes a new TimeRefresher
     */
    public TimeRefresher()
    {
        _now = Calendar.getInstance();
        _now.setTime(new Date());
    }

    @Override
    public void run()
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
            Thread.sleep(1000);
            _now.setTime(new Date());
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("The Thread to refresh the Time was interrupted");
        }
        
    }
    
    /**
     * Gets the calender instance from this instance of TimeRefresher
     * @return a refenrence to this instances calender object
     */
    public Calendar getCalenderInstace()
    {
        return _now;
    }
    
    /**
     * Gets a date instance from this instance of TimeRefresher
     * @return a date instance to this instances date object
     */
    public Date getDateInstance()
    {
        return _now.getTime();
    }

}
