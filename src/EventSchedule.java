import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class EventSchedule 
{
	public static void main(String[] args) throws Exception
	{
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		
		// find and read the bot token from a text file
		BufferedReader tokenReader = new BufferedReader(new FileReader("../EventSchedule/TOKEN.txt"));
		final String BOT_TOKEN = tokenReader.readLine();
		tokenReader.close();
		
		// build the bot
		JDA bot = new JDABuilder(BOT_TOKEN)
				.addEventListeners(new BotCommands())
				.addEventListeners(new BotScheduler())
				.build().awaitReady();
		
		// set up the notification lambda
		Runnable notifyUsers = () -> {
			// TODO: set up code to notify users of an event
		};
		
		Calendar nextMinute = Calendar.getInstance();
		nextMinute.add(Calendar.MINUTE, 1);
		nextMinute.set(Calendar.SECOND, 0);
		
		// check every minute starting from the next minute on the clock
		long millisUntilNextMinute = nextMinute.getTimeInMillis() - System.currentTimeMillis();
		
		ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(notifyUsers, millisUntilNextMinute, 60 * 1000, TimeUnit.MILLISECONDS);
	}
}
