package concurrent.channel.impl;

import concurrent.channel.Channel;

public class ChannelMain {
	private static Channel c = new ChannelImpl(3);
	
    private static Runnable t1 = new Runnable() {
        public void run() {
            try{
                for(int i=0; i<5; i++){
                	System.out.println("t1, i = " + i);
                    c.putMessage("t1 message: " + i);
                }
            } catch (Exception e){}
        }
    };
 
    private static Runnable t2 = new Runnable() {
        public void run() {
            try{
                for(int i=0; i<5; i++){
                    String message = c.takeMessage();
                	System.out.println("t2 ||" + message);
                }
            } catch (Exception e){}
       }
    };

	public static void main(String[] args) throws InterruptedException {
		
		Thread t01 = new Thread(t1);
		Thread t02 = new Thread(t2);
		
		t01.start();
        t02.start();
        
        t01.join();
        t02.join();
	}
}

