package materials;

import enums.TournamentMode;
import net.dv8tion.jda.core.entities.Message;
import values.TournamentParticipant;

import java.util.ArrayList;
import java.util.Iterator;
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
        for (TournamentParticipant participant : _matchedOpponents)
        {
            if (participant.equals(loser))
            {
                _matchedOpponents.remove(participant);
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
        return null;
    }


    @Override
    public void matchOpponents(ArrayList<TournamentParticipant> participants)
    {
        //TODO IlligalArgumentException: bound must be positive fixen
        CopyOnWriteArrayList safeList = makeConcurrentList(participants);

        if (participants.size() % 2 == 0)
        {
            Random random = new Random();

            for (Object participant : safeList)
            {
                int index = random.nextInt(safeList.size() - 1);
                _matchedOpponents.add((TournamentParticipant) safeList.get(index));
                safeList.remove(index);
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
            matchOpponents(participants);

        }
    }

    private CopyOnWriteArrayList<TournamentParticipant> makeConcurrentList(ArrayList<TournamentParticipant> participants)
    {
        CopyOnWriteArrayList<TournamentParticipant> safeList = new CopyOnWriteArrayList<TournamentParticipant>();

        for (TournamentParticipant participant : participants)
        {
            safeList.add(participant);
        }

        return safeList;
    }

    /**
     * This method is called, when a Round ends and starts the next round
     */
    private void onRoundEnd()
    {
        ArrayList<TournamentParticipant> participants = new ArrayList<>();
        ++_round;
        for (TournamentParticipant participant : _matchedOpponents)
        {
            participants.add(participant);
        }

        matchOpponents(participants);
    }
}
