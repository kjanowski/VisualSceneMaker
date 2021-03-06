package de.dfki.vsm.util.xml;

import org.w3c.dom.Element;

/**
 * @author Gregor Mehlmann
 */
public interface XMLParseable {

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public void parseXML(final Element element) throws XMLParseError;
}
