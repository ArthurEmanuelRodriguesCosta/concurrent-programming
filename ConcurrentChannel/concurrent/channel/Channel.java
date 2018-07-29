package concurrent.channel;

public interface Channel {
	
	public void putMessage(String message);
	
	public String takeMessage();
}

