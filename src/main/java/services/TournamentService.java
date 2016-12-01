package services;

import java.util.ArrayList;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import values.AbstractTournament;

/**
 * a class that starts and manages them as well
 * @author Timbo
 * @version 18.11.2016
 */
public class TournamentService
{
    /**
     * All Participants of the tournament
     */
    private ArrayList<String> _participants;
    
    /**
     * starts a new tournament with the given mode
     * @param participants the participants of the tournament
     * @param tournament Die Art des Turniers
     * @require participants instanceof ArrayList<String>
     */
    public TournamentService(ArrayList<String> participants, AbstractTournament tournament)
    {
        assert (participants instanceof ArrayList<?>);
        
        _participants = participants;
    }
    
    /**
     * Initializes a new tournament with the given participants
     * @param participants The participants of the tournament
     * @return a message that the bot sends to the channel with all the opponents in it
     */
    public Message initializeTournament(ArrayList<String> participants)
    {
        MessageBuilder builder = new MessageBuilder();
        builder.appendString("Participants of this Tournament are: \n");
        
        for (String string : participants)
        {
            _participants.add(string);
            builder.appendString(string + "\n");
        }
        
        return builder.build();
    }
    
    
    
    
    
    
}
