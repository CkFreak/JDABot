package services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

/**
 * A class that executes commands passed to it by the CommandHandler
 * @author Timbo
 * @version 1.12.2016
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
     * The Take my energy ASCII emoji
     */
    private final static String TAKE_MY_ENERGY = "༼ つ ◕_◕ ༽つ";

    /**
     * Initializes a CommandService
     * @param jda The bots JDA instance
     */
    public CommandService(JDA jda)
    {
        _jda = jda;
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
                .append(event.getAuthor().getAsMention() + " sends: " + TAKE_MY_ENERGY);
            try
            {
                event.getChannel()
                    .sendFile(file, builder.build()).queue();
            }
            catch (IOException e)
            {
                event.getChannel()
                    .sendMessage(
                            "There has been an error while sending the Image. Please contact a Dev. IO-Exception!").queue();
                e.printStackTrace();
            }
        }
        catch (IllegalArgumentException e)
        {
            event.getChannel()
                .sendMessage(
                        "There has been an error. Please contact a Dev, IllegalArgumentException while sending a picture (CommandService)!").queue();
            e.printStackTrace();
        }
    }

    /**
     * Collects all Roles incoporated by a user
     * @param user The User whoms roles are being requested
     * @return A formated string with all user roles
     */
    public String getUserInfo(String user, Guild guild)
    {
        List<Member> users = guild
            .getMembers();
        String roles = "This users roles are:\n";

        for (Member member : users)
        {
            if (member.getUser().getName().equalsIgnoreCase(user))
            {
                List<Role> rolesForUser = member.getRoles();

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

        if (isAdmin()
                || isModerator()
                || isOwnerOfServer(event.getAuthor(), event.getGuild()))
        {
            //Channel getten und dann alle Permissions nehmen und auf einen neuen Channel packen, der am besten an der selben
            //Stelle ist wie der den man löscht

        }
        else
        {
            event.getChannel()
                .sendMessage("You do not have sufficent permissions!");
        }
    }

    /**
     * Deletes messages from the channel the command has been postet in
     * @param event The MessageReceivedEvent 
     * @param amount The amount of messages to be deleted
     */
    public void deleteChannelMessages(MessageReceivedEvent event, int amount)
    {
            List<Message> recentMessages = event.getChannel()
                .getHistory()
                .retrievePast(amount).complete();

            for (Message message : recentMessages)
            {
                message.delete().queue();
            }
    }

    /**
     * Gives a Message that contains all commands the bot is able to handle
     * @param event The event that holds the Channel the bot has to answer to 
     * @return A Message that is directly delivered to the channel
     */
    @Deprecated
    public void getHelpCommands(MessageReceivedEvent event, String path)
    {

        MessageBuilder builder = new MessageBuilder();

        try
        {
            List<String> allCommands = Files.readAllLines(Paths.get(path));

            int command = 0;
            int index = 0;

            for (int i = 0; index < allCommands.size() - 1; ++i)
            {
                builder.append("```");
                while (index <= allCommands.size() / 10)
                {
                    builder.append(allCommands.get(command) + "\n");
                    ++command;
                    ++index;
                }
                builder.append("```");
                event.getChannel()
                        .sendMessage(builder.build()).queue();
                builder = new MessageBuilder();
                index = 0;
            }
        }
        catch (IOException e)
        {
            event.getChannel()
                .sendMessage(
                        "**commands.txt** not found. Please contact a Dev!").queue();
        }
        catch (IndexOutOfBoundsException e)
        {
            System.out.println("Nothing severe happened. Index was at exactly the length but contained a null character");
        }

    }

    /**
     * Builds a message that contains the commands for the specified file
     * @param path The Path to the commands file
     * @return a message that contains all commands for that file
     */
    public Message getCommands(String path)
    {
        MessageBuilder builder = new MessageBuilder();

        try
        {
            List<String> musicCommands = Files.readAllLines(Paths.get(path));


            for (String command : musicCommands)
            {
                builder.append(command + "\n");
            }
            return builder.build();
        }
        catch (IOException e)
        {
            builder = new MessageBuilder();
                       return  builder.append("**The appropriate command file** was not found. Please contact a Dev!").build();
        }
        catch (IndexOutOfBoundsException e)
        {
            System.out.println("Nothing severe happened. Index was at exactly the length but contained a null character");
        }

        return new MessageBuilder().append("Something went wrong contact a Dev please!").build();
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
            .sendMessage("Hello " + user).queue();
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
            .getMembersWithRoles(admin);

        for (Member user : admins)
        {
                userWithAdminPrivileges += user.getUser().getName() + "\n";
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
            userWithModPriviliges += user.getUser().getName() + "\n";
        }
        return userWithModPriviliges + "```";
    }

    /**
     * Shuts the bot down and closes all open connections
     * @param event The MessageReceivedEvent that is used to check admin privileges
     */
    public void reactToShutdown(MessageReceivedEvent event)
    {
        if(event.getMember().getRoles().contains(_roles.get(0)) || event.getMember().isOwner())
        {
            _jda.shutdown();
            String command = "sudo reboot";
            Runtime runtime = Runtime.getRuntime();

            try
            {
                Process process = runtime.exec(command);

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    System.out.println(line);
                }
            }
            catch (IOException ex)
            {
                System.out.println("Shutdown interrupted for IO reasons");
            }
        }
    }

    /**
     * Changes the Bots Game
     */
    public void changeGame(JDA jda, String game)
    {
        jda.getPresence().setGame(Game.of(game));
    }

    /**
     * Checks the user for admin privileges
     * @return true, if the user is admin false otherwise
     */
    public boolean isAdmin()
    {
        for (Role role : _roles)
        {
            if (role.hasPermission(Permission.ADMINISTRATOR))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks for moderator priviliges
     * @return true if the user is moderator, false otherwise
     */
    public boolean isModerator()
    {
        for (Role role : _roles)
        {
            if (role.hasPermission(Permission.MANAGE_ROLES)
                    || role.hasPermission(Permission.MESSAGE_MANAGE) || role.hasPermission(Permission.BAN_MEMBERS)
                    || role.hasPermission(Permission.MANAGE_CHANNEL)
                    || role.hasPermission(Permission.MESSAGE_HISTORY))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks, wheater or not the user is owner of the server
     * @param user the user that is to be checked
     * @param guild The Guild the user is from
     * @return true if the user is the owner, false otherwise
     */
    public boolean isOwnerOfServer(User user, Guild guild)
    {
        return (user
            .equals(guild
                .getOwner()));
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
     * @param event The MessagereceivedEvent
     * @param username The name of the user that is being promoted
     * @param role The new role the user should have
     */
    public boolean promoteUser(MessageReceivedEvent event, String username,
            String role)
    {
        List<Member> users = event.getGuild()
            .getMembers();
        if (!isRoleInList(_roles, role))
        {
            event.getChannel()
                .sendMessage("The choosen role does not exist").queue();
            return false;
        }
        else if (!isUserInList(users, username))
        {
            event.getChannel()
                .sendMessage("The choosen user does not seem to exist!").queue();
            return false;
        }
        else
        {
            for (Member user : users)
            {
                if (user.getUser().getName().equalsIgnoreCase(username))
                {
                    GuildController controller = event.getGuild()
                        .getController();
                    int indexOfRole = getIndexOfRole(role);
                    controller.addRolesToMember(user, _roles.get(indexOfRole)).queue();
                    return true;
                }
            }
        }
        return false;
    }

    private int getIndexOfRole(String role)
    {
        for (Role role1 : _roles)
        {
            if (role1.getName().equalsIgnoreCase(role))
            {
                return _roles.indexOf(role1);
            }
        }
        return -1;
    }

    /**
     * Determines weather or not a user is part of a List of Members
     * @param members The List of members the user is supposed to be in
     * @param username The User in question
     * @return true, if the user is part of the list, false otherwise
     */
    private boolean isUserInList(List<Member> members, String username)
    {
        for (Member member : members)
        {
            if (member.getUser().getName().equalsIgnoreCase(username))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines weather or not a role is included in a given list
     * @param roles The list that the role is supposed to be in
     * @param role The role in question
     * @return true, if the role is contained, false otherwise
     */
    private boolean isRoleInList(List<Role> roles, String role)
    {
        for (Role role1 : roles)
        {
            if (role1.getName().equalsIgnoreCase(role))
            {
                return true;
            }
        }
        return false;
    }

}
