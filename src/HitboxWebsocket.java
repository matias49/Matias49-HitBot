import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

public class HitboxWebsocket extends WebSocketClient {

	static String token;
	static String botName;
	static boolean connected;
	
	public static String getServerIP() {

		String serverList;

		// Recover the first server_ip given by the server
		serverList = HitboxWebsocket
				.stringConnection("http://api.hitbox.tv/chat/servers.json?redis=true");
		int serverIP1 = serverList.indexOf("server_ip\":\"");

		// First number of the IP
		int startIP = serverIP1 + 12;
		System.out.println(serverIP1);
		System.out.println(serverList.charAt(startIP));
		// Recover the last IP
		int endIP = serverList.indexOf("\"", startIP);
		System.out.println(endIP);
		System.out.println(serverList.charAt(endIP - 1));

		// Recover the entire IP
		String IP = serverList.substring(startIP, endIP);
		// System.out.println(IP); // DEBUG LINE - print the IP recovered

		return IP;
	}

	public static String getConnectionID(String IP) {

		// gets the connectionID of the server
		String connectionID = HitboxWebsocket.stringConnection("http://" + IP
				+ "/socket.io/1/");

		// Gets the position of the first colon
		int colon = connectionID.indexOf(":");
		String ID = connectionID.substring(0, colon);

		System.out.println(ID);

		return ID;
	}

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
			System.out.println(urlParameters);
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
			
			
			int token1 = authToken.indexOf("authToken\":\"");
			int startToken = token1 + 12;
			System.out.println(authToken.charAt(startToken));

			int endToken = authToken.indexOf("\"", startToken);
			token = authToken.substring(startToken, endToken);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
		System.out.println(token);
		return token;
	}

	
	// Constructors
	public HitboxWebsocket(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public HitboxWebsocket(URI serverURI) {
		super(serverURI);
	}
	
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// TODO Auto-generated method stub
		System.out.println("new connection opened");
		connected = true;
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		System.out.println("received message: " + message);
		
		// if ping
		if (message.equals("2::")) {
			// pong
			 this.send("2::");
		}

	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		System.out.println("closed with exit code " + code
				+ " additional info: " + reason);

	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		System.err.println("an error occured:" + ex);
	}
	
	public void sendMessage(String channel, String message){
		System.out.println("Sending message...");
		this.send("5:::{\"name\":\"message\",\"args\":[{\"method\":\"chatMsg\",\"params\":{\"channel\":\""+channel+"\",\"name\":\"Matias49\",\"nameColor\":\"FA5858\",\"text\":\"BOT - "+message+"\"}}]}");
		
	}

	public void joinChannel(String channel) {
		System.out.println("Joining...");
		// TODO Auto-generated method stub
		this.send("5:::{\"name\":\"message\",\"args\":[{\"method\":\"joinChannel\",\"params\":{\"channel\":\""+channel+"\",\"name\":\"Matias49\",\"token\":\""
				+ token + "\",\"isAdmin\":false}}]}");

	}

}
