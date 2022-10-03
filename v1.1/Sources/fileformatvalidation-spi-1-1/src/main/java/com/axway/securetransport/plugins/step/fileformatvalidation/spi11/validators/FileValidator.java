package com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators;

import java.nio.file.Path;

public interface FileValidator {
    /**
     * Checks if the current file is valid.
     *
     * @param filePath The path to the xml file to validate.
     * @return true if the file is valid, false - if it is not
     */
    boolean isFileValid(Path filePath);

    /**
     * Returns the error message in case of an error or an invalid file.
     *
     * @return error message
     */
    default String getErrorMessage(){
        return "";
    }
}
