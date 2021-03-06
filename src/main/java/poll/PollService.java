package poll;

import java.util.ArrayList;

import poll.pollTypes.AbstractPoll;
import poll.pollTypes.AnonymousPoll;
import poll.pollTypes.PublicPoll;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * This class works with all PollFachwerten and manages those
 * @author Timbo 
 * @version 1.12.2016
 *
 */
public class PollService
{
    /**
     * A list with all running Polls
     */
    private ArrayList<AbstractPoll> _polls;
    
    /**
     * Initializes a new PollService
     */
    public PollService()
    {
        _polls = new ArrayList<>();
    }

    /**
     * Starts a new Poll
     * @param name the name of the poll
     * @param user the user that started the poll
     * @param event The MessagereceivedEvent
     * @param arrayList a list with all poll options
     * @return A pollFachwert that can be used
     */
    public void startPPoll(String name, User user,
            MessageReceivedEvent event, ArrayList<String> arrayList)
    {
        event.getChannel()
            .sendMessage("Public Vote with the topic: " + name
                    + " was staredted .\n The options are:\n"
                    + this.getOptions(arrayList));
        _polls.add(new PublicPoll(name, user, arrayList));
    }

    /**
     * Starts a new Anonymous Poll
     * @param name the name of the poll
     * @param user the user that started the poll
     * @param event the event that got fired with initialization of the poll
     * @param arrayList the options for the poll
     */
    public void startAPoll(String name, User user,
            MessageReceivedEvent event, ArrayList<String> arrayList)
    {
        event.getChannel()
            .sendMessage("Anonymous Vote with the topic " + name
                    + " was started.\n The options are:\n"
                    + this.getOptions(arrayList));
        
        _polls.add(new AnonymousPoll(name, user, arrayList));
    }


    /**
     * Ends the Poll if the user that ends it is also the initiator
     * @param name the name of the poll
     * @param user the requesting user
     * @return true if it suceeds false otherwise
     */
    public Message endPoll(String name, User user)
    {
        MessageBuilder builder = new MessageBuilder();

        for (AbstractPoll pollFachwert : _polls)
        {
            if (pollFachwert.getName()
                .equals(name))
            {
                return pollFachwert.calculateResults();
            }
        }
        builder.append("Leider gibt es keine Poll mit dem Namen " + name
                + " oder du hast diese nicht gestartet");
        Message msg = builder.build();
        removePoll(name);
        return msg;
    }
    
    /**
     * Removes a poll from the poll list
     * @param pollName The poll that is to be removed from the list
     */
    public void removePoll(String pollName)
    {
        for (AbstractPoll abstractPoll : _polls)
        {
            if (abstractPoll.getName()
                .equals(pollName))
            {
                _polls.remove(abstractPoll);
            }
        }

    }
    
    /**
     * Returns all active polls
     * @return A Message that contains all polls
     */
    public Message getCurrentVotes()
    {
        MessageBuilder builder = new MessageBuilder();

        for (AbstractPoll abstractPoll : _polls)
        {
            builder.append(abstractPoll.getName() + "\n");
        }
        return builder.build();
    }
    
    /**
     * Returns the requested poll's status
     * @param pollName The requested poll's name
     * @return The state or an error 
     */
    public Message getStatus(String pollName)
    {
        MessageBuilder builder = new MessageBuilder();

        for (AbstractPoll abstractPoll : _polls)
        {
            if (abstractPoll.getName()
                .equals(pollName))
            {
                if (abstractPoll instanceof PublicPoll)
                {
                    return ((PublicPoll) abstractPoll)
                        .getStatus((PublicPoll) abstractPoll);
                }
            }
        }
        return builder.append(
                "The specified poll has not been found or is anonymous!")
            .build();
    }
    
    
    /**
     * Votes for one option
     * @param name The polls name
     * @param options the choosen option
     * @param event The event to be passed on down to the polls themselves
     */
    public Message vote(String name, ArrayList<String> options, User user, MessageReceivedEvent event)
    {
        String option = options.get(0);
        
        for (AbstractPoll pollFachwert : _polls)
        {
            if (pollFachwert.getName()
                .equals(name))
            {
                if (pollFachwert instanceof PublicPoll)
                {
                    return pollFachwert.vote(name, option, user);
                }
                else if (pollFachwert instanceof AnonymousPoll)
                {
                    return pollFachwert.votePrivately(name,
                            option, user, event);
                }
            }
        }
        return new MessageBuilder()
            .append("Something went wrong. Please try again!")
            .build();
    }

    /**
     * Gets all the options for the poll in a formated string
     * @param arrayList the options are in this list
     * @return a formated String with all options in it
     */
    private String getOptions(ArrayList<String> arrayList)
    {
        String choices = "";
        for (int i = 0; i < arrayList.size() - 1; ++i)
        {
            if (!(arrayList.get(i) == null))
            {
                choices += arrayList.get(i) + "\n";
            }
        }
        return choices;
    }
}
