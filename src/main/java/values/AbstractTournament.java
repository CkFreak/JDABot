package values;

import java.util.ArrayList;

import javax.management.modelmbean.RequiredModelMBean;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * An abstract class that describes a tournament in general
 * @author Timbo
 * @version 30.11.2016
 */
public abstract class AbstractTournament
{
    protected TournamentMode _mode;
    protected ArrayList<String> _participants;
    
    /**
     * Instatiates a new Abstract Tournament
     * @param participants a list of participants
     * @param mode the tournament mode
     * @require participants instanceof ArrayList<String>
     */
    public AbstractTournament(ArrayList<String> participants , TournamentMode mode)
    {
        assert (participants instanceof ArrayList<?>);
        _mode = mode;
        _participants = (ArrayList<String>) participants;
    }
    
    
    /**
     * matches each player against an opponent
     * @param participants a list of participants
     * @return an arrayList with the opponents
     */
    protected abstract ArrayList<String> matchOpponents(ArrayList<String> participants);
    
    
    /**
     * Initializes a new tournament with the given participants
     * @param participants The participants of the tournament
     * @return a message that the bot sends to the channel with all the opponents in it
     */
    public Message initializeTournament(ArrayList<String> participants)
    {
        MessageBuilder builder = new MessageBuilder();
        
        for (String string : participants)
        {
            _participants.add(string);
        }
        
        return null;
    }
    
    
    /**
     * Gives all participants of the Tournament
     * @return a list with all participants of the tournament
     */
    protected ArrayList<String> getParticipants()
    {
        return _participants;
    }
    
    
    /**
     * Gives the mode of this tournament
     * @return the tournament mode
     */
    protected TournamentMode getMode()
    {
        return _mode;
    }
    
    
}
