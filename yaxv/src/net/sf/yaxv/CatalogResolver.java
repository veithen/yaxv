package net.sf.yaxv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CatalogResolver implements EntityResolver {
	private static class Entry {
		private final String publicId;
		private final String systemId;
		private final String resource;
		
		public Entry(String publicId, String systemId, String resource) {
			this.publicId = publicId;
			this.systemId = systemId;
			this.resource = resource;
		}
		
		public String getPublicId() { return publicId; }
		public String getSystemId() { return systemId; }
		public String getResource() { return resource; }
	}
	
	private final static Map instances = new HashMap();
	
	private final Map entriesByPublicId = new HashMap();
	private final Map entriesBySystemId = new HashMap();
	
	private CatalogResolver(String location) throws CatalogResolverException {
		try {
			InputStream catalog = CatalogResolver.class.getResourceAsStream(location + "/catalog");
			if (catalog == null) {
				throw new CatalogResolverException("Catalog resource not found");
			}
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(catalog, "UTF-8"));
				String line;
				int lineNr = 0;
				while ((line = in.readLine()) != null) {
					lineNr++;
					if (line.length() > 0 && line.charAt(0) != '#') {
						String[] parts = line.split("|");
						if (parts.length != 3) {
							throw new CatalogResolverException("Invalid catalog resource format (line " + lineNr + ")");
						}
						Entry entry = new Entry(parts[0], parts[1], location + "/" + parts[2]);
						entriesByPublicId.put(parts[0], entry);
						entriesBySystemId.put(parts[1], entry);
					}
				}
			}
			finally {
				try {
					catalog.close();
				}
				catch (IOException ex) {}
			}
		}
		catch (IOException ex) {
			throw new CatalogResolverException("Unable to read catalog", ex);
		}
	}
	
	public static CatalogResolver getInstance(String location) throws CatalogResolverException {
		if (location.charAt(0) != '/') {
			location = "/" + location;
		}
		synchronized (instances) {
			CatalogResolver instance = (CatalogResolver)instances.get(location);
			if (instance == null) {
				instance = new CatalogResolver(location);
				instances.put(location, instance);
			}
			return instance;
		}
	}

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		Entry entry = null;
		if (publicId != null) {
			entry = (Entry)entriesByPublicId.get(publicId);
			if (entry == null) {
				return null;
			}
		}
		if (systemId != null) {
			if (entry == null) {
				entry = (Entry)entriesBySystemId.get(systemId);
				if (entry == null) {
					return null;
				}
			} else {
				if (!entry.getSystemId().equals(systemId)) {
					throw new SAXException("System ID of external entity does not match its public ID");
				}
			}
		}
		return new InputSource(CatalogResolver.class.getResourceAsStream(entry.getResource()));
	}
}
