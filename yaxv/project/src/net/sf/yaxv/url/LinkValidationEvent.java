package net.sf.yaxv.url;

public class LinkValidationEvent {
	private final String key;
	private final Object[] args;
	
	public LinkValidationEvent(String key, Object[] args) {
		this.key = key;
		this.args = args;
	}
	
	public void dispatch(LinkValidationEventListener listener) {
		listener.event(key, args);
	}
}
