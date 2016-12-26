package materials;

import enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import values.TournamentParticipant;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A Tournament that represents the SingleElimination Mode of an AbstractTournament
 * Created by Timbo on 15/12/2016.
 */
public class SingleEliminationTournament extends AbstractTournament
{

    public SingleEliminationTournament(String name, ArrayList<TournamentParticipant> participants, TournamentMode mode)
    {
        super(name, participants, mode);
    }

    /**
     * Removes the loser from the Tournament
     * MAY RETURN NULL
     */
    public Message registerLoss(TournamentParticipant loser)
    {
        MessageBuilder builder = new MessageBuilder();
        for (TournamentParticipant participant : _matchedOpponents)
        {
            if (participant.equals(loser))
            {
                _matchedOpponents.remove(participant);
                builder.append("Participants are: " + listParticipants());
            }
            if (_matchedOpponents.size() == 1)
            {
                return announceWinner();
            }
            if (_matchedOpponents.size() <= _amountOfPlayersAtRoundStart/2)
            {
                onRoundEnd();
            }
        }
        return builder.build();
    }


    @Override
    public void matchOpponents(ArrayList<TournamentParticipant> participants)
    {
        //TODO IlligalArgumentException: bound must be positive fixen
        CopyOnWriteArrayList safeList = makeConcurrentList(participants);


        if (participants.size() == 2)
        {
            _matchedOpponents.add((TournamentParticipant) safeList.get(0));
            _matchedOpponents.add((TournamentParticipant) safeList.get(1));
            participants = null;
            _amountOfPlayersAtRoundStart = _matchedOpponents.size();
        }
        else if (participants != null && participants.size() % 2 == 0)
        {
            Random random = new Random();

            for (Object participant : safeList)
            {
                int index;
                index = random.nextInt(safeList.size() - 1);
                _matchedOpponents.add((TournamentParticipant) safeList.get(index));
                safeList.remove(index);
                random = new Random();
                index = random.nextInt(safeList.size() - 1);
                _matchedOpponents.add((TournamentParticipant) safeList.get(index));
                safeList.remove(index);
            }
            _amountOfPlayersAtRoundStart = _matchedOpponents.size();
        }
        else
        {
            Random random = new Random();
            int index = random.nextInt(safeList.size() - 1);
            _matchedOpponents.add(0, (TournamentParticipant) safeList.get(index));
            safeList.remove(index);
            participants.remove(index);
            _amountOfPlayersAtRoundStart = _matchedOpponents.size();
            matchOpponents(participants);

        }
    }


    /**
     * This method is called, when a Round ends and starts the next round
     */
    private void onRoundEnd()
    {
        ArrayList<TournamentParticipant> participants = new ArrayList<>();
        for (TournamentParticipant participant : _matchedOpponents)
        {
            participants.add(participant);
        }

        matchOpponents(participants);
    }
}
