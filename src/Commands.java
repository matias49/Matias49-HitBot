import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * The Commands class is used to store all commands of a specific channel.
 * Each bot will have an instance of the class. Has add and remove commands methods
 * 
 * Might change fast because of HashMap limitations. Might create a command Class with key, message, channel and adminOnly variables
 * 
 * @author Matias49
 *
 * TODO
 * 1 - add and remove methods // DONE
 */
public class Commands implements Serializable {
	
	// ID for serialization
	private static final long serialVersionUID = -2725048107664973185L;
	
	// Hash map of commands. Key is the name of the command and Value the message the bot will write each time the command is called
	HashMap<String, String> commands = new HashMap<String, String>();
	
	/**
	 * Constructor
	 */
	public Commands() {
	}

	/**
	 * Write the HashMap into a file to avoid loss on crash. Executed on each modification of the HashMap
	 * @param channel - the name of the channel. The file will have its name
	 */
	public void write(String channel) {
		// TODO Auto-generated method stub
		try {
			FileOutputStream writeFile = new FileOutputStream(channel+".ser");
				ObjectOutputStream oos = new ObjectOutputStream(writeFile);
				oos.writeObject(commands);
				oos.close();
				writeFile.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Load the HashMap of the channel. Only executed when the bot is connected to the server
	 * @param channel - the name of the channel. The bot will load its file
	 */
	public void load(String channel) {
		// TODO Auto-generated method stub
		try {
			FileInputStream readFile = new FileInputStream(channel+".ser");
				ObjectInputStream ois = new ObjectInputStream(readFile);
				this.commands = (HashMap) ois.readObject();
				ois.close();
				readFile.close();
		} 
		// If the file doesn't exist, it'll be created.
		catch (FileNotFoundException e) {
			System.out.println("Fichier du channel introuvable. Création en cours..."); // DEBUG LINE - The channel file doesn't exist. It'll be created
			// TODO Auto-generated catch block
			e.printStackTrace();
			// Creation of the channel file
			this.write(channel);
			System.out.println("Fichier crée"); // DEBUG LINE - print if the file is created
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Find if the command provided by the user exists in this channel
	 * @param command - the command to execute
	 * @return true if the command exists, false otherwise
	 */
	public boolean exists(String command) {
		return commands.containsKey(command);
	}

	/**
	 * Return the message of the command provided by the user
	 * The message will be written by the bot on the channel
	 * @param command - the command to execute
	 * @return The string the bot will write
	 */
	public String returnMessage(String command) {
		return commands.get(command);
	}
	
	/**
	 * Add the command to the HashMap of the channel
	 * @param key - the name of the command
	 * @param value - the message that will be printed each time the command is called
	 * @param channel - the channel of the command in order to write the file
	 */
	public void addCommand(String key, String value, String channel){
		// If the command name doesn't start with !, we'll add it before adding it to the HashMap
		if (!key.startsWith("!")){
			key = "!"+key;
		}
		commands.put(key, value);
		
		// Update the channel file in case of crashes
		this.write(channel);
	}

	/**
	 * Remove the command to the HashMap of the channel
	 * @param nameCommandRemove
	 * @param channel
	 */
	public void removeCommand(String nameCommandRemove, String channel) {
		// If the command name doesn't start with !, we'll add it before removing it to the HashMap
		if (!nameCommandRemove.startsWith("!")){
			nameCommandRemove = "!"+nameCommandRemove;
		}
		commands.remove(nameCommandRemove);
		
		// Update the channel file in case of crashes
		this.write(channel);
		
	}
	
	
}
