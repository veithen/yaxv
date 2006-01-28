package net.sf.yaxv.url;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.yaxv.Resources;

public class URLValidationEngine {
	private static class Target {
		private final URL url;
		private final URLValidator validator;
		private final List/*<Link>*/ links = new LinkedList();
		private LinkValidationEvent[] events;
		private boolean processed = false;
		private List/*<LinkValidationEventListener>*/ pendingListeners = new LinkedList();
		
		public Target(URL url, URLValidator validator) {
			this.url = url;
			this.validator = validator;
		}
		
		public void process() {
			LinkValidationEvent[] events;
			try {
				events = validator.validate(url);
			}
			catch (UnknownHostException ex) {
				events = new LinkValidationEvent[] { new LinkValidationEvent(Resources.LINK_UNKNOWN_HOST, new Object[] { url }) };
			}
			catch (IOException ex) {
				events = new LinkValidationEvent[] { new LinkValidationEvent(Resources.LINK_BROKEN_LINK, new Object[] { url, ex.getMessage() }) };
			}
			synchronized (this) {
				this.events = events;
				processed = true;
			}
		}
		
		public void dispatchEventsNowOrLater(LinkValidationEventListener listener) {
			LinkValidationEvent[] events;
			synchronized (this) {
				if (processed) {
					events = this.events;
				} else {
					events = null;
					pendingListeners.add(listener);
				}
			}
			if (events != null) {
				for (int i=0; i<events.length; i++) {
					events[i].dispatch(listener);
				}
			}
		}
		
		public void dispatchPending() {
			if (events != null && events.length != 0) {
				for (Iterator it = pendingListeners.iterator(); it.hasNext(); ) {
					for (int i=0; i<events.length; i++) {
						events[i].dispatch((LinkValidationEventListener)it.next());
					}
				}
			}
			pendingListeners = null;
		}
	}
	
	private class Worker implements Runnable {
		private boolean running = true;
		
		public void run() {
			main: while (true) {
				Target target;
				synchronized (incoming) {
					while (incoming.isEmpty()) {
						if (!state.isStandby()) {
							break main;
						}
						try {
							incoming.wait();
						}
						catch (InterruptedException ex) {}
					}
					target = (Target)incoming.removeFirst();
				}
				target.process();
				synchronized (processed) {
					processed.add(target);
				}
			}
			synchronized (processed) {
				running = false;
				processed.notify();
			}
		}
		
		private boolean isRunning() { return running; }
	}
	
	private static class State {
		private boolean standby;
		
		public synchronized void setStandby(boolean standby) { this.standby = standby; }
		public synchronized boolean isStandby() { return standby; }
	}
	
	private final Map/*<String,URLValidator>*/ validators = new HashMap();
	private final LinkedList/*<Target>*/ incoming = new LinkedList();
	private final LinkedList/*<Target>*/ processed = new LinkedList();
	private final Worker[] workers;
	
	private final Map/*<String,Target>*/ targets = new HashMap();
	
	private final State state = new State();
	
	public URLValidationEngine(int threads) {
		validators.put("http", new HttpURLValidator());
		state.setStandby(true);
		workers = new Worker[threads];
		for (int i=0; i<threads; i++) {
			Worker worker = new Worker();
			workers[i] = worker;
			new Thread(worker).start();
		}
	}
	
	public void validateLink(URL url, LinkValidationEventListener listener) {
		// We use the string representation of the url (and not the url itself) to do the lookup,
		// because hosts comparison requires name resolution and this would make lookups 
		// blocking operations (see documentation of the equals and hashCode method of java.net.URL).
		String key = url.toString();
		Target target = (Target)targets.get(key);
		if (target == null) {
			String protocol = url.getProtocol();
			URLValidator validator = (URLValidator)validators.get(protocol);
			if (validator != null) {
				target = new Target(url, validator);
				targets.put(key, target);
				synchronized (incoming) {
					incoming.add(target);
					incoming.notify();
				}
			} else {
				listener.event(Resources.LINK_UNSUPPORTED_PROTOCOL, new Object[] { protocol });
			}
		}
		if (target != null) {
			target.dispatchEventsNowOrLater(listener);
		}
	}
	
	public void flushProcessed() {
		while (true) {
			Target target;
			synchronized (processed) {
				if (processed.isEmpty()) {
					break;
				} else {
					target = (Target)processed.removeFirst();
				}
			}
			target.dispatchPending();
		}
	}
	
	public void finish() {
		state.setStandby(false);
		// Wake up all workers
		synchronized (incoming) {
			incoming.notifyAll();
		}
		while (true) {
			flushProcessed();
			synchronized (processed) {
				if (processed.isEmpty()) {
					boolean stillWorking = false;
					for (int i=0; i<workers.length; i++) {
						if (workers[i].isRunning()) {
							stillWorking = true;
							break;
						}
					}
					if (stillWorking) {
						try {
							processed.wait();
						}
						catch (InterruptedException ex) {}
					} else {
						break;
					}
				}
			}
		}
	}
}
