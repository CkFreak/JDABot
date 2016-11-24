package listener;

import handler.CommandHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * A Class that listens for any incoming message
 * @author Timbo
 * @version 2016
 */
public class CommandListener extends ListenerAdapter
{
    CommandHandler _handler;

    /**
     * Initializes a new CommandListener
     * @param jda The JDA instance that represents this bot
     */
    public CommandListener(JDA jda)
    {
         _handler = new CommandHandler(jda);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        _handler.handleIncomingMessages(event);
    }
}
