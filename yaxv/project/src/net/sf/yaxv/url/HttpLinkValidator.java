package net.sf.yaxv.url;

import gnu.inet.http.HTTPConnection;
import gnu.inet.http.Response;
import java.io.IOException;
import java.net.URL;
import net.sf.yaxv.Resources;

public class HttpLinkValidator implements LinkValidator {
	public LinkValidationEvent[] validate(URL url) throws IOException {
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
					return null;
				} else if (code == 404) {
					return new LinkValidationEvent[] { new LinkValidationEvent(Resources.LINK_HTTP_BROKEN_LINK, new Object[] { url, new Integer(code) } ) };
				} else if (code == 302) {
					url = new URL(url, response.getHeader("Location"));
				} else {
					return new LinkValidationEvent[] { new LinkValidationEvent(Resources.LINK_HTTP_UNRECOGNIZED_RESPONSE_CODE, new Object[] { url, new Integer(code) } ) };
				}
			}
			finally {
				connection.close();
			}
		}
	}
}
