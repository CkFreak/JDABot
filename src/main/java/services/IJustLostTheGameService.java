package services;

import java.util.*;

import runnables.TimeRefresher;

/**
 * A class that abstracts the Game so it can be played by a computer. In Random intervals of at most 4 days 
 * the game will be lost
 * @author Timbo
 * @version 05.2017
 */
public class IJustLostTheGameService extends Observable implements Observer
{
    private static final String DAMMIT_I_JUST_LOST_THE_GAME = "Dammit I just lost the Game!";
    private static final int[] RANDOM_TIMES = {8, 14, 19, 26, 31, 42};


    /**
     * Initializes a new IJustLostTheGameService that collects all neccesarry infomration
     */
    public IJustLostTheGameService()
    {
       init();
    }

    /**
     * Initializes a new Game for a server. Is called again, when the game ends.
     */
    private void init()
    {
         Random random = new Random();
        TimeRefresher timeRefresher = new TimeRefresher(3600000 * RANDOM_TIMES[random.nextInt(RANDOM_TIMES.length -1 )]);
        timeRefresher.addObserver(this);
        Thread gameThread = new Thread(timeRefresher, "GAME_THREAD");
        gameThread.start();
        System.out.println("Game has been started");
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg)
    {
        setChanged();
        notifyObservers(DAMMIT_I_JUST_LOST_THE_GAME);
        init();
    }
}
