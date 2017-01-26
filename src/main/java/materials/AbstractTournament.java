package materials;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    TournamentMode _mode;

    /**
     * The amount of players at round start
     */
    int _amountOfPlayersAtRoundStart;

    /**
     * A list with all participants of this Tournament
     */
    final ArrayList<TournamentParticipant> _participants;

    /**
     * A list with all participants now in matched order. This list is only filled, when matchOpponents has been called
     */
    LinkedList<TournamentParticipant> _matchedOpponents;
    
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

    public abstract Message registerLoss(TournamentParticipant participant);
    
    
    /**
     * Gives all participants of the Tournament
     * @return a list with all participants of the tournament
     */
    public ArrayList<TournamentParticipant> getParticipants()
    {
        return _participants;
    }

    /**
     * Announces the winner of a tournament
     * @return A message with the winner in it
     */
    protected Message announceWinner()
    {
        MessageBuilder builder = new MessageBuilder();

        return builder.append("The winner of the tournament is: ***" + _matchedOpponents.poll().getName() + "***").build();
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
    String listParticipants()
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
    CopyOnWriteArrayList<TournamentParticipant> makeConcurrentList(List<TournamentParticipant> participants)
    {
        CopyOnWriteArrayList<TournamentParticipant> safeList = new CopyOnWriteArrayList<TournamentParticipant>();

        for (TournamentParticipant participant : participants)
        {
            safeList.add(participant);
        }

        return safeList;
    }

    /**
     * Gives a String with all opponents in the order in which they shall play each other
     * @param participants a list with the opponents matched
     * @return a String with all opponents in matched order
     */
    String getMatchedOpponents(LinkedList<TournamentParticipant> participants)
    {
        StringBuilder builder = new StringBuilder();
        CopyOnWriteArrayList<TournamentParticipant> safeList = makeConcurrentList(participants);


        for (TournamentParticipant parti : safeList)
        {
            if(!safeList.isEmpty())
            {
                if (safeList.size() % 2 == 0)
                {
                    builder.append("***" + safeList.get(0).getName() + "***" + " vs " + "***" + safeList.get(1).getName() + "***" + "\n");
                    safeList.remove(1);
                    safeList.remove(0);
                }
                else
                {
                    builder.append("***" + safeList.get(0).getName() + "***" + "Safe Round \n");
                    safeList.remove(0);
                }
            }
        }
        return builder.toString();
    }

    /**
     * Makes a new LinkedList from a CopyOnWriteArrayList
     * @param participants the CopyOnWriteArrayList that is to be converted
     * @return a new LinkedList
     */
    LinkedList<TournamentParticipant> makeLinkedListFromCopyOnWriteList(CopyOnWriteArrayList<TournamentParticipant> participants)
    {
        LinkedList<TournamentParticipant> returnList = new LinkedList<>();

        for (TournamentParticipant participant : participants)
        {
            returnList.add(participant);
        }
        return returnList;
    }




    
}
