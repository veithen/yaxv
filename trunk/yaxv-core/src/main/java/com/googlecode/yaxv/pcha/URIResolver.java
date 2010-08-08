package com.googlecode.yaxv.pcha;

import java.net.URI;
import java.net.URISyntaxException;

public interface URIResolver extends PluggableContentHandler {
    URI resolveURI(String uri) throws URISyntaxException;
}
