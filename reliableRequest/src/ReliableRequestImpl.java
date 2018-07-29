package src;

import interfaces.Request;
import interfaces.Response;

public class ReliableRequestImpl implements Request{

	private static Response response = new ResponseImpl();;
	
	private static final String[] mirrors = {"mirror1.com", "mirror2.br", "mirror3.edu"};
	
	public static void main(String[] args) throws InterruptedException {
		
		ReliableRequestImpl req = new ReliableRequestImpl();
		
		for (int i = 0; i < mirrors.length; i++) {
			final int param = i;
			Thread t = new Thread(new Runnable() {
				int mirrorPos = param;
				@Override
				public void run() {
					System.out.println("mirror: " + mirrors[mirrorPos] + " response :" + response.getResponse());
					String rawResponse = req.request(mirrors[mirrorPos]);
					if (response.getResponse() == null) {
						response.setResponse(rawResponse);
					}
				}
			});
			
			t.start();
			
		}
		
		//System.out.println(response.getResponse());
	}
	
//	public Runnable doRequest = new Runnable() {
//
//		@Override
//		public void run() {
//			String rawResponse = request("mirror3.edu");
//		}
//		
//	};

	@Override
	public String request(String serverName) {
		try {
			if (serverName.equals(mirrors[0]))
				Thread.sleep(1000 * 1);
			else if (serverName.equals(mirrors[1]))
				Thread.sleep(1000 * 2);
			else if (serverName.equals(mirrors[2]))
				Thread.sleep(1000 * 4);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return serverName;
	}

	@Override
	public String reliableRequest() {
		return null;
	}
}
