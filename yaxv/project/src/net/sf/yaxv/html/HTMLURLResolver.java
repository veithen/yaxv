package net.sf.yaxv.html;

import java.net.MalformedURLException;
import java.net.URL;
import net.sf.yaxv.pcha.DefaultPluggableContentHandler;
import net.sf.yaxv.pcha.PCHAContext;
import net.sf.yaxv.pcha.URLResolver;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HTMLURLResolver extends DefaultPluggableContentHandler implements URLResolver {
	private final URL defaultBase;
	private URL base;
	
	public HTMLURLResolver(URL defaultBase) {
		this.defaultBase = defaultBase;
	}
	
	public void startDocument(PCHAContext context) throws SAXException {
		base = defaultBase;
	}
	
	public void startElement(PCHAContext context, String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("base".equals(localName)) {
			try {
				base = new URL(atts.getValue("href"));
			}
			catch (MalformedURLException ex) {
				throw new SAXException(ex);
			}
		}
	}
	
	public URL resolveUrl(String url) throws MalformedURLException {
		return new URL(base, url);
	}
}
