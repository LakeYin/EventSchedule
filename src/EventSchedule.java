import java.io.BufferedReader;
import java.io.FileReader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class EventSchedule 
{
	public static void main(String[] args) throws Exception
	{
		BufferedReader tokenReader = new BufferedReader(new FileReader("../EventSchedule/TOKEN.txt"));
		final String BOT_TOKEN = tokenReader.readLine();
		tokenReader.close();
		
		JDA bot = new JDABuilder(BOT_TOKEN)
				.addEventListeners(new BotCommands())
				.addEventListeners(new BotScheduler())
				.build().awaitReady();
	}
}
