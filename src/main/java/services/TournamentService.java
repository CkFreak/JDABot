package services;

import java.util.ArrayList;

import enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import values.AbstractTournament;
import values.SingleEliminationTournament;
import values.TournamentParticipant;

/**
 * a class that starts tournaments and manages them as well
 * @author Timbo
 * @version 18.11.2016
 */
public class TournamentService
{

    /**
     * starts a new tournament with the given mode
     * @param participants the participants of the tournament
     * @param tournament Die Art des Turniers
     */
    public TournamentService()
    {

    }

    /**
     * Initializes a new tournament with the given participants
     * @param participants The participants of the tournament
     * @param mode the mode the tournament is being played in
     * @return a message that the bot sends to the channel with all the opponents in it
     */
    public Message initializeTournament(ArrayList<String> participants,
            TournamentMode mode)
    {
        AbstractTournament tournament = null;
        MessageBuilder builder = new MessageBuilder();
        ArrayList<TournamentParticipant> tournamentParticipants = getParticipants(participants);

        switch (mode)
        {
        case SINGE_ELIMINATION:
            tournament = new SingleEliminationTournament(tournamentParticipants, mode);
            break;
        case DOUBLE_ELIMINATION:
            break;
        case TRIPLE_ELIMINATION:
            break;
        case ROUND_ROBIN:
            break;

        default:
            return builder.appendString(
                    "There was no tournament made! Contact a Dev please")
                .build();
        }

        builder.appendString("Participants of this Tournament are: \n");

        for (TournamentParticipant parti : tournamentParticipants)
        {
            tournament.addParticipant(parti);
            builder.appendString(parti.getName() + "\n");
        }

        return builder.build();
    }

    /**
     * Creates new particpants from an ArrayList of Strings
     * @param participants the participants as Strings
     * @return A new list with TournamentParticipants
     */
    private ArrayList<TournamentParticipant> getParticipants(
            ArrayList<String> participants)
    {
        ArrayList<TournamentParticipant> participants2 = new ArrayList<>();
        
        for (String string : participants)
        {
            participants2.add(new TournamentParticipant(string));
        }
        return participants2;
    }

}
