package services;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import values.AbstractPoll;
import values.AnonymousPoll;
import values.PublicPoll;

/**
 * This class works with all PollFachwerten and manages those
 * @author Timbo 
 * @version 2016
 *
 */
public class PollService
{
    /**
     * Initializes a new PollService
     */
    public PollService()
    {

    }

    /**
     * Starts a new Poll
     * @param name the name of the poll
     * @param millis The intervall  in which the game should be lost
     * @param event The MessagereceivedEvent
     * @return A pollFachwert that can be used
     */
    public PublicPoll startPPoll(String name, User user,
            MessageReceivedEvent event, ArrayList<String> arrayList)
    {
        event.getChannel()
            .sendMessage("Public Vote with the topic: " + name
                    + " was staredted .\n The options are:\n"
                    + this.getOptions(arrayList));
        return new PublicPoll(name, user, arrayList);
    }

    public AnonymousPoll startAPoll(String name, User user,
            MessageReceivedEvent event, ArrayList<String> arrayList)
    {
        event.getChannel()
            .sendMessage("Anonymous Vote with the topic " + name
                    + " was started.\n The options are:\n"
                    + this.getOptions(arrayList));
        
        return new AnonymousPoll(name, user, arrayList);
    }

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

    /**
     * Ends the Poll if the user that ends it is also the initiator
     * @param polls a list with all running polls
     * @param name the name of the poll
     * @param user the requesting user
     * @return true if it suceeds false otherwise
     */
    public Message endPoll(List<AbstractPoll> polls, String name, User user)
    {
        MessageBuilder builder = new MessageBuilder();

        for (AbstractPoll pollFachwert : polls)
        {
            if (pollFachwert.getName()
                .equals(name))
            {
                return pollFachwert.calculateResults();
            }
        }
        builder.appendString("Leider gibt es keine Poll mit dem Namen " + name
                + " oder du hast diese nicht gestartet");
        Message msg = builder.build();
        return msg;
    }

}
