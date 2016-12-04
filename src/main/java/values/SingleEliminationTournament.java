package values;

import java.util.ArrayList;

import enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * A Tournament that represents the SingleElimination Mode of an AbstractTournament
 * @author Timbo
 * @version 30.11.2016
 */
public class SingleEliminationTournament extends AbstractTournament
{

    public SingleEliminationTournament(
            ArrayList<TournamentParticipant> participants, TournamentMode mode)
    {
        super(participants, mode);
    }

    /**
     * Registers the results of a game. 
     * One point is given to the winner and one point is taken from the loser. 
     * If the players are not part of the tournament the method will return that as error message
     * @param winner The winner of the match
     * @param loser the loser of the match
     * @return A message with either the results or if the players are not part of the tournament an error.
     */
    public Message registerResult(String winner, String loser)
    {
        MessageBuilder builder = new MessageBuilder();
        TournamentParticipant theWinner = null;
        TournamentParticipant theLoser = null;

        if (!_participants.contains(new TournamentParticipant(winner)))
        {
            return builder.appendString(
                    "The winner you have entered is not registerd for this tournament")
                .build();
        }
        else if (!_participants.contains(new TournamentParticipant(loser)))
        {
            return builder.appendString(
                    "The loser you have entered is not registered for this tournament")
                .build();
        }

        for (TournamentParticipant tournamentParticipant : _participants)
        {
            if (tournamentParticipant.getName()
                .equals(winner))
            {
                theWinner = tournamentParticipant;
                tournamentParticipant.incrementWins();
            }
            else if (tournamentParticipant.getName()
                .equals(loser))
            {
                theLoser = tournamentParticipant;
                tournamentParticipant.incrementLosses();
            }
        }
        return builder.appendString(theWinner.getName() + " now has "
                + theWinner.getWins() + " wins.\n" + theLoser.getName()
                + " now has " + theLoser.getLosses() + " losses.")
            .build();
    }
    
    /**
     * This Method will add one draw to each players result table
     * @param playerOne The first player of the match
     * @param playerTwo the second player of the match
     * @return A message with the new results or an error if the player did not play in the tournament
     */
    public Message registerDraw(String playerOne, String playerTwo)
    {
        MessageBuilder builder = new MessageBuilder();
        
        if (!_participants.contains(new TournamentParticipant(playerOne)))
        {
            return builder.appendString(
                    "The first player you have entered is not registerd for this tournament")
                .build();
        }
        else if (!_participants.contains(new TournamentParticipant(playerTwo)))
        {
            return builder.appendString(
                    "The second player you have entered is not registered for this tournament")
                .build();
        }
        
        for (TournamentParticipant tournamentParticipant : _participants)
        {
            if (tournamentParticipant.getName().equals(playerOne))
            {
                tournamentParticipant.incrementDraws();
            }
            else if (tournamentParticipant.getName().equals(playerTwo)) {
                tournamentParticipant.incrementDraws();
            }
        }
        return builder.appendString("A draw has been added to " + playerOne + " and " + playerTwo).build(); 
    }

    @Override
    protected ArrayList<String> matchOpponents(ArrayList<String> participants)
    {
        return null;
    }

}
