package net.sf.yaxv.url;

import gnu.inet.http.HTTPConnection;
import gnu.inet.http.Response;
import java.io.IOException;
import java.net.URL;
import net.sf.yaxv.ErrorListener;

public class HttpURLValidator {
	
	/** Creates a new instance of HttpURLValidator */
	public HttpURLValidator() {
	}
	
	public void validate(URL url, ErrorListener errorListener) throws IOException {
		while (true) {
			HTTPConnection connection;
			{
				String host = url.getHost();
				int port = url.getPort();
				if (port == -1) {
					port = 80;
				}
				connection = new HTTPConnection(host, port);
			}
			try {
				Response response = connection.newRequest("HEAD", url.getPath()).dispatch();

				int code = response.getCode();
				if (code == 200) {
					return;
				} else if (code == 404) {
					errorListener.log(ErrorListener.ERROR, -1, -1, "Broken link to " + url + ": " + code);
					return;
				} else if (code == 302) {
					url = new URL(url, response.getHeader("Location"));
				} else {
					errorListener.log(ErrorListener.ERROR, -1, -1, "Unrecognized response code for " + url + ": " + code);
					return;
				}
			}
			finally {
				connection.close();
			}
		}
	}
}
