package de.taimos.dvalin.interconnect.model.metamodel.xmladapter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.taimos.dvalin.interconnect.model.metamodel.FilterableType;

public class FilterableTypeAdapter extends XmlAdapter<String, FilterableType> {
    @Override
    public FilterableType unmarshal(String v) throws Exception {
        try {
            return FilterableType.valueOf(v);
        } catch(Exception e) {
            throw new JAXBException(e);
        }
    }
    
    @Override
    public String marshal(FilterableType v) throws Exception {
        return v.name();
    }
}
