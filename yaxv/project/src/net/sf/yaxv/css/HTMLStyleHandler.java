package net.sf.yaxv.css;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.yaxv.pcha.DefaultPluggableContentHandler;
import net.sf.yaxv.pcha.PCHAContext;
import net.sf.yaxv.pcha.URIResolver;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HTMLStyleHandler extends DefaultPluggableContentHandler {
	private final StylesheetCache cache;
	private final List stylesheets = new LinkedList();
	
	public HTMLStyleHandler(StylesheetCache cache) {
		this.cache = cache;
	}
	
	public void startElement(PCHAContext context, String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("link") && "stylesheet".equals(attributes.getValue("rel")) && "text/css".equals(attributes.getValue("type"))) {
			try {
				URI stylesheetURI = ((URIResolver)context.getContentHandler(URIResolver.class)).resolveURI(attributes.getValue("href"));
				stylesheets.add(cache.loadStylesheet(stylesheetURI));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		CSSContext cssContext = ((CSSContextTracker)context.getContentHandler(CSSContextTracker.class)).getContext();
		List rulesets = new LinkedList();
		for (Iterator it = stylesheets.iterator(); it.hasNext(); ) {
			rulesets.addAll(Arrays.asList(((Stylesheet)it.next()).getRulesets(cssContext)));
		}
//		if (!rulesets.isEmpty()) {
//			System.out.println(localName + " -> " + rulesets);
//		}
	}
}
