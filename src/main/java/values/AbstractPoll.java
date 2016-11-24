package values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * An abtract class describing a poll
 * @author CkFreak & Procrastinator
 *
 */
public abstract class AbstractPoll
{
    /**
     * The Poll's name
     */
    protected String _name;

    /**
     * The user that has initilised the poll
     */
    protected User _initiator;

    /**
     * The provided answers
     */
    protected int[][] _answers;

    /**
     * All possible answers
     */
    protected ArrayList<String> _options;

    /**
     * A Map that keeps track of those, who have already voted
     */
    protected Map<String, User> _registeredVotes;

    /**
     * Initilizes a new AbstractPoll
     * @param name The name of the poll
     * @param millis Time in which the poll shall be decided
     */
    public AbstractPoll(String name, User user, ArrayList<String> arrayList)
    {
        _name = name;
        _options = arrayList;
        _answers = new int[arrayList.size()][1];
        _initiator = user;
        _registeredVotes = new HashMap<>();
    }

    /**
     * Returns the polls name
     * @param poll The poll whos name is wanted
     * @return The poll's name
     */
    public String getName()
    {
        return this._name;
    }

    /**
     * BCalculates the polls results
     * @return A Message Object with the poll's results
     */
    public Message calculateResults()
    {
        int totalVotes = 0;
        int result = 0;
        MessageBuilder builder = new MessageBuilder();

        for (int i = 0; i < _answers.length - 1; ++i)
        {
            totalVotes += _answers[i][0];
        }

        if (totalVotes == 0)
        {
            builder
                .appendString("Es wurden keine Votes für diese Poll abgegeben");
        }

        else
        {
            for (int i = 0; i < _answers.length - 1; ++i)
            {
                //calculate answer percentegages
                result = ((_answers[i][0]) / totalVotes * 100);
                if (_options.get(i) != null)
                {
                    builder.appendString("Das Ergebnis für Option " + (i + 1) + " : "
                            + _options.get(i).toString() + " ist: " + result + "%\n");
                    result = 0;
                }
            }
        }

        return builder.build();
    }

    /**
     * Registers a vote to a poll
     * @param name The poll's name that the vote shall be registered to 
     * @param option The chosen option
     */
    public abstract Message vote(String name, String option, User user);

    public abstract Message votePrivately(String name, String option, User user, MessageReceivedEvent event);

    /**
     * Registers a vote in the vote map 
     * @param name The poll's name
     * @param user The user that has voted
     */
    protected void registerVote(String name, User user)
    {
        _registeredVotes.put(name, user);
    }

    /**
     * Returns the poll's initiator
     * @param poll The poll whoms initiator is being searched
     * @return the user who has initiated the poll
     */
    protected User getInitiator(AbstractPoll poll)
    {
        return poll._initiator;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof AbstractPoll)
        {
            return this._name.equals(((AbstractPoll) o).getName());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        for (int i = 0; i < _name.length() - 1; ++i)
        {
            hash += _name.charAt(i);
        }
        return hash;
    }

    @Override
    public String toString()
    {
        return this.getName();
    }
}
