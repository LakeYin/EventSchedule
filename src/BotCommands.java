import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotCommands extends ListenerAdapter 
{
	private Connection database;
	private Logger logger;
	
	public BotCommands(String databaseFilePath, Logger logger) throws Exception
	{
		super();
		Class.forName("org.sqlite.JDBC");
		database = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
		this.logger = logger;
	}
	
	@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
		if (event.getAuthor().isBot()) return;
		
		// TODO: setup code for utilities
		// - configuring roles for event creation
		// - setting event channel
		
		String message = event.getMessage().getContentRaw();
		
		if(message.startsWith("!set_channel "))
		{
			if(event.getMember().hasPermission(Permission.ADMINISTRATOR))
				setEventChannel(event);
			else
				event.getChannel().sendMessage("You do not have permission to change the event channel!");
		}
		else if(message.startsWith("!add_event_role "))
		{
			if(event.getMember().hasPermission(Permission.ADMINISTRATOR))
				addEventRole(event);
			else
				event.getChannel().sendMessage("You do not have permission to add event scheduling roles!");
		}
		else if(message.startsWith("!remove_event_role "))
		{
			if(event.getMember().hasPermission(Permission.ADMINISTRATOR))
				removeEventRole(event);
			else
				event.getChannel().sendMessage("You do not have permission to remove event scheduling roles!");
		}
		else if(message.startsWith("!server_info "))
		{
			getServerInfo(event);
		}
		else if(message.startsWith("!help "))
		{
			getHelp(event);
		}
    }
	
	private void setEventChannel(GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split(" ", 2);
			
		if(args.length != 2 || !(args[1].startsWith("<#") && args[1].endsWith(">")))
		{
			event.getChannel().sendMessage("Improper arguments. (!set_channel <#channel>)");
		}
		else
		{
			try
			{
				Statement statement = database.createStatement();
				String newChannel = args[1].substring(2, args[1].length()-1);
				
				// delete the old entry if it is there and insert the new one
				String replace = String.format("REPLACE INTO mainChannels (guildId, channelId) VALUES (%s, %s);", 
								 event.getGuild().getId(), newChannel);
				
				statement.executeUpdate(replace);
				
				statement.close();
				database.commit();
				
				event.getChannel().sendMessage("Event channel updated to <#" + newChannel + ">");
			}
			catch(SQLException e)
			{
				event.getChannel().sendMessage("There was a problem with the database!");
				logger.log(Level.SEVERE, e.getMessage());
			}
		}
	}
	
	private void addEventRole(GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split(" ", 2);
		
		if(args.length != 2)
		{
			event.getChannel().sendMessage("Improper arguments. (!add_event_role role_name)");
			return;
		}
		
		// try to find the role based on the string provided (we need to find an id)
		List<Role> rolesFound = event.getGuild().getRolesByName(args[1], true);
		
		if(rolesFound.isEmpty())
		{
			event.getChannel().sendMessage("Could not find any corresponding roles.");
		}
		else if(rolesFound.size() > 1)
		{
			event.getChannel().sendMessage("Multiple roles found. Please try a different query.");
		}
		else
		{
			try
			{
				Statement statement = database.createStatement();
				Role role = rolesFound.get(0);
				String insertNew = String.format("INSERT INTO schedulePerms (guildId, roleId) VALUES (%s, %s);", 
								   event.getGuild().getId(), role.getId());
				
				statement.executeUpdate(insertNew);
				
				statement.close();
				database.commit();
				
				event.getChannel().sendMessage(role.getAsMention() + " added as a role with event scheduling permission.");
			}
			catch(SQLException e)
			{
				event.getChannel().sendMessage("There was a problem with the database!");
				logger.log(Level.SEVERE, e.getMessage());
			}
		}
	}
	
	private void removeEventRole(GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split(" ", 2);
		
		if(args.length != 2)
		{
			event.getChannel().sendMessage("Improper arguments. (!add_event_role role_name)");
			return;
		}
		
		// try to find the role based on the string provided (we need to find an id)
		List<Role> rolesFound = event.getGuild().getRolesByName(args[1], true);
		
		if(rolesFound.isEmpty())
		{
			event.getChannel().sendMessage("Could not find any corresponding roles.");
		}
		else if(rolesFound.size() > 1)
		{
			event.getChannel().sendMessage("Multiple roles found. Please try a different query.");
		}
		else
		{
			try
			{
				Statement statement = database.createStatement();
				Role role = rolesFound.get(0);
				String findRole = String.format("SELECT * FROM schedulePerms WHERE guildId = %s AND roleId = %s;", 
								  event.getGuild().getId(), role.getId());
				String deleteRole = String.format("DELETE FROM schedulePerms WHERE guildId = %s AND roleId = %s;", 
						   			event.getGuild().getId(), role.getId());
				
				// check if the role is recorded to have event setting permission
				if(statement.execute(findRole))
				{
					statement.executeUpdate(deleteRole);
					event.getChannel().sendMessage(role.getAsMention() + " deleted as a role with event scheduling permission.");
					database.commit();
				}
				else
				{
					event.getChannel().sendMessage(role.getAsMention() + " is not a role with event scheduling permission.");
				}
				
				statement.close();
			}
			catch(SQLException e)
			{
				event.getChannel().sendMessage("There was a problem with the database!");
				logger.log(Level.SEVERE, e.getMessage());
			}
		}
	}
	
	private void getServerInfo(GuildMessageReceivedEvent event)
	{
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setTitle("Event Schedule Server Information");
		builder.setDescription("Server Information for " + event.getGuild().getName());
		
		String eventChannel = "Not set";
		List<String> roles = new ArrayList<String>();
		
		try
		{
			Statement statement = database.createStatement();
			String getChannel = String.format("SELECT channelId FROM mainChannels WHERE guildId = %s", 
					  			event.getGuild().getId());
			String getRoles = String.format("SELECT roleId FROM schedulePerms WHERE guildId = %s", 
		  					  event.getGuild().getId());
			
			// a Statement can only have one ResultSet at a time so we need this order
			// first get the channel
			ResultSet eventChannelIdSet = statement.executeQuery(getChannel);
			String[] eventChannelId = (String[])eventChannelIdSet.getArray(1).getArray();
			if(eventChannelId.length > 0)
				eventChannel = "<#" + eventChannelId[0] + ">";
			eventChannelIdSet.close();
			
			// then the roles
			ResultSet roleIdSet = statement.executeQuery(getRoles);
			String[] roleIds = (String[])roleIdSet.getArray(1).getArray();
			for(String id : roleIds)
				roles.add("<@&" + id + ">");
			roleIdSet.close();
			
			statement.close();
		}
		catch(SQLException e)
		{
			event.getChannel().sendMessage("There was a problem with the database!");
			logger.log(Level.SEVERE, e.getMessage());
		}
		
		StringBuilder roleString = new StringBuilder();
		for(String role : roles)
			roleString.append(role + "\n");
		
		builder.addField("Event Channel", eventChannel, false);
		builder.addField("Roles with scheduling permission", roleString.toString(), false);
		
		builder.setFooter("Type !get_help to see commands.");
		
		event.getChannel().sendMessage(builder.build());
	}
	
	private void getHelp(GuildMessageReceivedEvent event)
	{
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setTitle("EventSchedule Commands");
		builder.addField("!get_help", "Displays this message.", false);
		builder.addField("!set_channel #channel", "Sets the channel this bot posts events on.", false);
		builder.addField("!add_event_role role_name", "Gives this role permission to create and cancel events.", false);
		builder.addField("!remove_event_role role_name", "Remove this role's permission to create and cancel events.", false);
		builder.addField("!server_info", "Displays the current event channel and the roles with event creation/cancelling permission.", false);
		
		// TODO: add info for event creation
		
		event.getChannel().sendMessage(builder.build());
	}
}