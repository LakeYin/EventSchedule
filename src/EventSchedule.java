import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class EventSchedule 
{
	public static void main(String[] args) throws Exception
	{
		LogManager logManager = LogManager.getLogManager();
		Logger logger = logManager.getLogger(Logger.GLOBAL_LOGGER_NAME);
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		final String databasePath = "/EventSchedule/database/eventData.db";
		
		// find and read the bot token from a text file
		BufferedReader tokenReader = new BufferedReader(new FileReader("../EventSchedule/TOKEN.txt"));
		final String botToken = tokenReader.readLine();
		tokenReader.close();
		
		// build the bot
		JDA bot = new JDABuilder(botToken)
				.addEventListeners(new BotCommands(databasePath, logger))
				.addEventListeners(new BotScheduler(databasePath, logger))
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
