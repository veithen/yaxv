package net.sf.yaxv.url;

public class LinkValidationEvent {
	private final String key;
	private final String[] args;
	
	public LinkValidationEvent(String key, String[] args) {
		this.key = key;
		this.args = args;
	}
	
	public String getKey() { return key; }
	public String[] getArgs() { return args; }
	
	public void dispatch(LinkValidationEventListener listener) {
		listener.event(key, args);
	}
}
