package materials;

import java.util.ArrayList;
import java.util.LinkedList;

import enums.TournamentMode;
import values.TournamentParticipant;

/**
 * An abstract class that describes a tournament in general
 * @author Timbo
 * @version 30.11.2016
 */
public abstract class AbstractTournament
{
	/**
	 * The mode this Tournament is being played in
	 */
    protected TournamentMode _mode;
    
    /**
     * A list with all participants of this Tournament
     */
    protected ArrayList<TournamentParticipant> _participants;
    
    /**
     * Instatiates a new Abstract Tournament
     * @param participants a list of participants
     * @param mode the tournament mode
     * @require participants instanceof ArrayList<String>
     */
    public AbstractTournament(ArrayList<TournamentParticipant> participants , TournamentMode mode)
    {
        assert participants instanceof ArrayList<?>;
        
        _mode = mode;
        _participants = (ArrayList<TournamentParticipant>) participants;
    }
    
    
    /**
     * matches each player against an opponent
     * @param participants a list of participants
     * @return an arrayList with the opponents
     */
    protected abstract ArrayList<LinkedList<TournamentParticipant>> matchOpponents(ArrayList<TournamentParticipant> participants);
    
    
    
    /**
     * Gives all participants of the Tournament
     * @return a list with all participants of the tournament
     */
    protected ArrayList<TournamentParticipant> getParticipants()
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
    
    /**
     * Adds the named user to the tournament
     * @param participant the user that takes part in the tournament
     */
    public void addParticipant(TournamentParticipant participant)
    {
        _participants.add(participant);
    }
    
}
