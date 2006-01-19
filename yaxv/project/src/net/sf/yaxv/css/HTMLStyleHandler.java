package net.sf.yaxv.css;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.yaxv.pcha.DefaultPluggableContentHandler;
import net.sf.yaxv.pcha.PCHAContext;
import net.sf.yaxv.pcha.URLResolver;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HTMLStyleHandler extends DefaultPluggableContentHandler {
	private final Parser cssParser;
	private final List stylesheets = new LinkedList();
	
	public HTMLStyleHandler(Parser cssParser) {
		this.cssParser = cssParser;
	}
	
	public void startElement(PCHAContext context, String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("link") && "stylesheet".equals(attributes.getValue("rel")) && "text/css".equals(attributes.getValue("type"))) {
			try {
				URL stylesheetUrl = ((URLResolver)context.getContentHandler(URLResolver.class)).resolveUrl(attributes.getValue("href"));
				System.out.println("Opening stylesheet " + stylesheetUrl);
				stylesheets.add(cssParser.parseStylesheet(stylesheetUrl.openStream()));
				// TODO: close stream
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
