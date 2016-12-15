package materials;

import enums.TournamentMode;
import values.TournamentParticipant;

import java.util.ArrayList;
import java.util.Random;

/**
 * A Tournament that represents the SingleElimination Mode of an AbstractTournament
 * Created by Timbo on 15/12/2016.
 */
public class SingleEliminationImpl extends AbstractTournament
{

    public SingleEliminationImpl(ArrayList<TournamentParticipant> participants, TournamentMode mode)
    {
        super(participants, mode);
    }

    @Override
    protected void matchOpponents(ArrayList<TournamentParticipant> participants)
    {

        if (participants.size() % 2 == 0)
        {
            Random random = new Random();
            for (TournamentParticipant participant : participants)
            {
                int index = random.nextInt(participants.size() - 1);
                _matchedOpponents.add(participants.get(index));
                participants.remove(index);
                index = random.nextInt(participants.size() - 1);
                _matchedOpponents.add(participants.get(index));
                participants.remove(index);
            }
        }
        else
        {
            Random random = new Random();
            int index = random.nextInt(participants.size() - 1);
            _matchedOpponents.add(0, participants.get(index));
            participants.remove(index);
            matchOpponents(participants);

        }
    }
}
