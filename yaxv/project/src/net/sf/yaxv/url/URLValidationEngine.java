package net.sf.yaxv.url;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import net.sf.yaxv.ErrorListener;

public class URLValidationEngine {
	private static class Target {
		
	}
	
	private static class Link {
		
	}
	
	private class Worker implements Runnable {
		public void run() {
		}
	}
	
	private final LinkedList/*<Target>*/ queue = new LinkedList();
	private final Worker[] workers;
	
	public URLValidationEngine(int threads) {
		workers = new Worker[threads];
		for (int i=0; i<threads; i++) {
			Worker worker = new Worker();
			workers[i] = worker;
//			new Thread(worker).start();
		}
	}
	
	public void validate(URL url, ErrorListener errorListener) {
		if (url.getProtocol().equals("http")) {
			try {
				new HttpURLValidator().validate(url, errorListener);
			}
			catch (IOException ex) {
				errorListener.log(ErrorListener.ERROR, -1, -1, "Broken link to " + url + ": " + ex.getMessage());
			}
		} else {
			System.out.println("Do not know how to handle protocol " + url.getProtocol());
		}
	}
}
