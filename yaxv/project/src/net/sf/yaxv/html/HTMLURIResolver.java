package net.sf.yaxv.html;

import java.net.URI;
import java.net.URISyntaxException;

import net.sf.yaxv.pcha.DefaultPluggableContentHandler;
import net.sf.yaxv.pcha.PCHAContext;
import net.sf.yaxv.pcha.URIResolver;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HTMLURIResolver extends DefaultPluggableContentHandler implements URIResolver {
	private final URI defaultBase;
	private URI base;
	
	public HTMLURIResolver(URI defaultBase) {
		this.defaultBase = defaultBase;
	}
	
	public void startDocument(PCHAContext context) throws SAXException {
		base = defaultBase;
	}
	
	public void startElement(PCHAContext context, String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("base".equals(localName)) {
			try {
				base = new URI(atts.getValue("href"));
			}
			catch (URISyntaxException ex) {
				throw new SAXException(ex);
			}
		}
	}
	
	public URI resolveURI(String uri) throws URISyntaxException {
		return base.resolve(uri);
	}
}
