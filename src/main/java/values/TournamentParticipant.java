package values;

/**
 * This class represents a Participant in a tournament from type AbstractTournament
 * @author Timbo
 * @version 2.12.2016
 */
public class TournamentParticipant
{
    
    /**
     * The number of losses a participant has aqurired
     */
    private int _losses;
    /**
     * The number of wins a participant has aquired
     */
    private int _wins;
    /**
     * The number of draws a participant has aquired
     */
    private int _draws;
    /**
     * The name of the participant
     */
    private String _name;
    
    /**
     * Instantiates a new TournamentParticipant
     * @param name The name of the Participant
     */
    public TournamentParticipant(String name)
    {
        _losses = 0;
        _wins = 0;
        _draws = 0;
        _name = name;
    }
    
    /**
     * Gets a players losses
     * @return the amount of losses a _player has
     */
    public int getLosses()
    {
        return _losses;
    }
    
    /**
     * Gets a players wins
     * @return the amount of wins a _player has
     */
    public int getWins()
    {
        return _wins;
    }
    
    /**
     * Gets the draws a _player has
     * @return the amount of draws a _player has
     */
    public int getDraws()
    {
        return _draws;
    }
    
    /**
     * Gets the name of the _player
     * @return the players name
     */
    public String getName()
    {
        return _name;
    }
    
    /**
     * Increents a players wins
     */
    public void incrementWins()
    {
        ++_wins;
    }
    
    /**
     * Increments a players drwaws
     */
    public void incrementDraws()
    {
        ++_draws;
    }
    
    /**
     * Increments a players losses
     */
    public void incrementLosses()
    {
        ++_losses;
    }
}
