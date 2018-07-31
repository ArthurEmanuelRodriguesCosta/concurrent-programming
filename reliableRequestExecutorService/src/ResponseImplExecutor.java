package src;

import interfaces.Response;

public class ResponseImplExecutor implements Response {
	
	private String response;
	
	public ResponseImplExecutor() {
		this.response = null;
	}

	@Override
	public synchronized String getResponse() {
		//System.out.println(Thread.currentThread().getName() + " " + Thread.currentThread().getState());
		//System.out.println("inside get: " + response);
		return response;
	}

	@Override
	public synchronized void setResponse(String newResponse) {
		//System.out.println("inside set: " + response);
		this.response = newResponse;
	}

}
