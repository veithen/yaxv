package net.sf.yaxv.url;

import java.io.IOException;
import java.net.URL;
import net.sf.yaxv.ErrorListener;

public interface URLValidator {
	void validate(URL url, ErrorListener errorListener) throws IOException;
}
