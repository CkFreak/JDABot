package tournament.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import tournament.tournamentModes.AbstractTournament;
import tournament.tournamentModes.SingleEliminationTournament;
import tournament.enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import tournament.values.TournamentParticipant;

/**
 * a class that starts tournaments and manages them as well
 * @author Timbo
 * @version 18.11.2016
 */
public class TournamentService
{
    private List<AbstractTournament> _tournaments;

    /**
     * Instantiates a new Tournament Service
     */
    public TournamentService()
    {
        _tournaments = new ArrayList<>();
    }

    /**
     * Initializes a new tournament with the given participants
     * @param participants The participants of the tournament
     * @param mode the mode the tournament is being played in
     * @return a message that the bot sends to the channel with all the opponents in it
     */
    public Message initializeTournament(String name,
                                        TournamentMode mode, ArrayList<String> participants)
    {
        AbstractTournament tournament = null;
        MessageBuilder builder = new MessageBuilder();
        ArrayList<TournamentParticipant> tournamentParticipants = createParticipants(participants);

        if(mode != null)
        {
            switch (mode)
            {
                case SINGE_ELIMINATION:
                    tournament = new SingleEliminationTournament(name, tournamentParticipants, mode);
                    _tournaments.add(tournament);
                    builder.append("A new Tournament with the name " + name + "was started" + "\n");
                    break;
                case DOUBLE_ELIMINATION:
                    return builder.append(
                            "There was no tournament made! Contact a Dev please. Or maybe it was just your Mode that does not" +
                                    " match any of the existing")
                            .build();
                case TRIPLE_ELIMINATION:
                    return builder.append(
                            "There was no tournament made! Contact a Dev please. Or maybe it was just your Mode that does not" +
                                    " match any of the existing")
                            .build();
                case ROUND_ROBIN:
                    return builder.append(
                            "There was no tournament made! Contact a Dev please. Or maybe it was just your Mode that does not" +
                                    " match any of the existing")
                            .build();

                default:
                    return builder.append(
                            "There was no tournament made! Contact a Dev please. Or maybe it was just your Mode that does not" +
                                    " match any of the existing")
                            .build();
            }
        }

        builder.append("Participants of this Tournament are: \n");

        for (TournamentParticipant parti : tournamentParticipants)
        {
            builder.append(parti.getName() + "\n");
        }

        if (tournament != null)
        {
            tournament.matchOpponents(tournamentParticipants);
            builder.append("Opponents are:\n");

            builder.append(getMatchedOpponents(tournament.getMatchedOpponents()));
        }

        return builder.build();
    }

    /**
     * Creates new particpants from an ArrayList of Strings
     * @param participants the participants as Strings
     * @return A new list with TournamentParticipants
     */
    private ArrayList<TournamentParticipant> createParticipants(
            ArrayList<String> participants)
    {
        ArrayList<TournamentParticipant> participants2 = new ArrayList<>();
        
        for (String string : participants)
        {
            participants2.add(new TournamentParticipant(string));
        }
        return participants2;
    }

    //TODO add remaining Tournament Types once implemented

    /**
     * Increments the losses on the player that was entered
     * @param tournament The tournament the player is playing in
     * @param player The player that has lost
     */
    public Message registerLoss(String tournament, String player)
    {
        for (AbstractTournament tournament1 : _tournaments)
        {
            if (tournament1.getName().equals(tournament))
            {
                for (TournamentParticipant participant : tournament1.getMatchedOpponents())
                {
                    if (participant.getName().equals(player))
                    {
                            return tournament1.registerLoss(participant);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gives a String with all opponents in the order in which they shall play each other
     * @param participants a list with the opponents matched
     * @return a String with all opponents in matched order
     */
    private String getMatchedOpponents(LinkedList<TournamentParticipant> participants)
    {
        StringBuilder builder = new StringBuilder();
        CopyOnWriteArrayList<TournamentParticipant> safeList = (CopyOnWriteArrayList<TournamentParticipant>) makeConcurrentList(participants);


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
                    builder.append("***" + safeList.get(0).getName() + "***" + " is in the Safe Round \n");
                    safeList.remove(0);
                }
            }
        }
        return builder.toString();
    }

    /**
     * Makes a list that can be accessed by multiple threads at once without interfearence
     * @param participants The List that has to be made concurrent
     * @return a CopyOnWriteArrayList with the contents of the input list
     */
    private List<TournamentParticipant> makeConcurrentList(List<TournamentParticipant> participants)
    {
        CopyOnWriteArrayList<TournamentParticipant> safeList = new CopyOnWriteArrayList<>();

        for (TournamentParticipant participant : participants)
        {
            safeList.add(participant);
        }

        return safeList;
    }
}
