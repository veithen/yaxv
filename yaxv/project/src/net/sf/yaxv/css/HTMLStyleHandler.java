package net.sf.yaxv.css;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HTMLStyleHandler extends DefaultHandler {
	private final URL base;
	private final Parser cssParser;
	private final CSSContextTracker tracker;
	private final List stylesheets = new LinkedList();
	
	public HTMLStyleHandler(URL base, Parser cssParser, CSSContextTracker tracker) {
		this.base = base;
		this.cssParser = cssParser;
		this.tracker = tracker;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("link") && "stylesheet".equals(attributes.getValue("rel")) && "text/css".equals(attributes.getValue("type"))) {
			try {
				URL stylesheetUrl = new URL(base, attributes.getValue("href"));
				System.out.println("Opening stylesheet " + stylesheetUrl);
				stylesheets.add(cssParser.parseStylesheet(stylesheetUrl.openStream()));
				// TODO: close stream
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		List rulesets = new LinkedList();
		for (Iterator it = stylesheets.iterator(); it.hasNext(); ) {
			rulesets.addAll(Arrays.asList(((Stylesheet)it.next()).getRulesets(tracker.getContext())));
		}
//		if (!rulesets.isEmpty()) {
//			System.out.println(localName + " -> " + rulesets);
//		}
	}
}
