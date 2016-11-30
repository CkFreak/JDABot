package services;

import java.util.ArrayList;

import enums.TournamentMode;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
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
    
    //TODO Turnier richtig implementieren
    /**
     * Initializes a new tournament with the given participants
     * @param participants The participants of the tournament
     * @return a message that the bot sends to the channel with all the opponents in it
     */
    public Message initializeTournament(ArrayList<String> participants)
    {
        MessageBuilder builder = new MessageBuilder();
        
        for (String string : participants)
        {
            _participants.add(string);
        }
        
        return null;
    }
    
    /**
     * matches each player against an opponent
     * @param participants a list of participants
     * @return an arrayList with the opponents
     */
    private ArrayList<String> matchOpponents(ArrayList<String> participants)
    {
        return null;
    }
    
    
}
