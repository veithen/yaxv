package com.googlecode.yaxv.css;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.googlecode.yaxv.pcha.DefaultPluggableContentHandler;
import com.googlecode.yaxv.pcha.PCHAContext;
import com.googlecode.yaxv.pcha.URIResolver;

public class HTMLStyleHandler extends DefaultPluggableContentHandler {
    private final StylesheetCache cache;
    private final List<Stylesheet> stylesheets = new LinkedList<Stylesheet>();
    
    public HTMLStyleHandler(StylesheetCache cache) {
        this.cache = cache;
    }
    
    public void startElement(PCHAContext context, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("link") && "stylesheet".equals(attributes.getValue("rel")) && "text/css".equals(attributes.getValue("type"))) {
            try {
                URI stylesheetURI = context.getContentHandler(URIResolver.class).resolveURI(attributes.getValue("href"));
                stylesheets.add(cache.loadStylesheet(stylesheetURI));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        CSSContext cssContext = context.getContentHandler(CSSContextTracker.class).getContext();
        List<Ruleset> rulesets = new LinkedList<Ruleset>();
        for (Stylesheet stylesheet : stylesheets) {
            rulesets.addAll(Arrays.asList(stylesheet.getRulesets(cssContext)));
        }
//        if (!rulesets.isEmpty()) {
//            System.out.println(localName + " -> " + rulesets);
//        }
    }
}
