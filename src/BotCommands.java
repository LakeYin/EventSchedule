import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotCommands extends ListenerAdapter 
{
	private Connection database;
	
	public BotCommands(String databaseFilePath) throws Exception
	{
		super();
		Class.forName("org.sqlite.JDBC");
		database = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
	}
	
	@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
		if (event.getAuthor().isBot()) return;
		
		// TODO: setup code for utilities
		// - configuring roles for event creation
		// - setting event channel
    }
}
