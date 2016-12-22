package services;

import java.util.ArrayList;
import java.util.List;

import enums.TournamentMode;
import materials.AbstractTournament;
import materials.SingleEliminationTournament;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import values.TournamentParticipant;

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
        AbstractTournament tournament;
        MessageBuilder builder = new MessageBuilder();
        ArrayList<TournamentParticipant> tournamentParticipants = createParticipants(participants);

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

        builder.append("Participants of this Tournament are: \n");

        for (TournamentParticipant parti : tournamentParticipants)
        {
            builder.append(parti.getName() + "\n");
        }

        tournament.matchOpponents(tournamentParticipants);

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
                for (TournamentParticipant participant : tournament1.getParticipants())
                {
                    if (participant.getName().equals(player))
                    {
                        if (tournament1 instanceof SingleEliminationTournament)
                        {
                            return ((SingleEliminationTournament) tournament1).registerLoss(participant);
                        }
                    }
                }
            }
        }
        return null;
    }

}
