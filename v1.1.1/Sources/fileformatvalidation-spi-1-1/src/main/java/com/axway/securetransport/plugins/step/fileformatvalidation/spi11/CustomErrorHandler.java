package com.axway.securetransport.plugins.step.fileformatvalidation.spi11;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * For the purposes of removing conventional ErrorHandler console logged error message.
 * The exceptions are not skipped, they are just thrown and handled outside the reader.
 */
public class CustomErrorHandler implements org.xml.sax.ErrorHandler {

    @Override
    public void warning(SAXParseException exception) throws SAXException {
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
    }
}
