package com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.impl;

import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.CustomErrorHandler;
import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.FileValidator;
import com.axway.st.plugins.improvedrouting.services.LoggingService;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class XMLValidator implements FileValidator {
    /**
     * Standard server log.
     */
    private final LoggingService logger;

    /**
     * Error message in case of an invalid file.
     */
    private String errorMessage;

    public XMLValidator(LoggingService logger) {
        this.logger = logger;
        this.errorMessage = "";
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isFileValid (Path filePath) {
        File uploadedFile = filePath.toFile();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try (InputStream inputStreamToParse = new FileInputStream(uploadedFile)) {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            reader.setErrorHandler(new CustomErrorHandler());
            reader.parse(new InputSource(inputStreamToParse));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            logger.debug("The file is not a valid XML file, because of : " + e);

            errorMessage = e.toString();
            return false;
        }

        return true;
    }
}