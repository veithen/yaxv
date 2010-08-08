package com.googlecode.yaxv.url;

import java.io.IOException;
import java.net.URI;

public interface LinkValidator {
    LinkValidationEvent[] validate(URI uri) throws IOException;
}
