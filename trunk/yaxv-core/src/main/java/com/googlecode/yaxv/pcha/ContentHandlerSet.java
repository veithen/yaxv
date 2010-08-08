package com.googlecode.yaxv.pcha;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ContentHandlerSet implements ContentHandler {
    private static class HandlerAndState {
        private final static int UNDISPATCHED = 0;
        private final static int DISPATCHING = 1;
        private final static int DISPATCHED = 2;

        private PluggableContentHandler pch;
        private int state = UNDISPATCHED;
        
        public HandlerAndState(PluggableContentHandler pch) {
            this.pch = pch;
        }
        
        public PluggableContentHandler getContentHandler() { return pch; }
        public int getState() { return state; }
        public void setState(int state) { this.state = state; }
        
        public void dispatch(Event event, PCHAContext context) throws SAXException {
            switch (state) {
                case UNDISPATCHED:
                    state = DISPATCHING;
                    event.dispatch(pch, context);
                    state = DISPATCHED;
                    // No break here!
                case DISPATCHED:
                    return;
                case DISPATCHING:
                    throw new Error(); // TODO
            }
        }
    }
    
    private class PCHAContextImpl implements PCHAContext {
        private final Event event;
        private final HandlerAndState[] hs;
        
        public PCHAContextImpl(Event event, HandlerAndState[] hs) {
            this.event = event;
            this.hs = hs;
        }

        public PluggableContentHandler getContentHandler(Class contentHandlerClass) throws SAXException {
            for (int i=0; i<hs.length; i++) {
                PluggableContentHandler pch = hs[i].getContentHandler();
                if (contentHandlerClass.isInstance(pch)) {
                    hs[i].dispatch(event, this);
                    return pch;
                }
            }
            throw new Error(); // TODO
        }
        
        public void dispatchAll() throws SAXException {
            for (int i=0; i<hs.length; i++) {
                hs[i].dispatch(event, this);
            }
        }

        public Locator getLocator() { return locator; }
    }
    
    private static abstract class Event {
        public abstract void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException;
    }
    
    private final List handlers = new LinkedList();
    
    private Locator locator;
    
    public void addContentHandler(PluggableContentHandler handler) {
        handlers.add(handler);
    }
    
    private void dispatch(Event event) throws SAXException {
        HandlerAndState[] hs = new HandlerAndState[handlers.size()];
        {
            int i = 0;
            for (Iterator it = handlers.iterator(); it.hasNext(); ) {
                hs[i++] = new HandlerAndState((PluggableContentHandler)it.next());
            }
        }
        new PCHAContextImpl(event, hs).dispatchAll();
    }

    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    public void startDocument() throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.startDocument(context);
            }
        });
    }

    public void endDocument() throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.endDocument(context);
            }
        });
    }

    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.startPrefixMapping(context, prefix, uri);
            }
        });
    }

    public void endPrefixMapping(final String prefix) throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.endPrefixMapping(context, prefix);
            }
        });
    }

    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.startElement(context, uri, localName, qName, atts);
            }
        });
    }

    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.endElement(context, uri, localName, qName);
            }
        });
    }

    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.characters(context, ch, start, length);
            }
        });
    }

    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.ignorableWhitespace(context, ch, start, length);
            }
        });
    }

    public void processingInstruction(final String target, final String data) throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.processingInstruction(context, target, data);
            }
        });
    }

    public void skippedEntity(final String name) throws SAXException {
        dispatch(new Event() {
            public void dispatch(PluggableContentHandler pch, PCHAContext context) throws SAXException {
                pch.skippedEntity(context, name);
            }
        });
    }
}
