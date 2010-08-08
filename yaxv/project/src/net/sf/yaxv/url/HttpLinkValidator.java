package net.sf.yaxv.url;

import gnu.inet.http.HTTPConnection;
import gnu.inet.http.Response;

import java.io.IOException;
import java.net.URI;

import net.sf.yaxv.Resources;

public class HttpLinkValidator implements LinkValidator {
    public LinkValidationEvent[] validate(URI uri) throws IOException {
        while (true) {
            HTTPConnection connection;
            {
                String host = uri.getHost();
                int port = uri.getPort();
                if (port == -1) {
                    port = 80;
                }
                connection = new HTTPConnection(host, port);
            }
            try {
                Response response = connection.newRequest("HEAD", uri.getPath()).dispatch();

                int code = response.getCode();
                if (code == 200) {
                    return null;
                } else if (code == 404) {
                    return new LinkValidationEvent[] { new LinkValidationEvent(Resources.LINK_HTTP_BROKEN_LINK, new String[] { uri.toString(), String.valueOf(code) } ) };
                } else if (code == 302) {
                    uri = uri.resolve(response.getHeader("Location"));
                } else {
                    return new LinkValidationEvent[] { new LinkValidationEvent(Resources.LINK_HTTP_UNRECOGNIZED_RESPONSE_CODE, new String[] { uri.toString(), String.valueOf(code) } ) };
                }
            }
            finally {
                connection.close();
            }
        }
    }
}
