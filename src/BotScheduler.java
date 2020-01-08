import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotScheduler extends ListenerAdapter
{
	private Connection database;
	private Logger logger;
	
	public BotScheduler(String databaseFilePath, Logger logger) throws Exception
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
		
		// TODO: setup code for creating or deleting events
    }
	
	@Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
    {
		if (event.getUser().isBot() || !isSelf(event)) return;
		
		// TODO: setup code for RSVPing to an event
    }
	
	@Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event)
    {
		if (event.getUser().isBot() || !isSelf(event)) return;
		
		// TODO: setup code for removing an RSVP
    }
	
	// checks if the author of the message being reacted to is the JDA
	private boolean isSelf(GenericGuildMessageReactionEvent event)
	{
		return event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete().getAuthor().getIdLong() == 
				event.getJDA().getSelfUser().getIdLong();
	}
}
