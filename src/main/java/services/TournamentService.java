package services;

import java.util.ArrayList;

import values.AbstractTournament;

/**
 * a class that starts and manages them as well
 * @author Timbo
 * @version 18.11.2016
 */
public class TournamentService
{
    private ArrayList<String> _participants;
    private AbstractTournament _tournamentType;
    
    /**
     * starts a new tournament with the given mode
     * @param participants the participants of the tournament
     * @param tournament Die Art des Turniers
     */
    public TournamentService(ArrayList<String> participants, AbstractTournament tournament)
    {
        _participants = participants;
        _tournamentType = tournament;
    }
    
    
    
    
    
    
}
