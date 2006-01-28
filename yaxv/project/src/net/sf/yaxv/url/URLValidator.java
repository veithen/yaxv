package net.sf.yaxv.url;

import java.io.IOException;
import java.net.URL;

public interface URLValidator {
	LinkValidationEvent[] validate(URL url) throws IOException;
}
