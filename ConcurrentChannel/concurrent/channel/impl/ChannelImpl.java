package concurrent.channel.impl;

import java.util.concurrent.ArrayBlockingQueue;

import concurrent.channel.Channel;

public class ChannelImpl implements Channel {
	
    private ArrayBlockingQueue<String> buffer;
    
    public ChannelImpl(int bufferSize) {
    	this.buffer = new ArrayBlockingQueue<String>(bufferSize, true);
    }

	@Override
	public void putMessage(String message) {
		try {
			buffer.put(message);
			System.out.println("put " + buffer);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String takeMessage() {
		String message = null;
		try {
			message = buffer.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ttake " + buffer);
		return message;
	}

}
