import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotScheduler extends ListenerAdapter 
{
	@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
		if (event.getAuthor().isBot()) return;
		
		// TODO: setup code for creating or deleting events
    }
	
	@Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
    {
		if (event.getUser().isBot()) return;
		
		// TODO: setup code for RSVPing to an event
    }
	
	@Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event)
    {
		if (event.getUser().isBot()) return;
		
		// TODO: setup code for removing an RSVP
    }
}
