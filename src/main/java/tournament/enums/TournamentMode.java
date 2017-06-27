package tournament.enums;

public enum TournamentMode
{
    /**
     * Single Emlimination means, one loss and you are out
     */
    SINGE_ELIMINATION,
    /**
     * Double Elimination means, that you can lose twice. One winners and one losers bracket
     */
    DOUBLE_ELIMINATION,
    /**
     * Triple Elimination means, that you can lose three times, once in winners once in losers1 and once in losers2
     */
    TRIPLE_ELIMINATION,
    /**
     * Each _player or team plays each other at least once
     */
    ROUND_ROBIN,
    /**
     * Similiar to Double Elimination except that you can play another game after losing the first two
     */
    THREE_GAME_GUARANTEE
}
