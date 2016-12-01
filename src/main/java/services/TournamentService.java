package services;

import java.util.ArrayList;

import enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import values.AbstractTournament;
import values.SingleEliminationTournament;

/**
 * a class that starts and manages them as well
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

        switch (mode)
        {
        case SINGE_ELIMINATION:
            tournament = new SingleEliminationTournament(participants, mode);
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

        for (String string : participants)
        {
            tournament.addParticipant(string);
            builder.appendString(string + "\n");
        }

        return builder.build();
    }

}
