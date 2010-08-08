package com.googlecode.yaxv.url;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.googlecode.yaxv.Messages;

public class FileLinkValidator implements LinkValidator {
    public LinkValidationEvent[] validate(URI uri) throws IOException {
        File file = new File(uri);
        if (file.exists()) {
            return null;
        } else {
            return new LinkValidationEvent[] { new LinkValidationEvent(Messages.LINK_FILE_BROKEN_LINK, new String[] { file.toString() } ) };
        }
    }
}
