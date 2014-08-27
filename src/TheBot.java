import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import org.jibble.*;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

/** TODO LIST
 * 1 - Universal constructor // DONE
 * 2 - add command method // DONE
 * 2.5 - DOCUMENTATION // DONE
 * 3 - Some Tests // DONE
 * 4 - Github pre-alpha
 * 5 - /r/hitbox
 * 6 - Working on !help command
 * 7 - Handling !add and !remove commands exceptions
 * 8 - Rename some variables/methods
 * 9 - Rework of the channel variable (bot will join multiple channels)
 * 10 - Exporting all messages to another class (better work on translation)
 * @author Matias49
 *
 */

/**
 * Main class of the program and the bot Constructor called by Main class is
 * defined here. All the behavior of the bot (commands, messages) is here For
 * more information about pircBot commands see :
 * http://www.jibble.org/pircbot.php
 * 
 * @author Matias49
 *
 */
public class TheBot extends PircBot {

	private String channel;
	private String botName;
	private String master;
	private final String IRCServer = "irc.glados.tv";
	private final int IRCPort = 6666;
	private String password;
	private Commands commands;

	/**
	 * Class constructor Create an instance of TheBot and tries to connect on
	 * the IRC server provided
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
		this.botName = botName;
		this.password = password;

		// If the user didn't insert the channel name with a # as first
		// character, it will be added
		if (!channel.startsWith("#")) {
			channel = "#" + channel;
		}
		this.channel = channel.toLowerCase();
		this.master = master;

		// setName method is part of PircBOT. Documentation :
		// http://www.jibble.org/javadocs/pircbot/org/jibble/pircbot/PircBot.html#setName(java.lang.String)
		this.setName(botName);

		// Debug - true to more outputs on the terminal - PircBOT method.
		// Documentation :
		// http://www.jibble.org/javadocs/pircbot/org/jibble/pircbot/PircBot.html#setVerbose(boolean)
		this.setVerbose(true);

		// Launch the connection to the server
		this.conection();
	}

	/**
	 * connection method Calls necessary methods to connect to the IRC server
	 */
	private void conection() {

		try {
			// PircBOT method. Documentation of the method :
			// http://www.jibble.org/javadocs/pircbot/org/jibble/pircbot/PircBot.html#connect(java.lang.String,
			// int, java.lang.String)
			this.connect(IRCServer, IRCPort, password);
		}
		// Catching exceptions
		catch (NickAlreadyInUseException e) {
			System.out.println("Nickname already used");
		} catch (IrcException e) {
			System.out.println("IRC error. The server might be offline");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.out
					.println("Unknown host. Verify your internet connection.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * onConnect method is called only if the bot is connected.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jibble.pircbot.PircBot#onConnect() Online documentation :
	 * http://
	 * www.jibble.org/javadocs/pircbot/org/jibble/pircbot/PircBot.html#onConnect
	 * ()
	 */
	public void onConnect() {
		// System.out.println("Connected"); // DEBUG LINE - print if the bot is
		// connected
		// Bot will join the channel provided in the constructor
		this.joinChannel(channel);

		// Create its commands instance.
		commands = new Commands();

		// Loads the existing commands of the channel
		// System.out.println("Chargement des paramètres du channel"); // DEBUG
		// LINE - print the loading of the channel commands
		commands.load(channel);
		this.envoyerMessage(channel, "Hello! I'm a bot!");
		// French translation : Bonjour ! Je suis un bot !
	}

	/**
	 * onMessage This method is called when a message is send in one of the
	 * channels where the bot is connected. Behavior of it is defined here.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jibble.pircbot.PircBot#onMessage(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * Online documentation :
	 * http://www.jibble.org/javadocs/pircbot/org/jibble/pircbot
	 * /PircBot.html#onMessage(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		// Remove all unwanted spaces
		message = message.trim();
		String command;

		// If the message is a command.
		if (message.startsWith("!")) {

			// Recover the command. Might be optimizable but I don't know how.
			int messageSize = message.length();

			// if the user just want to show the message of a command and not
			// adding or removing a command, his input doesn't terminate by a
			// space
			if (!message.contains(" ")) {
				command = message.substring(0, message.length());
			} else {
				int spaceCommand = message.indexOf(" ");
				command = message.substring(0, spaceCommand);
			}

			// System.out.println(command); // DEBUG LINE - write the String
			// contained in the command variable

			// If the command exists in the channel, the bot will write in the
			// channel the message assigned with the command.
			if (commands.exists(command)) {
				String messageReturn = commands.returnMessage(command);
				this.envoyerMessage(channel, messageReturn);
			}

			// If the command doesn't exist, it might be a special command with
			// special code, like !add or !remove.
			// Might be also optimizable for !add and !remove commands but I
			// don't know how.

			// Add your personal commands in the switch below
			else {
				switch (command) {
				/**
				 * !add command add the command to the commands list
				 * (nameCommand) parameters : name of the command and message
				 */
				// Add a command to the channel
				// Work : !add nameFunction Message of the function
				case "!add":
					// If the command is executed by the bot owner
					if (sender.equals(master)) {
						// Recover the name of the command to add
						int endNameCommandSpaceAdd = message.indexOf(" ",
								command.length() + 1);
						String nameCommandAdd = message.substring(
								command.length() + 1, endNameCommandSpaceAdd);

						// System.out.println(nameCommandAdd); // DEBUG LINE -
						// print the string contained in nameCommandAdd

						// Recover the message of the command. The message will
						// be
						// printed each time the command is called
						String messageCommand = message.substring(
								endNameCommandSpaceAdd + 1, messageSize);

						// Send the information to the Commands class in order
						// to
						// add the command and write the channel file in case of
						// crashes
						commands.addCommand(nameCommandAdd, messageCommand,
								channel);
						// We alert the command has been added
						this.envoyerMessage(channel, "Command added.");
						// French translation : Commande ajoutée

					} else {
						// We inform the user he doesn't have the rights
						this.envoyerMessage(channel,
								"Tss tss. You don't have the rights. Noob.");
						// French Translation : Tss tss. Vous n'avez pas les
						// droits. Noob.
					}
					break;

				// Remove the command provided to the channel
				// Work : !remove nameFunction
				case "!remove":
					// If the command is executed by the bot owner
					if (sender.equals(master)) {
						// Recover the name of the command to remove
						String nameCommandRemove = message.substring(
								command.length() + 1, message.length());
						// System.out.println(nameCommandRemove); // DEBUG LINE
						// - print the string contained in nameCommandRemove
						commands.removeCommand(nameCommandRemove, channel);
						// We alert the command has been added
						this.envoyerMessage(channel, "Command removed.");
						// French translation : Commande supprimée
					} else {
						// We inform the user he doesn't have the rights
						this.envoyerMessage(channel,
								"Tss tss. You don't have the rights. Noob.");
						// French translation : Tss tss. Vous n'avez pas les
						// droits. Noob.
					}
					break;

				// Help command. List all available commands
				// Work : !help
				// TODO
				case "!help":
					this.envoyerMessage(channel,
							"The !help command isn't ready. Please call it later :)");
					// French translation : La commande !help est en cours de
					// rédaction. Patience :)
					break;

				// Personal use. Can be examples for you.

				// Close the bot
				// Work : !close
				case "!close":
					// If the command is executed by the bot owner
					if (sender.equals(master)) {
						// Alert the channel the bot is exiting
						this.envoyerMessage(channel,
								"I'm asked to leave. Goodbye :p");
						// French translation : On m'a demandé de Partir. À
						// bientôt :p
						System.exit(0);
					} else {
						// We inform the user he doesn't have the rights
						this.envoyerMessage(channel,
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
					this.envoyerMessage(channel,
							"Unknown command. Please verify your input.");
					// French translation : La commande est inconnue. Veuillez
					// vérifier votre commande envoyée.
					break;
				}
			}
		}

	}

	/**
	 * Method called on each connection on the channel
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jibble.pircbot.PircBot#onJoin(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String) Online
	 * documentation :
	 * http://www.jibble.org/javadocs/pircbot/org/jibble/pircbot/
	 * PircBot.html#onJoin(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	protected void onJoin(String channel, String sender, String login,
			String hostname) {

		// If the streamer joins his channel
		if (sender.equalsIgnoreCase(master)) {
			// Welcome message of the streamer
			// optional, doesn't work if botName = master
			// this.envoyerMessage(channel ,"Bonjour Maître !");
			// Translation : Hello master !
		} else {
			// Welcome message of everyone else
			this.envoyerMessage(channel, "Welcome " + sender
					+ " on my stream :) !help to see available commands");
			// French translation :"Bienvenue " +sender+ " sur le stream de
			// Quairine :) !help pour voir la liste des commandes disponibles

		}
		// You can add more welcome messages for special users. Copy the if
		// statement and replace the master argument per the username needed
	}

	/**
	 * This method is called when a message is send to the channel The
	 * sendMessage method is part of PircBOT. See the documentation :
	 * http://www.
	 * jibble.org/javadocs/pircbot/org/jibble/pircbot/PircBot.html#sendMessage
	 * (java.lang.String, java.lang.String)
	 * 
	 * @param message
	 *            - The message to send to the channel
	 * 
	 */
	public void envoyerMessage(String channel, String message) {
		this.sendMessage(channel, "BOT - " + message);
		// The bot will write "BOT - " followed of the message. You can edit of
		// course.
	}

}
