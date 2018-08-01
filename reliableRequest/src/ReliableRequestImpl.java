package src;

import java.util.Scanner;

import interfaces.Request;
import interfaces.Response;


public class ReliableRequestImpl implements Request {

	private static Response response = new ResponseImpl();;
	private static final String[] mirrors = {"mirror1.com", "mirror2.br", "mirror3.edu"};
	
	private Thread t1;
	private Thread t2;
	private Thread t3;
	private volatile boolean stop = false;
	
	private final int MIRROR_1 = 0;
	private final int MIRROR_2 = 1;
	private final int MIRROR_3 = 2;
	
	public ReliableRequestImpl() {
		
	}
	
	@Override
	@Deprecated
	public String request(String serverName) {
		try {
			if (serverName.equals(mirrors[MIRROR_1])) {
				Thread.sleep(1000 * 30);
			}
			else if (serverName.equals(mirrors[MIRROR_2]))
				Thread.sleep(1000 * 3);
			else if (serverName.equals(mirrors[MIRROR_3]))
				Thread.sleep(2);
		} catch (InterruptedException e) {
		}
		
		return serverName;
	}

	@Deprecated
	private synchronized void testAndSet(String valueToSet, Thread threadToFinish1, Thread threadToFinish2) {
		//System.out.println(Thread.currentThread().getName());

		if (response.getResponse() == null) {
			threadToFinish1.interrupt();
			threadToFinish2.interrupt();
			response.setResponse(valueToSet);
		}
	}

	@Override
	@Deprecated
	public String reliableRequest() {

		t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				String rawResponse = request(mirrors[MIRROR_1]);

				testAndSet(rawResponse, t2, t3);
			}
		});
		t1.start();

		t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				String rawResponse = request(mirrors[MIRROR_2]);

				testAndSet(rawResponse, t1, t3);
			}
		});
		t2.start();

		t3 = new Thread(new Runnable() {
			@Override
			public void run() {
				String rawResponse = request(mirrors[MIRROR_3]);
				
				testAndSet(rawResponse, t1, t2);
			}
		});
		t3.start();

		try {
			t1.join();
		} catch (InterruptedException e) {
		}
		
		return response.getResponse();
	}

	@Deprecated
	public String reliableRequestTime() throws Exception {
		
		t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				String rawResponse = request(mirrors[MIRROR_1]);

				testAndSet(rawResponse, t2, t3);
			}
		});
		t1.start();

		t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				String rawResponse = request(mirrors[MIRROR_2]);

				testAndSet(rawResponse, t1, t3);
			}
		});
		t2.start();
		
		t3 = new Thread(new Runnable() {
			@Override
			public void run() {
				String rawResponse = request(mirrors[MIRROR_3]);
				
				testAndSet(rawResponse, t1, t2);
			}
		});
		t3.start();
		
		try {
			t1.join(2000);
		} catch (InterruptedException e) {
		}

		if (response.getResponse() == null) {
			throw new Exception("Requests to mirrors took more than 2 seconds.");
		}
		
		return response.getResponse();
	}

	// taking too long to finish
	@Deprecated
	public void reliableRequestEvent() {
		
		Thread readStandard = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Scanner sc = new Scanner(System.in);

				while (!stop)
					stop = sc.nextLine().equals("S");

			}

		});
		
		Thread t4 = new Thread(new Runnable() {

			@Override
			public void run() {
				
				System.out.println("Press S to stop at any moment.");
				System.out.println("==============================");
				
				
				while (!stop) {
					try {
						System.out.println(reliableRequestTime());
					} catch (Exception e) {
						e.printStackTrace();
						stop = true;
					}
				}
			}
			
		});
		
		t4.start();
		readStandard.start();
	}

	@Deprecated
	public static void main(String[] args) throws InterruptedException {
		
		ReliableRequestImplExecutor req = new ReliableRequestImplExecutor();
		
		try {
			// uncomment the line below to test the reliableRequest with a time threshold of 2 seconds 
			//System.out.println("Mirror used: " + req.reliableRequestTime());
			// uncomment the line below to teste the reliableRequest
			//System.out.println("Mirror used: " + req.reliableRequest());
			
			// runs reliableRequest until a S in written in the standard input
			req.reliableRequestEvent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
