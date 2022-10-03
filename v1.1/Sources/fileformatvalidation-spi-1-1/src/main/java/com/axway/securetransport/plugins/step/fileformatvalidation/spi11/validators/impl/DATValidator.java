package com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.impl;

import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.FileValidator;
import com.axway.st.plugins.improvedrouting.services.LoggingService;
import com.axway.st.plugins.services.logging.LogLevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Path;

public class DATValidator implements FileValidator {
    /**
     * Standard server log.
     */
    private final LoggingService logger;
    /**
     * Holds whether the debug messages are enabled.
     */
    private final boolean isDebugEnabled;
    /**
     * Error message in case of an invalid file.
     */
    private String errorMessage;
    /**
     * Fixed length of the file record.
     */
    private final String fixedLength;
    /**
     * Record header for the file.
     */
    private final String recordHeader;
    /**
     * Forbidden characters in the file.
     */
    private String forbiddenChars;

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public DATValidator(LoggingService logger, String fixedLength, String recordHeader, String forbiddenChars) {
        this.logger = logger;
        this.isDebugEnabled = logger.isEnabledFor(LogLevel.DEBUG);
        this.fixedLength = fixedLength;
        this.recordHeader = recordHeader;
        this.forbiddenChars = forbiddenChars;
        this.errorMessage = "";
    }

    @Override
    public boolean isFileValid(Path filePath) {
        String targetFileName = String.valueOf(filePath.getFileName());

        FileValidator xmlValidator = new XMLValidator(logger);
        boolean isXmlValidFile = xmlValidator.isFileValid(filePath);

        if (isXmlValidFile) {
            logger.error("File: " + targetFileName + " it is not a .dat file, it is a well formed .xml file");
            errorMessage = "The file is a well formed xml file; ";

            return false;
        }

        boolean notExistFChar = true;
        boolean isSizeValid = true;
        boolean isRecordHeadValid = true;

        StringBuilder errorMsgBuilder = new StringBuilder();
        File uploadedFile = filePath.toFile();
        int recordSize = getFixedLength(fixedLength);

        boolean shouldCheckRecordHeader = recordHeader != null && !recordHeader.isEmpty();
        if (!shouldCheckRecordHeader) {
            logger.debug("Record header is null or empty. Record header check will be skipped.");
        }

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(uploadedFile));
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            String line;
            int nLine = 0;

            while ((line = bufferedReader.readLine()) != null) {

                if (recordSize != 0 && !checkRecordSize(recordSize, line)) {
                    errorMsgBuilder.append("Some record in the file does not have the right size; ");
                    logger.error("The size of the record n. " + nLine + " does not match with the assigned fixed length: " + recordSize);
                    isSizeValid = false;
                }

                if (!checkCustomForbiddenChars(line, nLine)) {
                    errorMsgBuilder.append("The file contains an invalid characters; ");
                    notExistFChar = false;
                }

                if (shouldCheckRecordHeader && !isRecordHeaderCheckValid(recordHeader, line)) {
                    logger.error("Record n. " + nLine + " has a header that does not match with the assigned record header: " + recordHeader);

                    errorMsgBuilder.append("The file contains a record with invalid head; ");
                    isRecordHeadValid = false;
                }
                nLine++;
            }
            errorMessage = errorMsgBuilder.toString();
        } catch (IOException e) {
            logger.error(" getInputStream() FAILED ! error= " + e);
            return false;
        }

        return notExistFChar && isSizeValid && isRecordHeadValid;
    }

    /**
     * Checks if the line contains a forbidden character.
     *
     * @param line  Current line of dat file.
     * @param nLine numeric representation of the nth line of the file.
     * @return true if the does not contain a forbidden character, false - otherwise.
     */
    private boolean checkCustomForbiddenChars(String line, int nLine) {
        if (!forbiddenChars.contains(",")) {
            forbiddenChars = forbiddenChars + ",";
        }

        char[] forbiddenCharArray = forbiddenChars.toCharArray();
        for (char forbiddenChar : forbiddenCharArray) {
            if (isDebugEnabled) {
                logger.debug("Executing check on the following forbidden character: " + forbiddenChar);
            }

            if (line.contains(String.valueOf(forbiddenChar))) {
                logger.error("Record n." + nLine + " contains the following forbidden char: " + forbiddenChar);
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the integer representation of fixedLength input.
     * In case of invalid input, the fixed length will be set to 0.
     *
     * @param fixedLength String representation of fixedLength input.
     * @return integer representation of fixedLength input
     */
    public int getFixedLength(String fixedLength) {
        if (fixedLength == null || fixedLength.isEmpty()) {
            return 0;
        }

        int length = 0;
        try {
            length = Integer.parseInt(fixedLength);
            if (isDebugEnabled) {
                logger.debug("fixedLength is set to " + length + ". Length control will be performed on each record.");
            }
        } catch (NumberFormatException e) {
            logger.warn("It is impossible to convert fixedLength to an int number. " +
                    "The value is set to 0. Validation against this property is skipped", e);
        }
        return length;
    }

    /**
     * Checks if the record size matches the current line length.
     *
     * @param recordSize Fixed length of the file record.
     * @param line       Current line of dat file.
     * @return true if the lengths match and false otherwise.
     */
    boolean checkRecordSize(int recordSize, String line) {
        return line.length() == recordSize;
    }

    /**
     * Checks if the current line starts with the record header.
     *
     * @param recordHeader Record header for the file.
     * @param line         Current line of dat file.
     * @return true if the line starts with the record header and false otherwise.
     */
    public boolean isRecordHeaderCheckValid(String recordHeader, String line) {
        return line.startsWith(recordHeader);
    }
}
