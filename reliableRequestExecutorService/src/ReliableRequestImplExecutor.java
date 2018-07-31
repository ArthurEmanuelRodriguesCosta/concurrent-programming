package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import interfaces.Request;
import interfaces.Response;

public class ReliableRequestImplExecutor implements Request {

	private static Response response = new ResponseImpl();;
	private static final String[] mirrors = {"mirror1.com", "mirror2.br", "mirror3.edu"};
	private ExecutorService executor = Executors.newFixedThreadPool(3);
	
	private Thread t1;
	private Thread t2;
	private Thread t3;
	private volatile boolean stop = false;
	
	private final int MIRROR_1 = 0;
	private final int MIRROR_2 = 1;
	private final int MIRROR_3 = 2;
	
	public ReliableRequestImplExecutor() {
		
	}
	
	@Override
	public String request(String serverName) {
		try {
			if (serverName.equals(mirrors[MIRROR_1])) {
				Thread.sleep(2000);
			}
			else if (serverName.equals(mirrors[MIRROR_2]))
				Thread.sleep(2000);
			else if (serverName.equals(mirrors[MIRROR_3]))
				Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		
		return serverName;
	}
	
	@Override
	public String reliableRequest() {

		Callable<String> request1 = () -> {
			return request(mirrors[MIRROR_1]);
		};

		Callable<String> request2 = () -> {
			return request(mirrors[MIRROR_2]);
		};

		Callable<String> request3 = () -> {
			return request(mirrors[MIRROR_3]);
		};
		
		List<Callable<String>> tasks = new ArrayList<>();
		tasks.add(request1);
		tasks.add(request2);
		tasks.add(request3);

		try {
			
			String result = executor.invokeAny(tasks);
			executor.shutdown();
			return result;
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public String reliableRequestTime() throws Exception {

		Callable<String> request1 = () -> {
			return request(mirrors[MIRROR_1]);
		};

		Callable<String> request2 = () -> {
			return request(mirrors[MIRROR_2]);
		};

		Callable<String> request3 = () -> {
			return request(mirrors[MIRROR_3]);
		};
		
		List<Callable<String>> tasks = new ArrayList<>();
		tasks.add(request1);
		tasks.add(request2);
		tasks.add(request3);

		try {
			String result = executor.invokeAny(tasks, 2000, TimeUnit.MILLISECONDS);
			executor.shutdown();
			return result;
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			executor.shutdown();
			e.printStackTrace();
		}
		return null;
	}

	// taking too long to finish
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
						executor = Executors.newFixedThreadPool(3);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
		t4.start();
		readStandard.start();
	}

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
