package net.sf.yaxv.url;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.yaxv.Messages;

public class LinkValidationEngine {
    private static class Target {
        private final URI uri;
        private final LinkValidator validator;
        private LinkValidationEvent[] events;
        private boolean processed = false;
        private List<LinkValidationEventListener> pendingListeners = new LinkedList<LinkValidationEventListener>();
        private long processedTime;
        
        public Target(URI uri, LinkValidator validator) {
            this.uri = uri;
            this.validator = validator;
        }
        
        public Target(URI uri, long processedTime, LinkValidationEvent[] events) {
            this.uri = uri;
            validator = null;
            processed = true;
            this.processedTime = processedTime;
            this.events = events;
        }
        
        public URI getURI() { return uri; }
        public LinkValidationEvent[] getEvents() { return events; }
        public long getProcessedTime() { return processedTime; }
        
        public void process() {
            LinkValidationEvent[] events;
            try {
                events = validator.validate(uri);
            }
            catch (UnknownHostException ex) {
                events = new LinkValidationEvent[] { new LinkValidationEvent(Messages.LINK_UNKNOWN_HOST, new String[] { uri.toString() }) };
            }
            catch (IOException ex) {
                events = new LinkValidationEvent[] { new LinkValidationEvent(Messages.LINK_BROKEN_LINK, new String[] { uri.toString(), ex.getMessage() }) };
            }
            synchronized (this) {
                this.events = events;
                processed = true;
                processedTime = System.currentTimeMillis();
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
    
    private static class Worker implements Runnable {
        private final LinkedList<Target> incoming;
        private final LinkedList<Target> processed;
        private final State state;
        
        private boolean running = true;
        
        public Worker(LinkedList<Target> incoming, LinkedList<Target> processed, State state) {
            this.incoming = incoming;
            this.processed = processed;
            this.state = state;
        }
        
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
                    target = incoming.removeFirst();
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
        
        public boolean isRunning() { return running; }
    }
    
    private static class State {
        private boolean standby;
        
        public State() {}
        
        public synchronized void setStandby(boolean standby) { this.standby = standby; }
        public synchronized boolean isStandby() { return standby; }
    }
    
    private final Map<String,LinkValidator> validators = new HashMap<String,LinkValidator>();
    private final LinkedList<Target> incoming = new LinkedList<Target>();
    private final LinkedList<Target> processed = new LinkedList<Target>();
    private final Worker[] workers;
    
    private final Map<URI,Target> targets = new HashMap<URI,Target>();
    
    private final State state = new State();
    
    public LinkValidationEngine(int threads) {
        validators.put("http", new HttpLinkValidator());
        validators.put("file", new FileLinkValidator());
        state.setStandby(true);
        workers = new Worker[threads];
        for (int i=0; i<threads; i++) {
            Worker worker = new Worker(incoming, processed, state);
            workers[i] = worker;
            new Thread(worker).start();
        }
    }
    
    public void writeCacheFile(File file) throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        for (Iterator it = targets.values().iterator(); it.hasNext(); ) {
            Target target = (Target)it.next();
            out.writeLong(target.getProcessedTime());
            out.writeUTF(target.getURI().toString());
            LinkValidationEvent[] events = target.getEvents();
            if (events == null) {
                out.writeInt(0);
            } else {
                out.writeInt(events.length);
                for (int i=0; i<events.length; i++) {
                    LinkValidationEvent event = events[i];
                    out.writeUTF(event.getKey());
                    String[] args = event.getArgs();
                    out.writeInt(args.length);
                    for (int j=0; j<args.length; j++) {
                        out.writeUTF(args[j]);
                    }
                }
            }
        }
        out.close();
    }
    
    public void loadCacheFile(File file) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        while (in.available() > 0) {
            long processedTime = in.readLong();
            String uriString = in.readUTF();
            LinkValidationEvent[] events;
            int eventCount = in.readInt();
            if (eventCount == 0) {
                events = null;
            } else {
                events = new LinkValidationEvent[eventCount];
                for (int i=0; i<eventCount; i++) {
                    String key = in.readUTF();
                    int argCount = in.readInt();
                    String[] args = new String[argCount];
                    for (int j=0; j<argCount; j++) {
                        args[j] = in.readUTF();
                    }
                    events[i] = new LinkValidationEvent(key, args);
                }
            }
            if (System.currentTimeMillis() - processedTime < 24L*3600000L) {
                try {
                    URI uri = new URI(uriString);
                    targets.put(uri, new Target(uri, processedTime, events));
                }
                catch (URISyntaxException ex) {}
            }
        }
        in.close();
    }
    
    public void validateLink(URI uri, LinkValidationEventListener listener) {
        Target target = targets.get(uri);
        if (target == null) {
            String scheme = uri.getScheme();
            LinkValidator validator = validators.get(scheme);
            if (validator != null) {
                target = new Target(uri, validator);
                targets.put(uri, target);
                synchronized (incoming) {
                    incoming.add(target);
                    incoming.notify();
                }
            } else {
                listener.event(Messages.LINK_UNSUPPORTED_SCHEME, new Object[] { scheme });
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
                    target = processed.removeFirst();
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
        // Wait until the processed queue has become empty and all workers have stopped
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
