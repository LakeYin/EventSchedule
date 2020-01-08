import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.dv8tion.jda.api.Permission;
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
			setEventChannel(event);
		}
		else if(message.startsWith("!add_event_role "))
		{
			addEventRole(event);
		}
		else if(message.startsWith("!remove_event_role "))
		{
			removeEventRole(event);
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
		if(event.getMember().hasPermission(Permission.ADMINISTRATOR))
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
					
					String deleteOld = "DELETE FROM mainChannels WHERE guildID = " + event.getGuild().getId() + ";";
					
					String insertNew = String.format("INSERT INTO mainChannels (guildID, channelID) VALUES (%s, %s);", 
									   event.getGuild().getId(), newChannel);
					
					statement.executeUpdate(deleteOld);
					statement.executeUpdate(insertNew);
					
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
		else
		{
			event.getChannel().sendMessage("You do not have permission to change the event channel!");
		}
	}
	
	private void addEventRole(GuildMessageReceivedEvent event)
	{
		
	}
	
	private void removeEventRole(GuildMessageReceivedEvent event)
	{
		
	}
	
	private void getServerInfo(GuildMessageReceivedEvent event)
	{
		
	}
	
	private void getHelp(GuildMessageReceivedEvent event)
	{
		
	}
}