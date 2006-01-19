package net.sf.yaxv.pcha;

import java.net.MalformedURLException;
import java.net.URL;

public interface URLResolver extends PluggableContentHandler {
	URL resolveUrl(String url) throws MalformedURLException;
}
