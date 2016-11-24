package services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import fachwerte.AbstractPoll;
import fachwerte.AnonymousPoll;
import fachwerte.PublicPoll;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.managers.GuildController;

/**
 * A class that executes commands passed to it by the CommandHandler
 * @author Timbo
 * @version 2016
 */
public class CommandService
{

    /**
     * A list with all roles from the server
     */
    private List<Role> _roles;

    /**
     * The JDA instance that represents this bot
     */
    JDA _jda;

    /**
     * A PollService that coordinates all polls
     */
    private PollService _pollService;

    /**
     * The Take my energy ASCII emoji
     */
    private final static String TAKE_MY_ENERGY = "༼ つ ◕_◕ ༽つ";

    /**
     * Initializes a CommandService
     * @param event A MessageReceivedEvent
     * @param jda The bots JDA instance
     */
    public CommandService(MessageReceivedEvent event, JDA jda)
    {
        _jda = jda;
        _pollService = new PollService();
    }

    /**
     * Sends a picture to the chat  
     * @param event The MessageReceivedEvent
     * @param filepath the patch to the desired picture
     */
    public void sendEmoji(MessageReceivedEvent event, String filepath)
    {
        try
        {
            String path = new File(filepath).getAbsolutePath();
            File file = new File(path);
            MessageBuilder builder = new MessageBuilder();
            builder
                .appendString(event.getAuthor() + " sends: " + TAKE_MY_ENERGY);
            try
            {
                event.getChannel()
                    .sendFile(file, builder.build());
            }
            catch (IOException e)
            {
                event.getChannel().sendMessage("There has been an error while sending the Image. Please contact a Dev. IO-Exception!");
                e.printStackTrace();
            }
        }
        catch (IllegalArgumentException e)
        {
            event.getChannel().sendMessage("There has been an error. Please contact a Dev, IllegalArgumentException while sending a picture (CommandService)!");
            e.printStackTrace();
        }
    }

    /**
     * Collects all Roles incoporated by a user
     * @param event The event holding the command message
     * @param messageContent a string array with all message content associated with the command
     * @return A formated string with all user roles
     */
    public String getUserInfo(MessageReceivedEvent event,
            String[] messageContent)
    {

        if (messageContent.length < 2)
        {
            return "A User has to be specified";
        }

        String username = messageContent[1];

        List<Member> users = event.getGuild()
            .getMembers();
        String roles = "This users roles are:\n";

        for (Member user : users)
        {
            if (user.getUser()
                .getAsMention()
                .equals(username))
            {
                List<Role> rolesForUser = event.getGuild()
                    .getRoles();
                for (Role role : rolesForUser)
                {
                    roles += role.getAsMention() + "\n";
                }
            }
        }

        if (roles.equals("This users roles are:\n"))
        {
            return "Unfortunately this user does not seem to exist!";
        }
        return roles;
    }

    /**
     * Deletes all Messages from this channel
     * @param event The MessageReceivedEvent
     */
    //TODO befehl fixen!
    public void deleteAllMessages(MessageReceivedEvent event)
    {

        if (isAdmin(event.getAuthor(), event)
                || isModerator(event.getAuthor(), event)
                || isOwnerOfServer(event.getAuthor(), event))
        {
            TextChannel channel = event.getTextChannel();
            ChannelManager manager = channel.getManager();
            Guild guild = event.getGuild();
            String channelName = channel.getName();
            List<PermissionOverride> permissionOverrides = channel
                .getPermissionOverrides();

            // manager.delete();
            //guild.createTextChannel(channelName);
            List<TextChannel> textChannels = guild.getTextChannels();
            for (int i = 0; i < textChannels.size() - 1; ++i)
            {
                if (textChannels.get(i)
                    .getName()
                    .equals(channelName))
                {
                    for (PermissionOverride permissionOverride : permissionOverrides)
                    {
                        // PermissionOverrideManager permissionMananger = permissionOverride
                        // .getManager();
                        // permissionMananger.overwrite(permissionOverride);
                        // permissionMananger.update();
                    }
                }
            }

        }
        else
        {
            event.getChannel()
                .sendMessage(
                        "You do not have sufficent permissions!");
        }
    }

    /**
     * Deletes messages from the channel the command has been postet in
     * @param event The MessageReceivedEvent 
     * @param amount The amount of messages to be deleted
     */
    public void deleteChannelMessages(MessageReceivedEvent event, int amount)
    {
        try
        {
            List<Message> recentMessages = event.getChannel()
                .getHistory()
                .retrievePast(amount).block();
            
            for (Message message : recentMessages)
            {
                message.deleteMessage();
            }
        }
        catch (RateLimitedException e)
        {
            event.getChannel().sendMessage("There has been a RateLimitedExeption during deletion. Please contact a Dev so he can fix it.");
            e.printStackTrace();
        }
        
               

//        for (Message message : recentMessages)
//        {
//            message.deleteMessage();
//            try
//            {
//                Thread.sleep(150);
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//        }

    }

    /**
     * Gives a Message that contains all commands the bot is able to handle
     * @param event The event that holds the Channel the bot has to answer to 
     * @return A Message that is directly delivered to the channel
     */
    public void getHelpCommands(MessageReceivedEvent event, String path)
    {

        MessageBuilder builder = new MessageBuilder();

        try
        {
            List<String> allCommands = Files.readAllLines(Paths.get(path));

            int index = 0;
            int command = 0;

            for (int i = 0; i < allCommands.size() - 1; ++i)
            {
                builder.appendString("```");

                while (index <= 10)
                {
                    builder.appendString(allCommands.get(command) + "\n");
                    command++;
                    index++;
                }
                event.getChannel()
                    .sendMessage(builder.appendString("```")
                        .build());
                builder = new MessageBuilder();
                index = 0;
            }
        }
        catch (IOException e)
        {
            event.getChannel()
                .sendMessage(
                        "**commands.txt** not found. Please contact a Dev!");
            e.printStackTrace();
        }

    }

    /**
     * Method replying to hello command
     * @param event The MessagereceivedEvent
     */
    public void replyToHello(MessageReceivedEvent event)
    {
        String user = event.getAuthor()
            .getName();
        event.getChannel()
            .sendMessage("Hello " + user);
    }

    /**
     * Gets all Admins from the Server
     * @param event The MessagereceivedEvent
     * @return a formated String with all Admins of the server
     */
    public String getAdmin(MessageReceivedEvent event)
    {
        String userWithAdminPrivileges = "```The admins of the server are:\n";

        Role admin = _roles.get(0);

        List<Member> admins = event.getGuild()
            .getMembers();

        for (Member user : admins)
        {
            if (user.getRoles()
                .contains(admin))
            {
                userWithAdminPrivileges += user.getUser() + "\n";
            }
        }

        return userWithAdminPrivileges + "```";

    }

    /**
     * Gives the server moderators
     * @param event DThe MessagereceivedEvent
     */
    public String getMods(MessageReceivedEvent event)
    {
        String userWithModPriviliges = "```Mods of this server are:\n";

        Role mod = _roles.get(2);

        List<Member> mods = event.getGuild()
            .getMembersWithRoles(mod);

        for (Member user : mods)
        {
            userWithModPriviliges += user.getUser() + "\n";
        }
        return userWithModPriviliges + "```";
    }

    /**
     * Shuts the bot down and closes all open connections
     * @param event The MessagereceivedEvent that is used to check admin priviliges
     */
    public void reagiereAufShutdown(MessageReceivedEvent event)
    {
        event.getMessage();
        if (isAdmin(event.getAuthor(), event)
                || isOwnerOfServer(event.getAuthor(), event))
        {
            _jda.shutdown();
        }
    }

    /**
     * Changes the Bots Game
     */
    public void changeGame(JDA jda, String game)
    {
        ((JDABuilder) jda).setGame(new Game()
        {
            
            @Override
            public String getUrl()
            {
                return "This is not a valid URL";
            }
            
            @Override
            public GameType getType()
            {
                return GameType.DEFAULT;
            }
            
            @Override
            public String getName()
            {
                return game;
            }
        });
    }

    /**
     * Checks the user for admin priviliges
     * @param user the user to be checked
     * @param event The MessagereceivedEvent
     * @return true, if the user is admin false otherwise
     */
    public boolean isAdmin(User user, MessageReceivedEvent event)
    {
        Role admin = _roles.get(0);

        List<Member> admins = event.getGuild()
            .getMembersWithRoles(admin);

        return admins.contains(event.getAuthor());
    }

    /**
     * Checks for moderator priviliges
     * @param user The user that being checked
     * @param event The MessagereceivedEvent
     * @return true if the user is moderator, false otherwise
     */
    public boolean isModerator(User user, MessageReceivedEvent event)
    {

        Role moderator = _roles.get(2);

        List<Member> moderators = event.getGuild()
            .getMembersWithRoles(moderator);

        return moderators.contains(event.getAuthor());
    }

    /**
     * Checks, wheater or not the user is owner of the server
     * @param user the user that is to be checked
     * @param event The MessagereceivedEvent
     * @return true if the user is the owner, false otherwise
     */
    public boolean isOwnerOfServer(User user, MessageReceivedEvent event)
    {
        if (event.getAuthor()
            .equals(event.getGuild()
                .getOwner()))
        {
            return true;
        }
        return false;
    }

    /**
     * Starts a new poll
     * @param name The poll's name
     * @param user the user that initiates the poll
     * @param event The MessagereceivedEvent
     * @param polls a list with all running polls
     * @param arrayList the choosen options
     */
    public void startPVote(String name, User user, MessageReceivedEvent event,
            List<AbstractPoll> polls, ArrayList<String> arrayList)
    {
        polls.add(_pollService.startPPoll(name, user, event, arrayList));
    }

    /**
     * Starts an anonymous poll
     * @param name the polls name
     * @param user the initiating user
     * @param event The MessagereceivedEvent
     * @param polls a list with all running polls
     * @param arrayList the choosen options
     */
    public void startAVote(String name, User user, MessageReceivedEvent event,
            List<AbstractPoll> polls, ArrayList<String> arrayList)
    {

        polls.add(_pollService.startAPoll(name, user, event, arrayList));
    }

    /**
     * Ends the poll
     * @param name Name of the poll that is to be ended
     * @param user The users name that ends the poll
     */
    public Message endVote(String name, User user, List<AbstractPoll> polls)
    {
        return _pollService.endPoll(polls, name, user);
    }

    /**
     * Votes for one option
     * @param name The polls name
     * @param option the choosen option
     * @param polls a list with all running polls
     */
    public Message vote(String name, String option, User user,
            List<AbstractPoll> polls, MessageReceivedEvent event)
    {
        for (AbstractPoll pollFachwert : polls)
        {
            if (pollFachwert.getName()
                .equals(name))
            {
                if (pollFachwert instanceof PublicPoll)
                {
                    return ((PublicPoll) pollFachwert).vote(name, option, user);
                }
                else if (pollFachwert instanceof AnonymousPoll)
                {
                    return ((AnonymousPoll) pollFachwert).votePrivately(name,
                            option, user, event);
                }
            }
        }
        return new MessageBuilder()
            .appendString("Something went wrong. Please try again!")
            .build();
    }

    /**
     * Initializes the role list
     * @param event The MessagereceivedEvent
     */
    public void initializeRoles(MessageReceivedEvent event)
    {
        _roles = event.getGuild()
            .getRoles();
    }

    /**
     * Promotes a user if the asking person is admin or moderator
     * @param The MessagereceivedEvent
     * @param username The name of the user that is being promoted
     * @param role The new role the user should have
     */
    public void promoteUser(MessageReceivedEvent event, String username,
            String role)
    {
        List<Member> users = event.getGuild()
            .getMembers();
        if (!_roles.contains(role))
        {
            event.getChannel()
                .sendMessage("The choosen role does not exist");
        }
        else if (!users.contains(username))
        {
            event.getChannel()
                .sendMessage(
                        "The choosen user does not seem to exist!");
        }
        else
        {
            for (Member user : users)
            {
                if (user.getAsMention()
                    .equals(username))
                {
                    GuildController controller = event.getGuild().getController();
                    int indexOfRole = _roles.indexOf(role);
                    controller.addRolesToMember(user, _roles.get(indexOfRole));
                }
            }
        }

    }

}
