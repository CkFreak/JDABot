package poll.pollTypes;

import java.util.ArrayList;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * This class represents a Poll Object. This poll is anonymous which means, that all votes castet to the poll cannot
 * be counted before the end of the poll
 * @author Timbo
 * @version 2016
 */
public class AnonymousPoll extends AbstractPoll
{

    /**
     * Initializes an AnonymousPoll instance
     * @param name The Poll's name 
     * @param user the user that initiated the poll
     * @param arrayList a list with all possible answers in it
     */
    public AnonymousPoll(String name, User user, ArrayList<String> arrayList)
    {
        super(name, user, arrayList);
    }

    @Override
    public Message votePrivately(String name, String option, User user, MessageReceivedEvent event)
    {
        event.getMessage().delete();
        
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
                        .append("Der Vote wurde erfolgreich aufgenommen!")
                        .build();
                }
            }
        }
        return builder.append("Du hast bereits abgestimmt!")
            .build();

    }

    @Override
    public Message vote(String name, String option, User user)
    {
        return null;
    }

}
