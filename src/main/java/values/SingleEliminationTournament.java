package values;

import java.util.ArrayList;


import enums.TournamentMode;

/**
 * A Tournament that represents the SingleElimination Mode of an AbstractTournament
 * @author Timbo
 * @version 30.11.2016
 */
public class SingleEliminationTournament extends AbstractTournament
{
    
    public SingleEliminationTournament(ArrayList<String> participants, TournamentMode mode)
    {
        super(participants, mode);
    }

    @Override
    protected ArrayList<String> matchOpponents(ArrayList<String> participants)
    {
        return null;
    }

}
