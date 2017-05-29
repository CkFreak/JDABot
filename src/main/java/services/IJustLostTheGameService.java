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

    /**
     * Initializes a new IJustLostTheGameService that collects all neccesarry infomration
     */
    public IJustLostTheGameService()
    {

    }
}
