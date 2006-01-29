package net.sf.yaxv;

import java.net.URI;
import net.sf.yaxv.css.StylesheetCacheEventListener;

public class AntStylesheetCacheEventListener implements StylesheetCacheEventListener {
	private final TaskEventListener parent;
	
	public AntStylesheetCacheEventListener(TaskEventListener parent) {
		this.parent = parent;
	}

	public void event(URI uri, int line, int column, String key) {
		parent.event(uri, line, column, key, new String[0]);
	}
}
