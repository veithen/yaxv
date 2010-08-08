package net.sf.yaxv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Set of (element name, attribute name) elements. This class is used to specify sets of
 * attributes that require specific processing, for example attributes that contain URLs
 * that must be validated.
 * 
 * @author veithen
 */
public class AttributeSet {
    private static class Entry {
        private final String element;
        private final String attribute;
        
        public Entry(String element, String attribute) {
            this.element = element;
            this.attribute = attribute;
        }
        
        public int hashCode() {
            return 31 * element.hashCode() + attribute.hashCode();
        }
        
        public boolean equals(Object _obj) {
            if (_obj instanceof Entry) {
                Entry obj = (Entry)_obj;
                return element.equals(obj.element) && attribute.equals(obj.attribute);
            } else {
                return false;
            }
        }
    }
    
    private final Set set = new HashSet();
    
    public AttributeSet(URL url) throws IOException, AttributeSetException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        String line;
        int lineNr = 0;
        while ((line = in.readLine()) != null) {
            lineNr++;
            if (line.length() > 0 && line.charAt(0) != '#') {
                String[] parts = line.split(":");
                if (parts.length != 2) {
                    throw new AttributeSetException("Invalid attribute set format (line " + lineNr + ")");
                }
                set.add(new Entry(parts[0], parts[1]));
            }
        }
    }
    
    public boolean contains(String element, String attribute) {
        return set.contains(new Entry(element, attribute));
    }
}
