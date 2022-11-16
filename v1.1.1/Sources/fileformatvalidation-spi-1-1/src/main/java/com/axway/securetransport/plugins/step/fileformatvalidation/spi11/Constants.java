package com.axway.securetransport.plugins.step.fileformatvalidation.spi11;

public class Constants {
    private Constants() {
    }

    public static final String FILE_STATUS_ACCEPTED = "Accepted";
    public static final String FILE_STATUS_REFUSED = "Refused";

    public static final String STATUS = "status";
    public static final String ERROR_MESSAGE = "errorMessage";

    public static final String XML = "xml";
    public static final String CSV = "csv";
    public static final String DAT = "dat";

    public static final String FILE_TYPE_ATTR = "fileType";
    public static final String QUARANTINE_FOLDER_ATTR = "quarantineFolder";
    public static final String SEPARATOR_ATTR = "separator";
    public static final String ELEMENT_NUMBER_ATTR = "elementNumber";
    public static final String FIXED_LENGTH_ATTR = "fixedLength";
    public static final String RECORD_HEADER_ATTR = "recordHeader";
    public static final String FORBIDDEN_CHARS_ATTR = "forbiddenChars";
//Start of PSO engagement constant changes
    public static final String SKIP_HEADER_ENABLED = "skipHeader";
    public static final String SKIP_FOOTER_ENABLED = "skipFooter";
    public static final String SKIP_HEADER_LINES = "headerCount";
    public static final String SKIP_FOOTER_LINES = "footerCount";
//End of PSO engagement constant changes
}
