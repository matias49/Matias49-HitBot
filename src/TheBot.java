import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;

/** TODO LIST
 * 1 - Universal constructor // DONE
 * 2 - add command method // DONE
 * 2.5 - DOCUMENTATION // DONE
 * 3 - Some Tests // DONE
 * 4 - Github pre-alpha // DONE
 * 5 - /r/hitbox // DONE
 * 5.5 - Switch to websockets (Thanks /user/Ertzel) // DONE
 * 5.6 - Work on substring commands // DONE
 * 5.7 - DOCUMENTATION // DONE
 * 5.8 - New version on Github // DONE 
 * 5.9 - Working on avoid command loop if "BOT -" is removed on HitboxWebsocket.sendMessage // DONE
 * 6 - Working on !help command
 * 7 - Handling !add and !remove commands exceptions
 * 8 - Rename some variables/methods // REMOVED - did with 5.5
 * 9 - Rework of the channel variable (bot will join multiple channels)
 * 10 - Exporting all messages to another class (better work on translation)
 * @author Matias49
 *
 */

/**
 * Main class of the program and the bot Constructor called by Main class is
 * defined here. All the behavior of the bot (commands, messages) is here
 * 
 * @author Matias49
 *
 */
public class TheBot {

	private String channel;
	private String master;
	private Commands commands = new Commands();
	private StringWebSocket stringActions = new StringWebSocket();
	private HitboxWebsocket client;
	
	public static String lastMessage;

	/**
	 * Class constructor Create an instance of TheBot and tries to connect on
	 * the Hitbox chat servers via Websocket
	 *
	 * @param botName
	 *            - The name the bot will use
	 * @param password
	 *            - The password of the bot account
	 * @param channel
	 *            - The channel the bot must join (unique channel)
	 * @param master
	 *            - The owner of the bot (will have access to admin commands)
	 */

	public TheBot(String botName, String password, String channel, String master) {
		// Setting variables
		this.channel = channel.toLowerCase();
		this.master = master;
		
		// Loading the existing commands of the channel
		commands.load(this.channel);

		// Launch the connection
		this.conection(botName, password);

	}

	/**
	 * connection method Calls necessary methods to launch the websocket
	 * 
	 * @param password - The password given by the user
	 * @param botName - The name of the bot account
	 */
	private void conection(String botName, String password) {

		// Recover all the information to launch the connection to the chat
		System.out.println("Getting Hitbox server IP");
		String IP = HitboxWebsocket.getServerIP();
		System.out.println("Getting connection ID");
		String ID = HitboxWebsocket.getConnectionID(IP);
		System.out.println("Getting your token");
		HitboxWebsocket.getToken(botName, password);

		try {
			System.out.println("Connecting to hitbox servers...");
			client = new HitboxWebsocket(new URI("ws://" + IP
					+ "/socket.io/1/websocket/" + ID), new Draft_10(), this);
			client.connectBlocking();
			client.joinChannel(channel);
			Thread.sleep(1000);
			client.sendMessage(channel, "Hello! I'm a bot!");
			// French Translation : Bonjour je suis un bot !

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Method called on each message the bot receives
	 * @param newMessage - the line the bot received
	 */
	public void newMessage(String newMessage) {
		// If the bot isn't joining the channel
		if (!HitboxWebsocket.joiningChannel) {
			
			// If the line received is a message
			if (stringActions.isMessage(newMessage)) {
				String theMessage = stringActions.getMessage(newMessage);
				
				// If the message is a command and if the command isn't what the bot sent last time
				if (theMessage.startsWith("!") && !theMessage.equals(lastMessage)) {
					this.execCommand(theMessage,
							stringActions.isOwner(newMessage), stringActions.getUserName(newMessage));
				}
			}
		}
	}

	/**
	 * Executes all the behavior of the bot when a command is called
	 * @param theMessage - The message of the user
	 * @param isOwner - If the message is sent by the owner
	 * @param userName - The username who sent the message
	 */
	private void execCommand(String theMessage, boolean isOwner, String userName) {
		String command;

		// Recover the command. Might be optimizable but I don't know how.
		int messageSize = theMessage.length();

		// if the user just want to show the message of a command and not
		// adding or removing a command, his input doesn't terminate by a
		// space
		if (!theMessage.contains(" ")) {
			command = theMessage.substring(0, theMessage.length());
		} else {
			int spaceCommand = theMessage.indexOf(" ");
			command = theMessage.substring(0, spaceCommand);
		}

		// System.out.println(command); // DEBUG LINE - write the String
		// contained in the command variable

		// If the command exists in the channel, the bot will write in the
		// channel the message assigned with the command.
		if (commands.exists(command)) {
			String messageReturn = commands.returnMessage(command);
			client.sendMessage(channel, messageReturn);
		}

		// If the command doesn't exist, it might be a special command with
		// special code, like !add or !remove.
		// Might be also optimizable for !add and !remove commands but I
		// don't know how.

		// Add your personal commands in the switch below
		else {
			switch (command) {
			/**
			 * !add command add the command to the commands list (nameCommand)
			 * parameters : name of the command and message
			 */
			// Add a command to the channel
			// Work : !add nameFunction Message of the function
			case "!add":
				// If the command is executed by the bot owner
				if (isOwner || userName.equals(master)) {
					// Recover the name of the command to add
					int endNameCommandSpaceAdd = theMessage.indexOf(" ",
							command.length() + 1);
					String nameCommandAdd = theMessage.substring(
							command.length() + 1, endNameCommandSpaceAdd);

					// System.out.println(nameCommandAdd); // DEBUG LINE -
					// print the string contained in nameCommandAdd

					// Recover the message of the command. The message will
					// be
					// printed each time the command is called
					String messageCommand = theMessage.substring(
							endNameCommandSpaceAdd + 1, messageSize);

					// Send the information to the Commands class in order
					// to
					// add the command and write the channel file in case of
					// crashes
					commands.addCommand(nameCommandAdd, messageCommand, channel);
					// We alert the command has been added
					client.sendMessage(channel, "Command added.");
					// French translation : Commande ajoutée

				} else {
					// We inform the user he doesn't have the rights
					client.sendMessage(channel,
							"Tss tss. You don't have the rights. Noob.");
					// French Translation : Tss tss. Vous n'avez pas les
					// droits. Noob.
				}
				break;

			// Remove the command provided to the channel
			// Work : !remove nameFunction
			case "!remove":
				// If the command is executed by the bot owner
				if (isOwner || userName.equals(master)) {
					// Recover the name of the command to remove
					String nameCommandRemove = theMessage.substring(
							command.length() + 1, theMessage.length());
					// System.out.println(nameCommandRemove); // DEBUG LINE
					// - print the string contained in nameCommandRemove
					commands.removeCommand(nameCommandRemove, channel);
					// We alert the command has been added
					client.sendMessage(channel, "Command removed.");
					// French translation : Commande supprimée
				} else {
					// We inform the user he doesn't have the rights
					client.sendMessage(channel,
							"Tss tss. You don't have the rights. Noob.");
					// French translation : Tss tss. Vous n'avez pas les
					// droits. Noob.
				}
				break;

			// Help command. List all available commands
			// Work : !help
			// TODO
			case "!help":
				client.sendMessage(channel,
						"The !help command isn't ready. Please call it later :)");
				// French translation : La commande !help est en cours de
				// rédaction. Patience :)
				break;

			// Personal use. Can be examples for you.

			// Close the bot
			// Work : !close
			case "!close":
				// If the command is executed by the bot owner
				if (isOwner || userName.equals(master)) {
					// Alert the channel the bot is exiting
					client.sendMessage(channel,
							"I'm asked to leave. Goodbye :p");
					// French translation : On m'a demandé de Partir. À
					// bientôt :p
					System.exit(0);
				} else {
					// We inform the user he doesn't have the rights
					client.sendMessage(channel,
							"Tss tss. You don't have the rights. Noob.");
					// French translation : Tss tss. Vous n'avez pas les
					// droits. Noob.
				}
				break;
			// Add the game to the list of games I might stream with the
			// user who sent the game
			// Work : !addGame name of the game
			case "!addGame":

				break;
			// If the command isn't find here, so it doesn't exist. We'll
			// inform that the command is unknown.
			default:
				client.sendMessage(channel,
						"Unknown command. Please verify your input.");
				// French translation : La commande est inconnue. Veuillez
				// vérifier votre commande envoyée.
				break;
			}
		}
	}

}
