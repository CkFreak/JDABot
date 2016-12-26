package materials;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import values.TournamentParticipant;

/**
 * An abstract class that describes a tournament in general
 * @author Timbo
 * @version 30.11.2016
 */
public abstract class AbstractTournament
{

    /**
     * The name of the tournament
     */
    private String _name;

    /**
	 * The mode this Tournament is being played in
	 */
    protected TournamentMode _mode;

    /**
     * The amount of players at round start
     */
    protected int _amountOfPlayersAtRoundStart;

    /**
     * A list with all participants of this Tournament
     */
    protected final ArrayList<TournamentParticipant> _participants;

    /**
     * A list with all participants now in matched order. This list is only filled, when matchOpponents has been called
     */
    protected LinkedList<TournamentParticipant> _matchedOpponents;
    
    /**
     * Instatiates a new Abstract Tournament
     * @param participants a list of participants
     * @param mode the tournament mode
     */
    public AbstractTournament(String name, ArrayList<TournamentParticipant> participants , TournamentMode mode)
    {
        _name = name;
        _mode = mode;
        _amountOfPlayersAtRoundStart = 0;
        _participants = participants;
        _matchedOpponents = new LinkedList<>();
    }
    
    
    /**
     * matches each _player against an opponent
     * @param participants a list of participants
     * @return an arrayList with the opponents
     */
    public abstract void matchOpponents(ArrayList<TournamentParticipant> participants);
    
    
    
    /**
     * Gives all participants of the Tournament
     * @return a list with all participants of the tournament
     */
    public ArrayList<TournamentParticipant> getParticipants()
    {
        return _participants;
    }
    
    
    /**
     * Gives the mode of this tournament
     * @return the tournament mode
     */
    public TournamentMode getMode()
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

    /**
     * Announces the winner of a tournament
     * @return A message with the winner in it
     */
    protected Message announceWinner()
    {
        MessageBuilder builder = new MessageBuilder();

        return builder.append("The winner of this tournament is: ***" + _matchedOpponents.getFirst().getName() + "***").build();
    }

    public String getName()
    {
        return _name;
    }

    /**
     * @return all matched opponents
     */
    public LinkedList<TournamentParticipant> getMatchedOpponents()
    {
        return _matchedOpponents;
    }

    /**
     * Makes a String of all remaining participants in the Tournament
     * @return a String with all participants in it.
     */
    public String listParticipants()
    {
        String participants = "\n";
        for (TournamentParticipant parti : _matchedOpponents)
        {
            participants += parti.getName() + "\n";
        }
        return participants;
    }

    /**
     * Takes an ArrayList of TournamentParticipants and turns it into a concurrentList that can be uses only by one Thread at a time
     * @param participants A List of TournamentParticipants
     * @return A Concurrent List with the Tournament Participants
     */
    protected CopyOnWriteArrayList<TournamentParticipant> makeConcurrentList(ArrayList<TournamentParticipant> participants)
    {
        CopyOnWriteArrayList<TournamentParticipant> safeList = new CopyOnWriteArrayList<TournamentParticipant>();

        for (TournamentParticipant participant : participants)
        {
            safeList.add(participant);
        }

        return safeList;
    }




    
}
