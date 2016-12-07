package materials;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import values.TournamentParticipant;

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
     * @param playerOne The first _player of the match
     * @param playerTwo the second _player of the match
     * @return A message with the new results or an error if the _player did not play in the tournament
     */
    public Message registerDraw(String playerOne, String playerTwo)
    {
        MessageBuilder builder = new MessageBuilder();
        
        if (!_participants.contains(new TournamentParticipant(playerOne)))
        {
            return builder.appendString(
                    "The first _player you have entered is not registerd for this tournament")
                .build();
        }
        else if (!_participants.contains(new TournamentParticipant(playerTwo)))
        {
            return builder.appendString(
                    "The second _player you have entered is not registered for this tournament")
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
    protected ArrayList<LinkedList<TournamentParticipant>> matchOpponents(ArrayList<TournamentParticipant> participants)
    {
        Random random = new Random();
        ArrayList<LinkedList<TournamentParticipant>> matchedOpponents = new ArrayList<>();
        
        //This shall be done, if there is an even amount of participants. Otherwise we have to choose one, that will
        //advance one round for free
        if (participants.size() % 2 == 0)
        {
            for (int i = 0; i < participants.size() - 1; ++i)
            {
                LinkedList<TournamentParticipant> opponents = new LinkedList<>();
                int player = random.nextInt(participants.size() - 1);
                
                //Get a random _player out of the list and match it against another random _player
                opponents.add(participants.get(player));
                matchedOpponents.add(opponents);
                //remove the players that we have just added
                participants.remove(player);
                
                //Do it here again, so that the _player we have already added is definetly gone from the list
                player = random.nextInt(participants.size() - 1);
                opponents.add(participants.get(player));
                participants.remove(player);
                
            }
        }
        else
        {
            LinkedList<TournamentParticipant> opponents = new LinkedList<>();
            opponents.add(0, participants.get(0));
            participants.remove(0);
            matchedOpponents.add(opponents);
            matchOpponents(participants);
        }
        return matchedOpponents;
    }

}
