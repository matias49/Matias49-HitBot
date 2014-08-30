import java.io.Console;

/**
 * Main class of the program. Contains only the main initializer method
 * @author Matias49
 *
 */
public class Main {
	
	/**
	 * Main initializer method.
	 * This is the method called when you launch the .jar. It creates the bot following the user inputs
	 * @param args
	 */
	public static void main(String[] args) {
		// Presentation
		System.out.println("---------- Matias49 IRC BOT for Hitbox. ----------");
		System.out.println("---------- Source code of the bot :  ----------");
		System.out.println("---------- Reddit post on /r/hitbox ----------");
		System.out.println("---------- Thanks for using my pre-alpha version of my bot. ----------");
		System.out.println("---------- Feel free to post any comments of the application on the Github page or on the Reddit topic. ----------");
		System.out.println("---------- Thanks a lot for helping me improve the bot. Matias49 ----------");
		System.out.println("---------- LAUNCH ----------");
		System.out.println("---------- The application will ask you for some information. ----------");
		System.out.println("---------- The bot name, the password of the bot name account, the channel the bot must join and the bot owner (will have access to admin commands, like !add and !remove. ----------");
		System.out.println("---------- The bot account can be your personnal HitBox account. Just write the same username in BotName and botOwner fields. ----------");
		// Asking for bot name
		System.out.println("\n\nPlease write your bot name.");
		String botName = System.console().readLine().trim().toLowerCase();
		
		// Asking for the password of the bot name
		System.out.println("Please write the password of the bot account (input hidden).");
		String password = String.valueOf(System.console().readPassword()).trim();
		
		// Asking for the channel the bot has to join
		System.out.println("Please write the name of the channel the bot must join.");
		String channel = System.console().readLine().trim().toLowerCase();
		
		// Asking for the username of the bot owner
		System.out.println("Please write the username of the bot owner. This account will have access to admin commands. (CASE SENSITIVE)");
		String botOwner = System.console().readLine().trim();
//		
		TheBot bot = new TheBot(botName, password, channel, botOwner);
		
		// Add a bot :
		// TheBot nameOfTheBot = new TheBot("NameOfTheBot","Password","ChannelTheBotMustJoin","NameOfTheBotOwner");
	}

}
