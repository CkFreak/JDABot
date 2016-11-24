package fachwerte;

import java.util.ArrayList;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * This class represents a Poll Object. This Poll is public which means it's state can always be checked
 * @author Timbo
 * @version 2016
 */
public class PublicPoll extends AbstractPoll
{

    /**
     * Initializes a new PublicPoll
     * @param name the poll's name
     * @param user the user that has initiated the poll
     * @param arrayList a list with all possible answers
     */
    public PublicPoll(String name, User user, ArrayList<String> arrayList)
    {
        super(name, user, arrayList);
    }

    @Override
    public Message vote(String name, String option, User user)
    {

        MessageBuilder builder = new MessageBuilder();

        for (int i = 0; i <= _options.size() - 2; ++i)
        {
            if (_options.get(i).equalsIgnoreCase(option))
            {
                System.out.println(_options.get(i) + " vote an stelle: " + i);
                if (!_registeredVotes.containsValue(user))
                {
                    _answers[i][0] += 1;
                    registerVote(name, user);
                    return builder
                        .appendString("Der Vote wurde erfolgreich aufgenommen!")
                        .build();
                }
            }
        }
        return builder.appendString("Du hast bereits abgestimmt!")
            .build();

    }

    /**
     * This method returns the polls status
     * @param poll The poll whoms status is to be checked
     * @return the statu of the poll
     */
    public Message getStatus(PublicPoll poll)
    {
        return calculateResults();
    }

    @Override
    public Message votePrivately(String name, String option, User user,
            MessageReceivedEvent event)
    {
        return null;
    }

}
