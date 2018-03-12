package de.taimos.dvalin.i18n.xml;

import de.taimos.dvalin.i18n.II18nCallback;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author psigloch
 *
 * 			Parser for Resource Files
 */
public class I18nXMLReader extends DefaultHandler {

	private static final String VALUE = "value";

	private static final String LOCALE = "locale";

	private static final String LANGUAGE = "language";

	private static final String ID = "id";

	private static final String LABEL = "label";

	private String id = null;

	private II18nCallback callback;


	/**
	 * @param callback the loader callback
	 */
	I18nXMLReader(II18nCallback callback) {
		this.callback = callback;
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase(I18nXMLReader.LABEL)) {
			this.id = attributes.getValue(I18nXMLReader.ID);
		} else if(qName.equalsIgnoreCase(I18nXMLReader.LANGUAGE)) {
			final String locale = attributes.getValue(I18nXMLReader.LOCALE);
			final String label = attributes.getValue(I18nXMLReader.VALUE);
			if((locale != null) && (label != null)) {
				this.callback.addText(this.id, locale, label);
			}
		}

	}

}