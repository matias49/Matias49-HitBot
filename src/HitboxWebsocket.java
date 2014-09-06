import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public class HitboxWebsocket extends WebSocketClient {

	static String token;
	static String botName;
	static boolean connected;
	static boolean joiningChannel = false;
	public TheBot bot;
	
	/**
	 * Gets the list of available IPs for the connection and substring the first IP given
	 * @return The IP of the first server given by the server
	 */
	public static String getServerIP() {

		String serverList;

		// Recover the first server_ip given by the server
		serverList = HitboxWebsocket
				.stringConnection("http://api.hitbox.tv/chat/servers.json?redis=true");
		int serverIP1 = serverList.indexOf("server_ip\":\"");

		// First number of the IP
		int startIP = serverIP1 + 12;
//		System.out.println(serverIP1); // DEBUG LINE
//		System.out.println(serverList.charAt(startIP)); // DEBUG LINE - show the character at the position recovered
		// Recover the last IP
		int endIP = serverList.indexOf("\"", startIP);
//		System.out.println(endIP); // DEBUG LINE
//		System.out.println(serverList.charAt(endIP - 1)); DEBUG LINE

		// Recover the entire IP
		String IP = serverList.substring(startIP, endIP);
		// System.out.println(IP); // DEBUG LINE - print the IP recovered

		return IP;
	}

	/**
	 * Gets the connection ID given by HitBox servers
	 * @param IP - the IP given by the getServerIP method
	 * @return - the connecion ID in order to connect the bot
	 */
	public static String getConnectionID(String IP) {

		// gets the connectionID of the server
		String connectionID = HitboxWebsocket.stringConnection("http://" + IP
				+ "/socket.io/1/");

		// Gets the position of the first colon
		int colon = connectionID.indexOf(":");
		String ID = connectionID.substring(0, colon);

//		System.out.println(ID); // DEBUG LINE - Prints the ID given by the server

		return ID;
	}

	/**
	 * Method used to connect to the given URL
	 * @param urlString - The URL to connect
	 * @return - the String returned by the URL
	 */
	public static String stringConnection(String urlString) {

		HttpURLConnection connection = null;
		StringBuffer response = new StringBuffer();

		try {
			// Create connection
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();

			// Get Response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}

		return response.toString();
	}

	/**
	 * Launch a connection to the HitBox server in order to retrieve the token to allow the bot to connect to the chat
	 * 
	 * @param user - the username given by the user
	 * @param password - the password of the account
	 * @return - The token if the connection is successful / Exit(0) if the credentials are wrong
	 */
	public static String getToken(String user, String password) {
		
		botName = user;
		
		HttpURLConnection connection = null;
		StringBuffer response = new StringBuffer();
		
		
		try {
			// Create connection
			URL url = new URL("http://api.hitbox.tv/auth/token");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");

			// Parameters

			String urlParameters = "login="+botName+"&pass="+password+"&app=desktop";
//			System.out.println(urlParameters);  // DEBUG LINE - SHOW THE PASSWORD GIVEN BY THE USER ON THE CONSOLE
			// Send post request
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// Get Response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();
			String authToken = response.toString();
			
			// Find where is the token
			int token1 = authToken.indexOf("authToken\":\"");
			int startToken = token1 + 12;
//			System.out.println(authToken.charAt(startToken)); // DEBUG LINE

			int endToken = authToken.indexOf("\"", startToken);
			// Store the token in the class
			token = authToken.substring(startToken, endToken);

			
		} catch (IOException e) {
			// If credentials are wrong
			System.out.println("Wrong credentials. Try again");
			System.exit(0);
		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
//		System.out.println(token); // DEBUG LINE
		return token;
	}

	
	// Constructors
	public HitboxWebsocket(URI serverUri, Draft draft, TheBot bot) {
		super(serverUri, draft);
		this.bot = bot;
	}

	public HitboxWebsocket(URI serverURI) {
		super(serverURI);
	}
	/**
	 * 
	 * If the connection is succesfull, this method is called in order to continue
	 * 
	 */
	/* (non-Javadoc)
	 * @see org.java_websocket.client.WebSocketClient#onOpen(org.java_websocket.handshake.ServerHandshake)
	 */
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// TODO Auto-generated method stub
		System.out.println("Connected to hitbox server");
		connected = true;
	}

	/**
	 * Method called on each received message by the bot
	 */
	/* (non-Javadoc)
	 * @see org.java_websocket.client.WebSocketClient#onMessage(java.lang.String)
	 */
	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
//		System.out.println("received message: " + message); // DEBUG LINE
		// if ping
		if (message.equals("2::")) {
			// pong
			 this.send("2::");
		}
		// If this is another important message
		else if (message.startsWith("5:::")){
			// The bot will take it
			bot.newMessage(message);
		}

	}

	/* (non-Javadoc)
	 * @see org.java_websocket.client.WebSocketClient#onClose(int, java.lang.String, boolean)
	 */
	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		System.out.println("closed with exit code " + code
				+ " additional info: " + reason);

	}

	/* (non-Javadoc)
	 * @see org.java_websocket.client.WebSocketClient#onError(java.lang.Exception)
	 */
	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		System.err.println("an error occured:" + ex);
		ex.printStackTrace();
	}
	

	/**
	 * Joins the channel given by the user
	 * @param channel - the channel to join
	 */
	public void joinChannel(String channel) {
		joiningChannel = true;
		System.out.println("Joining channel...");
		this.send("5:::{\"name\":\"message\",\"args\":[{\"method\":\"joinChannel\",\"params\":{\"channel\":\""+channel+"\",\"name\":\"Matias49\",\"token\":\""
				+ token + "\",\"isAdmin\":false}}]}");
		try {
			// Sleep to avoid the bot react to older messages
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Channel joined");
		joiningChannel = false;

	}
	
	public void sendMessage(String channel, String message){
		System.out.println("Sending message : " + message);
		this.send("5:::{\"name\":\"message\",\"args\":[{\"method\":\"chatMsg\",\"params\":{\"channel\":\""+channel+"\",\"name\":\"Matias49\",\"nameColor\":\"FA5858\",\"text\":\"BOT - "+message+"\"}}]}");
		// Store the message sent in order to avoid loops
		TheBot.lastMessage = message;
	}

}
