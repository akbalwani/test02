package com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.impl;

import com.axway.securetransport.plugins.step.fileformatvalidation.spi11.validators.FileValidator;
import com.axway.st.plugins.improvedrouting.services.LoggingService;
import com.axway.st.plugins.services.logging.LogLevel;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class CSVValidator implements FileValidator {
    /**
     * Constant for error message.
     */
    private static final String CSV_READER_FAILED_MESSAGE = " Read Input Stream from uploaded file: %s failed for csvreader check, error= ";

    /**
     * Standard server log.
     */
    private final LoggingService logger;

    /**
     * Error message in case of an invalid file.
     */
    private String errorMessage;

    /**
     * Separator for .csv file type.
     */
    private final String separator;

    
    /**
     * Number of expected elements in the .csv file.
     */
    
    private final String elementNumber;
    
    /**
     * Number of Header lines to be skipped.
     */
        
    // Start of PSO engagement variable changes
    
    private String headerCount;

    /**
     * Enables Skip header option.
     */
    

	private boolean skipHeader;

    /**
     * Enables Skip footer option.
     */
    
	private boolean skipFooter;

    /**
     * Number of Footer lines to be skipped.
     */
    
	private String footerCount;
	//End of PSO engagement variable changes
	
    public CSVValidator(LoggingService logger, String separator, String elementNumber,boolean skipHeader, String headerCount,boolean skipFooter,String footerCount) {
        this.logger = logger;
        this.separator = separator;
        this.elementNumber = elementNumber;
        this.errorMessage = "";
        this.headerCount= headerCount;
        this.skipHeader= skipHeader;
        this.skipFooter= skipFooter;
        this.footerCount= footerCount;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isFileValid(Path filePath) {
        String targetFileName = String.valueOf(filePath.getFileName());
        
        FileValidator xmlValidator = new XMLValidator(logger);
        boolean isXmlValidFile = xmlValidator.isFileValid(filePath);

        if (isXmlValidFile) {
            logger.error("File: " + targetFileName + " is not a .csv file, it is a well formed .xml file");
            errorMessage = "The file is a well formed xml file; ";
            return false;
        }

        CSVReader csvReader = null;
        try (FileReader fileReader = new FileReader(filePath.toFile())) {
            final char cSeparator = separator.charAt(0);

            final CSVParser csvParser = new CSVParserBuilder()
                    .withSeparator(cSeparator)
                    .withIgnoreQuotations(false)
                    .build();
//Commented for PSO engagement to add options to skip header and footer
            
//            csvReader = new CSVReaderBuilder(fileReader)
//                    .withSkipLines(1)
//                    .withCSVParser(csvParser)
//                    .build();

//Start of PSO engagement changes            
            
if(skipHeader){
	logger.info("Skip header option is enabled");
            if (headerCount == null || headerCount.isEmpty()) {  
            	  errorMessage = "Header count is null or empty, No lines will be skipped";
                  logger.warn(errorMessage);
                  csvReader = new CSVReaderBuilder(fileReader)
                          .withCSVParser(csvParser)
                          .build();

            }else {
            csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(Integer.parseInt(headerCount))
                    .withCSVParser(csvParser)
                    .build();
}
            }else {
            	logger.info("Skip header option is disabled.");
                csvReader = new CSVReaderBuilder(fileReader)

                        .withCSVParser(csvParser)
                        .build();

            }            

			List<String[]> parsedFileLines = csvReader.readAll();

			if(skipFooter) {
				logger.info("Skip footer option is enabled");
				logger.info("Number of footer lines to be skipped is configured as "+footerCount);
				if(parsedFileLines.size()>0) {
				int length=parsedFileLines.size();
				for (int i = Integer.parseInt(footerCount); i >= 1; --i) {
					length=length-1;
			    	parsedFileLines.remove(length);
				}
				}else {
					  errorMessage = "Current document is null or empty.";
		                logger.error(errorMessage);
				}
				
			}else {
				logger.info("Skip footer option is disabled");
			}
//			if (logger.isEnabledFor(LogLevel.DEBUG)) {
//				parsedFileLines.forEach(x -> logger.info(Arrays.toString(x)) );
//			}
// End of PSO engagement changes			
            if (parsedFileLines == null || parsedFileLines.isEmpty()) {
                errorMessage = "Current document is null or empty.";
                logger.error(errorMessage);

                return false;
            }
            
            boolean isElementNumberValid = areAllRowsTheSameLength(parsedFileLines);
            if (!isElementNumberValid) {
                errorMessage = String.format(CSV_READER_FAILED_MESSAGE, targetFileName);
                errorMessage = errorMessage + " There is at least 1 record in the csv file that contain an unexpected number of elements.";
                logger.error(errorMessage);

                return false;
            }
        } catch (CsvException | IOException exception) {
            errorMessage = String.format(CSV_READER_FAILED_MESSAGE, targetFileName) + exception;
            logger.error(errorMessage);

            return false;
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException ioException) {
                    logger.error(ioException.getMessage());
                }
            }
        }

        return true;
    }

    /**
     * Checks if each record of the file contains the specified number of elements.
     * If there is no provided value, the element number will be the number of elements of the first record of the file.
     *
     * @param parsedFileLines all records of the file
     * @return true if each record has the correct number of elements, false - otherwise
     */
    private boolean areAllRowsTheSameLength(List<String[]> parsedFileLines) {

        int nElement;
        if (elementNumber != null && !elementNumber.isEmpty()) {
            nElement = Integer.parseInt(elementNumber);
        } else {
            nElement = parsedFileLines.get(0).length;
        }

        boolean isRowSameLength = true;
        for (int row = 0; row < parsedFileLines.size(); row++) {
            int currentLineLength = parsedFileLines.get(row).length;

            if (nElement != currentLineLength) {
                logger.error(String.format("The expected csv elements number is: %s, but the csv file has row n.%s with different elements number - %s.",
                        nElement, row+1, currentLineLength));
                isRowSameLength = false;
                //Uncomment the commented code to stop checking the file for further errors and immediately return false result.
                //return false;
            }
        }
        
        

        return isRowSameLength;
        //return true;
    }
}
