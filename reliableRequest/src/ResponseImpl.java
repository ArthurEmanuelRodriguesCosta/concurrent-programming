package src;

import interfaces.Response;

public class ResponseImpl implements Response {
	
	private String response;
	
	public ResponseImpl() {
		this.response = null;
	}

	@Override
	public synchronized String getResponse() {
		System.out.println("inside get: " + response);
		return response;
	}

	@Override
	public synchronized void setResponse(String newResponse) {
		System.out.println("inside set: " + response);
		this.response = newResponse;
	}

}
