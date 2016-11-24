package listener;

import java.util.List;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * This class listens for User promotions to post a reaction in the chat
 * @author Timbo
 * @version 2016
 */
public class UserPromotedListener extends ListenerAdapter
{

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event)
    {
        MessageBuilder msg = new MessageBuilder();
        msg.appendString(event.getMember()
            .getAsMention() + " now is: " + getRoleAsStringAdd(event));

        event.getGuild()
            .getPublicChannel()
            .sendMessage(msg.build());
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event)
    {
        MessageBuilder msg = new MessageBuilder();
        msg.appendString(event.getMember()
            .getAsMention() + " was demoted and is no longer: "
                + getRoleAsStringRemove(event));

        event.getGuild()
            .getPublicChannel()
            .sendMessage(msg.build());
    }

    /**
     * Gets a String of the Roles that have been removed without all the numbers in front of it
     * @param event A GuildMemberRoleEvent to get all the roles from 
     * @return a strign with only the Role in it
     */
    private String getRoleAsStringRemove(GuildMemberRoleRemoveEvent event)
    {
        List<Role> roles = event.getRoles();
        String role = "";

        role = roles.get(0)
            .getAsMention();
        role.replaceAll("[[//d]]*", "");
        return role;
    }

    /**
     * Gets a String of the Roles that have been added without the numbers 
     * @param event a GuildMemmberRoleAddEvent to get all the roles from
     * @return A String with only the roles in it
     */
    private String getRoleAsStringAdd(GuildMemberRoleAddEvent event)
    {
        List<Role> roles = event.getRoles();
        String role = "";

        role = roles.get(0)
            .getAsMention();
        role.replaceAll("[[//d]]*", "");

        return role;
    }
}
