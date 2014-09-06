
/**
 * StringWebSocket class
 * This class contains all the methods used to recover all important information given by a websocket line
 * @author Matias49
 *
 */
public class StringWebSocket {

	/**
	 * Verify is the line received is a message
	 * @param newMessage - The message recieved
	 * @return true if the line contains a message, false otherwise
	 */
	public boolean isMessage(String newMessage) {
		if (this.getMethod(newMessage).equals("chatMsg")){
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Substring the method of the message recieved
	 * @param newMessage - the message recieved
	 * @return - the method of the message recieved
	 */
	public String getMethod(String newMessage) {
		
		// Where is the method name
		int startMethodPlace = newMessage.indexOf("method");
		int startMethodString = startMethodPlace + 11;
		int endMethodString = newMessage.indexOf("\"", startMethodString);
		
		String method = newMessage.substring(startMethodString, endMethodString-1);
//		System.out.println(method); // DEBUG LINE
		return method;
		
		
	}
	
	/**
	 * Substring the message sended by the user in the line the bot recieved
	 * @param newMessage - the message recieved
	 * @return - the user input
	 */
	public String getMessage(String newMessage) {
		
		// Where is the user input
		int startMessagePlace = newMessage.indexOf("text");
		int startMessageString = startMessagePlace + 9;
		int endMessageString = newMessage.indexOf("\"", startMessageString);
		
		String message = newMessage.substring(startMessageString, endMessageString-1);
//		System.out.println(message); // DEBUG LINE
		return message;
		
	}
	
	/**
	 * Get the channel where comes the message
	 * @param newMessage - the message recieved
	 * @return the channel name
	 */
	public String getChannel(String newMessage){
		
		// Where is the channel name
		int startChannelPlace = newMessage.indexOf("channel");
		int startChannelString = startChannelPlace + 12;
		int endChannelString = newMessage.indexOf("\"", startChannelString);
		
		String channel = newMessage.substring(startChannelString, startChannelString-1);
//		System.out.println(channel); // DEBUG LINE
		return channel;
	}
	
	/**
	 * Get the username who sent the message
	 * @param newMessage - the message recieved
	 * @return the username
	 */
	public String getUserName(String newMessage) {
		
		int startNamePlace = newMessage.indexOf("name",newMessage.indexOf("channel"));
		int startNameString = startNamePlace + 9;
		int endNameString = newMessage.indexOf("\"", startNameString);
		
		String name = newMessage.substring(startNameString, endNameString-1);
//		System.out.println(name); // DEBUG LINE
		return name;
		
	}
	
	/**
	 * Verify is the line received is sent by the owner of the channel
	 * @param newMessage - The message recieved
	 * @return true if the line is sent by the owner, false otherwise
	 */
	public boolean isOwner(String newMessage) {
		
		int startIsOwnerPlace = newMessage.indexOf("isOwner");
		int startIsOwnerString = startIsOwnerPlace + 10;
		int endIsOwnerString = newMessage.indexOf("\"", startIsOwnerString);
		
		String isFollower = newMessage.substring(startIsOwnerString, endIsOwnerString-2);
//		System.out.println(isFollower); // DEBUG LINE
		if (isFollower.equals("true")){
			return true;
		}
		else {
			return false;
		}
		
	}
	
	
	/**
	 * Verify is the line received is sent by a follower of the channel
	 * @param newMessage - The message recieved
	 * @return true if the line is sent by a follower, false otherwise
	 */
	public boolean isFollower(String newMessage) {
		
		int startIsFollowerPlace = newMessage.indexOf("isFollower");
		int startIsFollowerString = startIsFollowerPlace + 15;
		int endIsFollowerString = newMessage.indexOf("\"", startIsFollowerString);
		
		String isFollower = newMessage.substring(startIsFollowerString, endIsFollowerString-1);
//		System.out.println(isFollower); // DEBUG LINE
		if (isFollower.equals("true")){
			return true;
		}
		else {
			return false;
		}
		
	}
	
	
	/**
	 * Verify is the line received is sent by a subscriber of the channel
	 * @param newMessage - The message recieved
	 * @return true if the line is sent by a subscriber, false otherwise
	 */
	public boolean isSubscriber(String newMessage) {
		
		int startIsSubscriberPlace = newMessage.indexOf("isSubscriber");
		int startIsSubscriberString = startIsSubscriberPlace + 17;
		int endIsSubscriberString = newMessage.indexOf("\"", startIsSubscriberString);
		
		String isSubscriber = newMessage.substring(startIsSubscriberString, endIsSubscriberString-1);
//		System.out.println(isSubscriber); // DEBUG LINE
		if (isSubscriber.equals("true")){
			return true;
		}
		else {
			return false;
		}
		
	}
}
